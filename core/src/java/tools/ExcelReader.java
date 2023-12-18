package tools;

import exceptions.ExcelFormatException;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * @TODO to be done
 * 需要新增多表处理
 *
 * 需要的格式为
 *
 */
public class ExcelReader {
    /**
     * tokens in result
     */
    static String GAME_MAP="game_map",HEADER="[HEADER]",REPRESENT = "[REPRESENT]", ALTER = "[ALTER]",LIST = "[LIST]", NONE="[NONE]",EMPTY_TOKEN = "[EMPTY]";

    public static void main(String[] args) {
        System.out.println(excel_to_json("excel_files/basic_game_map.xlsx").getJSONArray("game_map").length());
        System.out.println(excel_to_json("excel_files/basic_game_map.xlsx").getJSONArray("game_map").getJSONArray(0).length());
        System.out.println(excel_to_json("excel_files/basic_game_map.xlsx").getJSONArray("game_map").toString());
    }

    /**
     * 读取给定格式的excel。
     * excel格式：第一行header，width  height(一行)
     * width x height的地图区域
     * 其他信息（若干行）
     *
     * @param excel_path excel的位置
     * @return JsonObject build from the Excel
     */
    public static JSONObject excel_to_json(String excel_path) {
        var f = new File(excel_path);
        JSONObject result_json = new JSONObject();// 返回的结果
        try (var workbook = new ReadableWorkbook(f)) {
            Sheet sheet = workbook.getFirstSheet();
            handle_map_sheet(sheet,result_json);
        } catch (IOException | ExcelFormatException e) {
            throw new RuntimeException(e);
        }
        return result_json;
    }

    /**
     * 处理一张有地图的表，放入json
     * @param sheet 目标表
     * @param result_json 结果json
     * @throws IOException
     */
    static void handle_map_sheet(Sheet sheet, JSONObject result_json) throws IOException, ExcelFormatException {
        try (Stream<Row> rows = sheet.openStream()) {
            var rowIterator = rows.iterator();
            init_result_json(result_json);
            handling_rows(rowIterator, result_json, result_json.getJSONArray(GAME_MAP));
        }
    }

    /**
     * 初始化result json
     * @param result_json
     */
    static void init_result_json(JSONObject result_json){
        result_json.put(GAME_MAP, new JSONArray());//the 2 dimension array of the map
        result_json.put(REPRESENT, new JSONObject());
        result_json.put(ALTER, new JSONObject());
    }

    /**
     * handling all the rows
     *
     * @param rowIterator row iterator
     * @param result_json result
     * @param map_arr     game map array
     */
    static void handling_rows(Iterator<Row> rowIterator, JSONObject result_json, JSONArray map_arr) throws ExcelFormatException {
        String task = "";
        while (rowIterator.hasNext()) {
            var row = rowIterator.next();
            //空了就关了
            if (row.getCellCount() == 0) {
                break;
            }
            // Header Row
            if (row.getCell(0).asString().equalsIgnoreCase(HEADER))
                handling_header(row, result_json);
                //if height is already loaded
            else if (result_json.has("height") && row.getRowNum() <= result_json.getInt("height") + 1) {// read the map (+1 cause the header)
                handling_map(row, map_arr);
            }
                // read other information
            else if (result_json.has("height") && row.getRowNum() > result_json.getInt("height") + 1) {
                // set the task
                if (row.getCell(0).asString().equalsIgnoreCase(REPRESENT)) {
                    task = REPRESENT;
                    continue;
                } else if (row.getCell(0).asString().equalsIgnoreCase(ALTER)) {
                    task = ALTER;
                    continue;
                }
                // handling represent（用来标明各个符号代表了什么）
                if (task.equals(REPRESENT))
                    handling_represent(row, result_json.getJSONObject(REPRESENT));
                    // handling alter（用来标明对该符号的调整）
                else if (task.equals(ALTER)){
                    handling_alter(row, result_json.getJSONObject(ALTER));
                }
            }
        }


    }

    /**
     * 处理header
     * 找出| w | <宽度> | 和 | h | <高度> |
     *
     * @param row         row
     * @param result_json result
     */
    static void handling_header(Row row, JSONObject result_json) {
        for (int i = 0; i < row.getCellCount(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null)
                continue;
            // handling the data from header
            if (cell.getValue() instanceof String) {
                String cell_str = cell.asString();
                // width and height
                if (cell_str.equalsIgnoreCase("w")||cell_str.equalsIgnoreCase("width")) {
                    result_json.put("width", row.getCell(i + 1).asNumber());
                } else if (cell_str.equalsIgnoreCase("h")||cell_str.equalsIgnoreCase("height")) {
                    result_json.put("height", row.getCell(i + 1).asNumber());
                }
            }
        }
    }

    /**
     * 处理游戏地图
     *
     * @param row     row
     * @param map_arr game map array
     */
    static void handling_map(Row row, JSONArray map_arr) {
        JSONArray row_arr = new JSONArray();
        for (int i = 0; i < row.getCellCount(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null)
                continue;
            if (cell.getValue() == null) {
                row_arr.put(EMPTY_TOKEN);
            } else {
                row_arr.put(whatever_cell(cell));
            }
        }
        map_arr.put(row_arr);
    }

    /**
     * 用来标明各个符号代表了什么
     *
     * @param row         row
     * @param result_json result
     */
    static void handling_represent(Row row, JSONObject represent_json) {
        // 因为represent是一格key一个value，所以这边循环步长为2
        for (int i = 0; i < row.getCellCount() - 1; i += 2) {
            // key value
            Cell cell_key = row.getCell(i);
            Cell cell_value = row.getCell(i + 1);
            if (cell_key == null)
                continue;

            if (cell_key.getValue() instanceof String) {
                // string key
                String key_str = cell_key.asString();
                // value (i don't care type)
                Object value_obj = whatever_cell(cell_value);

                // add to represent
                represent_json.put(key_str, value_obj);
            }
        }
    }

    /**
     * 用来表明对符号的调整
     * 行的开头标明要更改的目标。
     * 每增加一个个值，就json多嵌套一层
     * 例如：| grandpa | shape | circle | radius | 20
     * 就是{"grandpa":{
     *     "shape":{
     *         "circle":{
     *             "radius":20
     *         }
     *     }
     * }}
     * 如果需要出现列表，用[LIST]标明
     * 例如：| fruit | apple | color | [list] | red | yellow | green
     * {
     *     "fruit":{
     *         "apple":{
     *             "color":["red","yellow","green"]
     *         }
     *     }
     * }
     * @param row         row
     * @param result_json result
     * @return true表示结束了
     */
    static void handling_alter(Row row, JSONObject alter_json) throws ExcelFormatException {
        // 因为alter的会比较复杂，所以这边一行默认只处理一个key，开头给出key
        String key = row.getCell(0).asString();
        /**
         * // value 默认为json object
         *         JSONObject value = new JSONObject();
         *         // 如果已经加载过一部分，那就在上面继续加载
         *         if (result_json.has(key)) value = result_json.getJSONObject(ALTER).getJSONObject(key);
         *             // add to alter(if not already added)
         *         else result_json.getJSONObject(ALTER).put(key, value);
         */


        // 当前在哪一层级的json中
        var current_sub_json=alter_json;
        for (int i = 0; i < row.getCellCount()-1; i++) {
            /**
             * tar_property 为当前key
             * sub_property 为当前value/子项的key
             */
            var tar_property=row.getCell(i).asString();
            var sub_property=whatever_cell(row.getCell(i+1));
            // 空值则退出
            if (key.equals("")||tar_property.equals("")||row.getCell(i+1).getText().equals(""))
                return;

            // tar_property遇到[LIST]，说明出错了
            if (tar_property.equalsIgnoreCase(LIST)){
                throw new ExcelFormatException("[LIST] should not used in the beggining!");
            }
            // sub_property 同样全部读取并且break
            if (String.valueOf(sub_property).equalsIgnoreCase(LIST)){
                var result_ja=new JSONArray();
                for (int j = i+2; j < row.getCellCount(); j++) {
                    result_ja.put(whatever_cell(row.getCell(j)));
                }
                current_sub_json.put(tar_property,result_ja);
                break;
            }
            //最后一个循环，此时的<tar_property,sub_property>就是简单<key,value>
            if (i==row.getCellCount()-2){
                if (String.valueOf(sub_property).equalsIgnoreCase(LIST)){
                    current_sub_json.put(tar_property,new JSONArray());
                    break;
                }
                current_sub_json.put(tar_property,sub_property);
                break;
            }
            // 此时未结束，所以应该认为下一步还是json object类型
            var sub_value= new JSONObject();
            // 如果已经加载过一部分，那就在上面继续加载
            if (current_sub_json.has(tar_property)){
                sub_value = current_sub_json.getJSONObject(tar_property);
            }
            current_sub_json.put(tar_property,sub_value);
            // 修改当前嵌套层级
            current_sub_json=sub_value;

        }

    }

    /**
     * I don't care the type of cell, so i return object
     *
     * @param cell_value cell value
     * @return object cell value
     */
    static Object whatever_cell(Cell cell_value) {
        if (cell_value == null) return new Object();
        if (cell_value.getValue() instanceof String) return cell_value.asString();
        else if (cell_value.getValue() instanceof BigDecimal) return cell_value.asNumber();
        else if (cell_value.getValue() instanceof Boolean) return cell_value.asBoolean();
        return new Object();
    }
}


/**
 * // 只有三个元素，说明是单个值
 *         if (row.getCellCount() < 4) {
 *             org.dhatim.fastexcel.reader.Cell cell_value = row.getCell(2);
 *             Object temp = whatever_cell(cell_value);
 *             // cellnum 1是该dict内的key
 *             value.put(row.getCell(1).asString(), temp);
 *         }// 否则 是数组
 *         else {
 *             // array
 *             JSONArray jsonArray = new JSONArray();
 *             for (int i = 2; i < row.getCellCount(); i++) {
 *                 org.dhatim.fastexcel.reader.Cell cell_value = row.getCell(i);
 *                 Object temp = whatever_cell(cell_value);
 *                 // 先放入数组
 *                 jsonArray.put(temp);
 *             }
 *             // 放入key和数组
 *             value.put(row.getCell(1).asString(), jsonArray);
 *         }
 */