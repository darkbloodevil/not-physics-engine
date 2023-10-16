package NotBox2D;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @TODO to be done
 */
public class ExcelReader {
    /**
     * tokens in result
     */
    static String REPRESENT = "represent", ALTER = "alter", EMPTY_TOKEN = "[EMPTY]";

    public static void main(String[] args) {
        System.out.println(excel_to_json("excel_test.xlsx").getJSONArray("game_map").length());
        System.out.println(excel_to_json("excel_test.xlsx").getJSONArray("game_map").getJSONArray(0).length());
    }

    /**
     * 读取给定格式的excel。
     * excel格式：第一行header，width  height(一行)
     * width x height的地图区域
     * 其他信息（若干行）
     *
     * @param excel_path excel的位置
     * @return JsonObject build from the excel
     */
    public static JSONObject excel_to_json(String excel_path) {
        var f = new File(excel_path);

        JSONObject result_json = new JSONObject();// 返回的结果
        try (var workbook = new ReadableWorkbook(f)) {
            Sheet sheet = workbook.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                var rowIterator = rows.iterator();
                JSONArray map_arr = new JSONArray();//the 2 dimension array of the map
                handling_rows(rowIterator,result_json,map_arr);
                result_json.put("game_map", map_arr);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result_json;
    }

    /**
     * handling all the rows
     * @param rowIterator row iterator
     * @param result_json result
     * @param map_arr game map array
     */
    static void handling_rows(Iterator<Row> rowIterator,JSONObject result_json,JSONArray map_arr){
        String task = "";
        while (rowIterator.hasNext()) {
            var row = rowIterator.next();
            // Header Row
            if (row.getCell(0).asString().equalsIgnoreCase("header")) {
                handling_header(row,result_json);
            }
            //if height is already loaded
            else if (result_json.has("height")) {
                // read the map (+1 cause the header)
                if (row.getRowNum() <= result_json.getInt("height") + 1) {
                    handling_map(row,map_arr);
                }
                // read other information
                else {
                    // set the task
                    if (row.getCell(0).asString().equalsIgnoreCase(REPRESENT)) {
                        task = REPRESENT;
                        result_json.put(REPRESENT, new JSONObject());
                        continue;
                    } else if (row.getCell(0).asString().equalsIgnoreCase(ALTER)) {
                        task = ALTER;
                        result_json.put(ALTER, new JSONObject());
                        continue;
                    }

                    // handling represent（用来标明各个符号代表了什么）
                    if (task.equals(REPRESENT)) {
                        handling_represent(row,result_json);
                    }
                    // handling alter（用来标明对该符号的调整）
                    else if (task.equals(ALTER)) {
                        if (handling_alter(row,result_json)){
                            break;
                        }
                    }
                }
            }

        }
    }

    /**
     * 处理header
     * @param row row
     * @param result_json result
     */
    static void handling_header(Row row,JSONObject result_json){
        for (int i = 0; i < row.getCellCount(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null)
                continue;
            // handling the data from header
            if (cell.getValue() instanceof String) {
                String cell_str = cell.asString();
                // width and height
                if (cell_str.equals("w")) {
                    result_json.put("width", row.getCell(i + 1).asNumber());
                } else if (cell_str.equals("h")) {
                    result_json.put("height", row.getCell(i + 1).asNumber());
                }
            }
        }
    }

    /**
     * 处理游戏地图
     * @param row row
     * @param map_arr game map array
     */
    static void handling_map(Row row,JSONArray map_arr){
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
     * @param row row
     * @param result_json result
     */
    static void handling_represent(Row row,JSONObject result_json){
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
                result_json.getJSONObject(REPRESENT).put(key_str, value_obj);
            }
        }
    }

    /**
     * 用来表明对符号的调整
     * @param row row
     * @param result_json result
     * @return true表示结束了
     */
    static boolean handling_alter(Row row,JSONObject result_json){
        //空了就关了
        if (row.getCellCount() == 0) {
            return true;
        }
        // 因为alter的会比较复杂，所以这边一行默认只处理一个key，开头给出key
        String key = row.getCell(0).asString();
        // value 默认为json object
        JSONObject value = new JSONObject();
        // 如果已经加载过一部分，那就在上面继续加载
        if (result_json.has(key)) value = result_json.getJSONObject(ALTER).getJSONObject(key);
            // add to alter(if not already added)
        else result_json.getJSONObject(ALTER).put(key, value);

//                                System.out.println("\nahhhhh" + row.getCellCount());
        // 只有三个元素，说明是单个值
        if (row.getCellCount() < 4) {
            org.dhatim.fastexcel.reader.Cell cell_value = row.getCell(2);
            Object temp = whatever_cell(cell_value);
            // cellnum 1是该dict内的key
            value.put(row.getCell(1).asString(), temp);
        }// 否则 是数组
        else {

            // array
            JSONArray jsonArray = new JSONArray();
            for (int i = 2; i < row.getCellCount(); i++) {
                org.dhatim.fastexcel.reader.Cell cell_value = row.getCell(i);
                Object temp = whatever_cell(cell_value);
                // 先放入数组
                jsonArray.put(temp);
            }
            // 放入key和数组
            value.put(row.getCell(1).asString(), jsonArray);
        }
        return false;
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
