import org.scalatest.funsuite.AnyFunSuite

import org.dhatim.fastexcel.reader.Cell
import NotBox2D.ExcelReader

//import org.junit.Test
//import org.junit.Assert._
import org.junit.jupiter.api.DisplayName

import org.junit.jupiter.api.{Test, Assertions}
class ExcelReaderTest{
//    test("NotBox2D.ExcelReader"){
//        assert(ExcelReader.excel_to_json("excel_test.xlsx")!=null,()=>"excel not null")
//    }

    @Test
    @DisplayName("testAddition aaaaaa")
    def testAddition(): Unit = {
        val result = 2 + 2
        Assertions.assertEquals(4, result,"result right")
    }
}
