package tools.cmdLine

/**
 * 渲染单元。推过builders的量来控制高度。宽度没办法靠自律了只能
 */
trait CmdRender {
   def render(builders: IndexedSeq[StringBuilder]): Unit
}
