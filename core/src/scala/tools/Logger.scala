package tools
import com.typesafe.scalalogging.{Logger=>ScalaLogger}

class Logger(val name:String){
    var logger:ScalaLogger=ScalaLogger(name)
    
    
    def info(message:String): Unit = {
        logger.info(message)
    }
}
