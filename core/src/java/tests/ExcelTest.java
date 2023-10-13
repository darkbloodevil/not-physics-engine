package tests;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;


import NotBox2D.ExcelReader;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.dhatim.fastexcel.reader.Cell;
import org.json.JSONArray;
import org.json.JSONObject;


//https://howtodoinjava.com/java/library/readingwriting-excel-files-in-java-poi-tutorial/
public class ExcelTest {
    static String REPRESENT = "represent", ALTER = "alter", EMPTY_TOKEN = "[EMPTY]";
    public static void main(String[] args) throws IOException {
//        System.out.println(excel_to_json("excel_test.xlsx").toString().replaceAll("],","],\n"));
//        System.out.println(ExcelReader.excel_to_json("excel_test.xlsx").toString().replaceAll("],","],\n"));
    }




//    private void write_test(){
//        //Blank workbook
//
//        XSSFWorkbook workbook = new XSSFWorkbook();
//
////Create a blank sheet
//
//        XSSFSheet sheet = workbook.createSheet("Employee Data");
//
////Prepare data to be written as an Object[]
//
//        Map<String, Object[]> data = new TreeMap<String, Object[]>();
//        data.put("1", new Object[]{"ID", "NAME", "LASTNAME"});
//        data.put("2", new Object[]{1, "Amit", "Shukla"});
//        data.put("3", new Object[]{2, "Lokesh", "Gupta"});
//        data.put("4", new Object[]{3, "John", "Adwards"});
//        data.put("5", new Object[]{4, "Brian", "Schultz"});
//
////Iterate over data and write to sheet
//
//        Set<String> keyset = data.keySet();
//        int rownum = 0;
//        for (String key : keyset) {
//
//            Row row = sheet.createRow(rownum++);
//            Object[] objArr = data.get(key);
//            int cellnum = 0;
//            for (Object obj : objArr) {
//                Cell cell = row.createCell(cellnum++);
//                if (obj instanceof String)
//                    cell.setCellValue((String) obj);
//                else if (obj instanceof Integer)
//                    cell.setCellValue((Integer) obj);
//            }
//        }
//
////Write the workbook in file system
//
//        try {
//            FileOutputStream out = new FileOutputStream(new File("howtodoinjava_demo.xlsx"));
//            workbook.write(out);
//            out.close();
//            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
