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
        JSONArray map_arr = new JSONArray();//the 2 dimension array of the map
        String task = "";
        try (var workbook = new ReadableWorkbook(f)) {
            Sheet sheet = workbook.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                var rowIterator = rows.iterator();
                while (rowIterator.hasNext()) {
                    var row = rowIterator.next();
                    // Header Row
                    if (row.getCell(0).asString().equalsIgnoreCase("header")) {
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
                    //if height is already loaded
                    else if (result_json.has("height")) {
                        // read the map (+1 cause the header)
                        if (row.getRowNum() <= result_json.getInt("height") + 1) {
                            JSONArray row_arr = new JSONArray();
                            for (int i = 0; i < row.getCellCount(); i++) {
                                org.dhatim.fastexcel.reader.Cell cell = row.getCell(i);
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
                                // 因为represent是一格key一个value，所以这边循环步长为2
                                for (int i = 0; i < row.getCellCount() - 1; i += 2) {
                                    // key value
                                    org.dhatim.fastexcel.reader.Cell cell_key = row.getCell(i);
                                    org.dhatim.fastexcel.reader.Cell cell_value = row.getCell(i + 1);
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
                            // handling alter（用来标明对该符号的调整）
                            else if (task.equals(ALTER)) {
                                //空了就关了
                                if (row.getCellCount() == 0) {
                                    break;
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
                            }

                        }
                    }

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        result_json.put("game_map", map_arr);
        return result_json;
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

//
//    /**
//     * 读取给定格式的excel。
//     * excel格式：第一行header，width  height(一行)
//     * width x height的地图区域
//     * 其他信息（若干行）
//     *
//     * @param excel_path excel的位置
//     * @return JsonObject build from the excel
//     */
//    public static JSONObject excel_to_json(String excel_path) {
//        //读取文件
//        FileInputStream file = null;
//        try {
//            file = new FileInputStream(excel_path);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        //Create Workbook instance holding reference to .xlsx file
//        XSSFWorkbook workbook = null;
//        try {
//            workbook = new XSSFWorkbook(file);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        XSSFSheet sheet = workbook.getSheetAt(0);//get first/desired sheet from the workbook
//        Iterator<Row> rowIterator = sheet.iterator();//Iterate through each rows one by one
//
//        JSONObject result_json = new JSONObject();// 返回的结果
//        JSONArray map_arr = new JSONArray();//the 2 dimension array of the map
//
//        String task = "";
//
//        while (rowIterator.hasNext()) {
//            Row row = rowIterator.next();
//
//            // Header Row
//            if (row.getCell(0).getStringCellValue().equalsIgnoreCase("header")) {
//                for (int i = 0; i < row.getLastCellNum(); i++) {
//                    Cell cell = row.getCell(i);
//                    if (cell == null)
//                        continue;
//                    // handling the data from header
//                    if (cell.getCellType() == CellType.STRING) {
//                        String cell_str = cell.getStringCellValue();
//                        // width and height
//                        if (cell_str.equals("w")) {
//                            result_json.put("width", row.getCell(i + 1).getNumericCellValue());
//                        } else if (cell_str.equals("h")) {
//                            result_json.put("height", row.getCell(i + 1).getNumericCellValue());
//                        }
//                    }
//                }
//            }
//            //if height is already loaded
//            else if (result_json.has("height")) {
//                // read the map (+1 cause the header)
//                if (row.getRowNum() < result_json.getInt("height") + 1) {
//                    JSONArray row_arr = new JSONArray();
//                    for (int i = 0; i < row.getLastCellNum(); i++) {
//                        Cell cell = row.getCell(i);
//                        if (cell == null)
//                            continue;
//
//                        switch (cell.getCellType()) {
//                            case NUMERIC -> {
//                                row_arr.put(cell.getNumericCellValue());
//                                System.out.print(cell.getNumericCellValue() + "\t");
//                            }
//                            case STRING -> {
//                                row_arr.put(cell.getStringCellValue());
//                                System.out.print(cell.getStringCellValue() + "\t");
//                            }
//                            case BLANK -> {
//                                row_arr.put(EMPTY_TOKEN);
//                                System.out.print(" " + "\t");
//                            }
//                        }
//                    }
//                    map_arr.put(row_arr);
//                }
//                // read other information
//                else {
//                    // set the task
//                    if (row.getCell(0).getStringCellValue().equalsIgnoreCase(REPRESENT)) {
//                        task = REPRESENT;
//                        result_json.put(REPRESENT, new JSONObject());
//                        continue;
//                    } else if (row.getCell(0).getStringCellValue().equalsIgnoreCase(ALTER)) {
//                        task = ALTER;
//                        result_json.put(ALTER, new JSONObject());
//                        continue;
//                    }
//
//                    // handling represent（用来标明各个符号代表了什么）
//                    if (task.equals(REPRESENT)) {
//                        // 因为represent是一格key一个value，所以这边循环步长为2
//                        for (int i = 0; i < row.getLastCellNum() - 1; i += 2) {
//                            // key value
//                            Cell cell_key = row.getCell(i);
//                            Cell cell_value = row.getCell(i + 1);
//                            if (cell_key == null)
//                                continue;
//
//                            if (cell_key.getCellType() == CellType.STRING) {
//                                // string key
//                                String key_str = cell_key.getStringCellValue();
//                                // value (i don't care type)
//                                Object value_obj = whatever_cell(cell_value);
//
//                                // add to represent
//                                result_json.getJSONObject(REPRESENT).put(key_str, value_obj);
//                            }
//                        }
//                    }
//                    // handling alter（用来标明对该符号的调整）
//                    else if (task.equals(ALTER)) {
//                        //空了就关了
//                        if (row.getZeroHeight()){
//                            break;
//                        }
//                        // 因为alter的会比较复杂，所以这边一行默认只处理一个key，开头给出key
//                        String key = row.getCell(0).getStringCellValue();
//                        // value 默认为json object
//                        JSONObject value = new JSONObject();
//                        // 如果已经加载过一部分，那就在上面继续加载
//                        if (result_json.has(key)) value = result_json.getJSONObject(ALTER).getJSONObject(key);
//                            // add to alter(if not already added)
//                        else result_json.getJSONObject(ALTER).put(key, value);
//
//                        System.out.println("\nahhhhh" + row.getLastCellNum());
//                        // 只有三个元素，说明是单个值
//                        if (row.getLastCellNum() < 4) {
//                            Cell cell_value = row.getCell(2);
//                            Object temp = whatever_cell(cell_value);
//                            // cellnum 1是该dict内的key
//                            value.put(row.getCell(1).getStringCellValue(), temp);
//                        }// 否则 是数组
//                        else {
//
//                            // array
//                            JSONArray jsonArray = new JSONArray();
//                            for (int i = 2; i < row.getLastCellNum(); i++) {
//                                Cell cell_value = row.getCell(i);
//                                Object temp = whatever_cell(cell_value);
//                                // 先放入数组
//                                jsonArray.put(temp);
//                            }
//                            // 放入key和数组
//                            value.put(row.getCell(1).getStringCellValue(), jsonArray);
//                        }
//                    }
//
//                }
//            }
//
//        }
//        result_json.put("game_map", map_arr);
//        try {
//            file.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return result_json;
//    }
//
//    /**
//     * I don't care the type of cell, so i return object
//     *
//     * @param cell_value cell value
//     * @return object cell value
//     */
//    static Object whatever_cell(Cell cell_value) {
//        if (cell_value == null) return new Object();
//        if (cell_value.getCellType() == CellType.STRING) return cell_value.getStringCellValue();
//        else if (cell_value.getCellType() == CellType.NUMERIC) return cell_value.getNumericCellValue();
//        else if (cell_value.getCellType() == CellType.BOOLEAN) return cell_value.getBooleanCellValue();
//        return new Object();
//    }