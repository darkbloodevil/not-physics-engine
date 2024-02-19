package npe.tools.cmdLine

import java.awt.{Graphics, Graphics2D}
import java.awt.image.BufferedImage

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

        

        val cyan: AnsiWrapper = AnsiWrapper(AnsiWrapper.BG_CYAN)
        for y <- 0 until height do {
            val builder = builders.apply(y)
            for x <- 0 until width do {
                /**
                 * @TODO 根据一定的映射规则，让graphics2D来画
                 *       注意如果要画出带ansi的，那就不可以再做替换了（会找不到位置
                 */
//                builder.append(if (image.getRGB(x, y) == -16777216) cyan.wrap(" ")
//                else if (image.getRGB(x, y) == -1) "#"
//                else "*")
            }

            /**
             * @TODO 根据一定的规则，替换掉上述画的部分像素
             */

        }

        val textBlock=CmdTextBlock(20,5)
        textBlock.update_properties("name","March 7th")
        textBlock.update_properties("sex","female")
        textBlock.update_color("name",AnsiWrapper(AnsiWrapper.RED),CmdTextBlock.VALUE_TAR)

        val textBlock2 = CmdTextBlock(10, 10)
        textBlock2.update_properties("name", "darkbloodevil")
        textBlock2.update_properties("kind", "evil")
        textBlock2.update_color("name", AnsiWrapper(AnsiWrapper.RED), CmdTextBlock.VALUE_TAR)
        textBlock2.set_symbols_order(Array("","name","-","kind","#"))

        val textBlock3:CmdTextBlock = textBlock.clone_prototype()
        textBlock3.update_properties("name", "<name>")
        textBlock3.update_properties("sex", "<sex>")

        textBlock.render(builders)
        textBlock2.render(builders)
        textBlock3.render(builders)
        draw()
    }

    /**
     * 把string builders全部渲染
     */
    private def draw(): Unit = {
        builders.foreach((builder: StringBuilder) => println(builder))
    }
}
