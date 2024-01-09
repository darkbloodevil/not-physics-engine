package tools.cmdLine

import java.awt.{Graphics, Graphics2D}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
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
                builder.append(if (image.getRGB(x, y) == -16777216) cyan.wrap(" ")
                else if (image.getRGB(x, y) == -1) "#"
                else "*")
            }

            /**
             * @TODO 根据一定的规则，替换掉上述画的部分像素
             */

        }
        draw()
    }

    /**
     * 把string builders全部渲染
     */
    private def draw(): Unit = {
        builders.foreach((builder: StringBuilder) => println(builder))
    }
}
