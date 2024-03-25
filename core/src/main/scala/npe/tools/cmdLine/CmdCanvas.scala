package npe.tools.cmdLine

import java.awt.{Graphics, Graphics2D}
import java.awt.image.BufferedImage
import org.json.JSONArray
import java.io.File


class CmdCanvas(width: Int, height: Int) {
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics: Graphics = image.getGraphics
    val graphics2D: Graphics2D = graphics.asInstanceOf[Graphics2D]
    /**
     * StringBuilder列表
     */
    private val builders = for _ <- 0 until height yield StringBuilder()
    
    def render(): Unit = {
        // 图片文件路径// 图片文件路径
        val imagePath = "badlogic.jpg"
        
        val textBlock = CmdTextBlock(20, 5)
        textBlock.update_property("name", "March 7th")
        textBlock.update_property("sex", "female")
        textBlock.update_color("name", AnsiWrapper(AnsiWrapper.RED), CmdTextBlock.VALUE_TAR)
        
        val textBlock2 = CmdTextBlock(10, 10)
        textBlock2.update_property("name", "darkbloodevil")
        textBlock2.update_property("kind", "evil")
        textBlock2.update_color("name", AnsiWrapper(AnsiWrapper.RED), CmdTextBlock.VALUE_TAR)
        textBlock2.set_symbols_order(Array("", "name", "-", "kind", "#"))
        
        val textBlock3: CmdTextBlock = textBlock.clone_prototype()
        textBlock3.update_property("name", "<name>")
        textBlock3.update_property("sex", "<sex>")
        textBlock3.update_properties(Map("year" -> "<year>", "job" -> "<job>"))
        
        
        textBlock.render(builders)
        textBlock2.render(builders)
        textBlock3.render(builders)
        draw()
        val parser: InputParser = InputParser()
        parser.add_int_command("charge")
        parser.add_str_command("fire", Array("holy", "evil"))
        parser.add_list_command("heal")
        parser.add_list_command("eat", Array("apple", "pear"))
        val input: CmdInput = CmdInput()
        input.set_parser(parser)
        while (true) {
            println(input.next_command().as_json_object())
            
        }
    }
    
    /**
     * 把string builders全部渲染
     */
    private def draw(): Unit = {
        builders.foreach((builder: StringBuilder) => println(builder))
    }
}
