package tools

import NotBox2D.ExcelReader
import org.dhatim.fastexcel.reader.Cell
import org.scalatest.funsuite.AnyFunSuite

//import org.junit.Test
//import org.junit.Assert._
import org.junit.jupiter.api.{Assertions, DisplayName, Test}
class ExcelReaderTest{
//    test("NotBox2D.ExcelReader"){
//        assert(ExcelReader.excel_to_json("excel_test.xlsx")!=null,()=>"excel not null")
//    }

    @Test
    @DisplayName("testAddition")
    def testAddition(): Unit = {
        val result = 2 + 2
        Assertions.assertEquals(4, result,"result right")
    }
}
