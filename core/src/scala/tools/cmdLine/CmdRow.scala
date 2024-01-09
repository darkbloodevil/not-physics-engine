package tools.cmdLine

/**
 * 用于画一行
 * @param renders render表
 */
class CmdRow(renders: List[CmdRender]) extends CmdRender {
    override def render(builders: IndexedSeq[StringBuilder]): Unit = {
        for render <- renders do {
            render.render(builders)
        }
    }
}
