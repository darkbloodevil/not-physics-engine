package npe.tools.cmdLine

import java.util

/**
 * RenderManager
 */
class RenderManager extends CmdRender {
    private val render_map: Map[String, CmdRender] = Map[String, CmdRender]()
    private val order_list: util.ArrayList[String] = util.ArrayList[String]();
    
    /**
     * 渲染存储的所有renderer
     *
     * @param builders string builders。通过限定个数来限定最大高度。
     */
    override def render(builders: IndexedSeq[StringBuilder]): Unit = {
        order_list.forEach((r: String) => {
            render_map(r).render(builders)
            
        })
    }
    
    
}
