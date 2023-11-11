package demos

import org.slf4j.LoggerFactory
//import ch.qos.logback.core.util.StatusPrinter
//import ch.qos.logback.classic.LoggerContext
import com.typesafe.scalalogging.Logger

object LogTester extends App{
    val logger = Logger(getClass.getName)
//    StatusPrinter.print((LoggerFactory.getILoggerFactory).asInstanceOf[LoggerContext])
    logger.info("vikas")
}