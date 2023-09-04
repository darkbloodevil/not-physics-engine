package NotBox2D;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @TODO to be done
 */
public class ExcelReader {
    public static JSONObject excel_to_json(String excel_path){
        FileInputStream file = null;
        try {
            file = new FileInputStream(excel_path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
//Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Get first/desired sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);
        //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
        JSONObject result_json=new JSONObject();
        JSONArray map_arr=new JSONArray();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getCell(0).getStringCellValue().equalsIgnoreCase("header")){
                for (int i = 0; i < row.getHeight(); i++) {
                    Cell cell= row.getCell(i);
                    if (cell==null)
                        continue;
                    switch (cell.getCellType()) {
                        case STRING -> {
                            String cell_str=cell.getStringCellValue();
                            if (cell_str.equals("w")){
                                result_json.put("width",row.getCell(i+1).getNumericCellValue());
                            } else if (cell_str.equals("h")) {
                                result_json.put("height",row.getCell(i+1).getNumericCellValue());
                            }
                        }
                    }
                }
                continue;
            }
            JSONArray row_arr=new JSONArray();
            for (int i = 0; i < row.getHeight(); i++) {
                Cell cell= row.getCell(i);
                if (cell==null)
                    continue;

                switch (cell.getCellType()) {
                    case NUMERIC -> {
                        row_arr.put(cell.getNumericCellValue());
                        System.out.print(cell.getNumericCellValue() + "\t");
                    }
                    case STRING -> {
                        row_arr.put(cell.getStringCellValue());
                        System.out.print(cell.getStringCellValue() + "\t");
                    }
                    case BLANK -> {
                        row_arr.put("[EMPTY]");
                        System.out.print(" " + "\t");
                    }
                }
            }
            map_arr.put(row_arr);
        }
        result_json.put("game_map",map_arr);
        try {
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result_json;
    }
}
