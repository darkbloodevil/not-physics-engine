package tools.cmdLine

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

class CmdImage(imagePath: String) {
    // 读取图片
    private val temp_image: BufferedImage = ImageIO.read(new File(imagePath))
    // 获取图片宽度和高度
    private val image_width: Int = temp_image.getWidth
    private val image_height: Int = temp_image.getHeight
    
    
    private val builders = for _ <- 0 until image_height yield StringBuilder()
    try {

        // 输出各个点的像素值
        for (y <- 0 until image_height) {
            val builder = builders.apply(y)
            for (x <- 0 until image_width) {
                if (x * 2 < temp_image.getWidth && y * 4 < temp_image.getWidth) {
                    val pixel = temp_image.getRGB(x, y * 4)
                    // 分离RGB值
                    val red = (pixel >> 16) & 0xFF
                    val green = (pixel >> 8) & 0xFF
                    val blue = pixel & 0xFF
                    val wrapper: AnsiWrapper = AnsiWrapper(AnsiWrapper.color24bit_bg(red, green, blue))
                    //                        wrapper.wrap(" ")
                    builder.append(wrapper.wrap(" "))
                }
            }
            println(builder)
        }
    } catch {
        case e: Exception =>
            e.printStackTrace()
    }
}
