package tools.cmdLine

/**
 * 命令行文本块的类
 *
 * 形如
 * ||====================||
 * ||名称：darkbloodevil  ||
 * ||描述：not-physics-   ||
 * ||-engine的开发者      ||
 * ||===================||
 *
 */
class CmdTextBlock(width: Int, height: Int) extends CmdRender {
    /**
     * 这个text block有什么properties
     */
    var properties: Map[String, Any] = Map()
    /**
     * 某个key的颜色
     */
    var key_ansi: Map[String, AnsiWrapper] = Map()
    /**
     * 某个key对应的value的颜色
     */
    var value_ansi: Map[String, AnsiWrapper] = Map()
    
    override def render(builders: IndexedSeq[StringBuilder]): Unit = {

    }
    
    

}
