package npe.tools.cmdLine

/**
 * 渲染单元。推过builders的量来控制高度。宽度没办法靠自律了只能
 */
trait CmdRender {
   /**
    * 渲染图像。注意就算没东西，也要渲染到保证高度一致
    * @param builders string builders。通过限定个数来限定最大高度。
    */
   def render(builders: IndexedSeq[StringBuilder]): Unit
}
