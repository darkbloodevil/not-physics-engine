import org.scalatest.funsuite.AnyFunSuite

import org.dhatim.fastexcel.reader.Cell
import NotBox2D.ExcelReader
class ExcelReaderTest extends AnyFunSuite{
    test("NotBox2D.ExcelReader"){
        assert(ExcelReader.excel_to_json("excel_test.xlsx")!=null)
    }
}
