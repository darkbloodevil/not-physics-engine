package demos



object map_demo {
    def main(args: Array[String]): Unit = {
        def map[A](f: (Int) => A, xs: List[Int]): List[A] = {
            for (x <- xs) yield f(x)
        }
        def double(i : Int): Int = i * 2
        print(map(double, List(1, 2, 3)))
    }
}