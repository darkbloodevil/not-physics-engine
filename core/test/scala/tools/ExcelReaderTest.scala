package tools

import org.dhatim.fastexcel.reader.Cell
import org.scalatest.funsuite.AnyFunSuite
import org.dhatim.fastexcel.reader.Cell
import org.dhatim.fastexcel.reader.ReadableWorkbook
import org.dhatim.fastexcel.reader.Row
import org.dhatim.fastexcel.reader.Sheet
import org.dhatim.fastexcel.Worksheet
import org.json.{JSONArray, JSONObject}
import org.scalatest.Assertions.*
//import org.junit.Test
//import org.junit.Assert._
import org.junit.jupiter.api.{Assertions, DisplayName, Test}
class ExcelReaderTest{
//    test("NotBox2D.ExcelReader"){
//        assert(ExcelReader.excel_to_json("excel_test.xlsx")!=null,()=>"excel not null")
//    }
//
//    @Test
//    @DisplayName("testAddition")
//    def testAddition(): Unit = {
//        val result = 2 + 2
//        Assertions.assertEquals(4, result,"result right")
//    }

    @Test
    @DisplayName("test_excel_to_json")
    def test_excel_to_json(): Unit = {
        val result=ExcelReader.excel_to_json("excel_test.xlsx")
        Assertions.assertTrue(result.has(ExcelReader.ALTER))
        val alter_json=result.getJSONObject(ExcelReader.ALTER)
        Assertions.assertTrue(alter_json.getJSONObject("A").getJSONObject("shape").
            getJSONObject("circle").getInt("radius")==20, "radius = 20")
        Assertions.assertTrue(alter_json.getJSONObject("A").getJSONObject("shape")
            .getString("dimension").equals("3d"), "dimension is 3d")

        Assertions.assertTrue(alter_json.getJSONObject("B").get("seed").isInstanceOf[JSONArray], "seed is array")
        Assertions.assertTrue(alter_json.getJSONObject("GA").getJSONArray("names").length()==2 , "has two names")


    }
}
