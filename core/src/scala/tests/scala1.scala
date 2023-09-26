package tests

import NotBox2D.JsonReader

object scala1 {
    def main(args: Array[String]): Unit = {
        println(JsonReader.read_json_from_path("test_json.json"))
    }
}
