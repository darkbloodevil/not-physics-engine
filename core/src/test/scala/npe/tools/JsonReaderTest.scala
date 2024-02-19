package npe.tools

import npe.tools.JsonReader
import org.json.JSONObject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.{Assertions, Test}
import org.scalatest.Assertions.*

class JsonReaderTest{
    @DisplayName("test_read_json_from_path")
    @Test
    def test_read_json_from_path(): Unit = {
        val json_result=JsonReader.read_json_from_path("test_json.json")
        Assertions.assertTrue(json_result.getJSONObject("grandfather").getString("body_type").equals("dynamic"),"grandfather is dynamic")
        Assertions.assertTrue(!json_result.getJSONObject("daughter").has("shape"), "daughter not extends yet")

    }
    @DisplayName("test_read_str_json")
    @Test
    def test_read_str_json(): Unit = {

        assertThrows[RuntimeException] { // Result type: Assertion
            JsonReader.read_str_json(
                """
                  |{
                  |  "grandfather": {
                  |    "extend": [
                  |      "father"
                  |    ]
                  |  },
                  |  "father": {
                  |    "extend": "grandfather",
                  |    "shape": "circle",
                  |    "radius": 10
                  |  }
                  |}
                  |""".stripMargin)
        }
        try{
            val json_result = JsonReader.read_str_json(
                """
                  |{
                  |  "grandfather": {
                  |    "body_type": "dynamic"
                  |  },
                  |  "father": {
                  |    "extend": [
                  |      "grandfather"
                  |    ],
                  |    "shape": "circle",
                  |    "radius": 10
                  |  },
                  |  "mother": {
                  |    "shape": "matrix",
                  |    "radius": 20
                  |  },
                  |  "daughter": {
                  |    "extend": ["mother","father"]
                  |  }
                  |}
                  |""".stripMargin)
            print(json_result)
            // 测试继承
            Assertions.assertTrue(json_result.getJSONObject("daughter").has("shape"), "daughter extends")
            // 测试继承祖父
            Assertions.assertTrue(json_result.getJSONObject("daughter").getString("body_type").equals("dynamic"), "daughter extends grandpa")
            // 测试继承顺序
            Assertions.assertTrue(json_result.getJSONObject("daughter").getString("shape").equals("matrix"), "daughter extends mother first then father")
        }
        catch{
            case e: RuntimeException => e.printStackTrace()
        }
            
            
            
        
    }
    @DisplayName("test_merge_json")
    @Test
    def test_merge_json(): Unit = {
        val ja=new JSONObject(
            """
              |{
              |     "circle":20,
              |     "square":30
              |}
              |""".stripMargin)
        val jb = new JSONObject(
            """
              |{
              |     "circle":30,
              |     "triangle":30
              |}
              |""".stripMargin)
        val merge_result=JsonReader.merge_json(ja,jb)
        Assertions.assertTrue(merge_result.has("square"),"has ja")
        Assertions.assertTrue(merge_result.has("triangle"),"has jb")
        Assertions.assertTrue(merge_result.getInt("circle")==30,"jb override ja")
    }
}
