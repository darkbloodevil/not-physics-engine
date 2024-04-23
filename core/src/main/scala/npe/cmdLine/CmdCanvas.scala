package npe.cmdLine

import java.awt.{Graphics, Graphics2D}
import java.awt.image.BufferedImage
import org.json.JSONArray
import scala.jdk.CollectionConverters._

import java.io.File
import java.util

/**
 * cmd的画布。
 * 提供add_renderer()来添加渲染器，然后render()渲染
 *
 * @param width  宽
 * @param height 高
 */
class CmdCanvas(width: Int, height: Int) {
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics: Graphics = image.getGraphics
    val graphics2D: Graphics2D = graphics.asInstanceOf[Graphics2D]
    private val renderer_list: java.util.List[CmdRenderer] = util.ArrayList[CmdRenderer]()
    
    /**
     * StringBuilder列表
     */
    private val builders = for _ <- 0 until height yield StringBuilder()
    
    def render(): Unit = {
        clean_canvas()
        for renderer <- renderer_list.asScala do {
            renderer.render(builders)
        }
        draw()
    }
    
    /**
     * 清理画布
     */
    def clean_canvas(): Unit = {
        for builder <- builders do {
            builder.clear()
        }
    }
    
    /**
     * 清理掉所有renders
     */
    def clean_renders(): Unit = {
        this.renderer_list.clear()
    }
    
    def add_renderer(renderer: CmdRenderer): Unit = {
        this.renderer_list.add(renderer)
    }
    
    def get_renderers(): util.List[CmdRenderer] = {
        this.renderer_list
    }
    
    /**
     * 把string builders全部渲染
     */
    private def draw(): Unit = {
        builders.foreach((builder: StringBuilder) => println(builder))
    }
}
