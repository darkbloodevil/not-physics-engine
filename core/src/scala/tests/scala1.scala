package tests

//import NotBox2D.JsonReader





object scala1 {
    def main(args: Array[String]): Unit = {
//        println(JsonReader.read_json_from_path("test_json.json"))
        val ala=closure(2)
        println(ala(1))

    }
    def closure(y:Int): Int => Int ={
        val f = (x: Int) => {
            x + y
        }
        f
    }


}
