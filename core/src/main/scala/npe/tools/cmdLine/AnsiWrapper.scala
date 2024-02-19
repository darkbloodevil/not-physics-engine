package npe.tools.cmdLine

import AnsiWrapper.{RESET, allow_ansi}


object AnsiWrapper {
    /**
     * 是否允许ansi（因为不同环境不一样
     */
    var allow_ansi = true
    // ANSI escape codes for text colors
    val RESET = "\u001B[0m"
    val BLACK = "\u001B[30m"
    val RED = "\u001B[31m"
    val GREEN = "\u001B[32m"
    val YELLOW = "\u001B[33m"
    val BLUE = "\u001B[34m"
    val MAGENTA = "\u001B[35m"
    val CYAN = "\u001B[36m"
    val WHITE = "\u001B[37m"
    // ANSI escape codes for background colors
    val BG_BLACK = "\u001B[40m"
    val BG_RED = "\u001B[41m"
    val BG_GREEN = "\u001B[42m"
    val BG_YELLOW = "\u001B[43m"
    val BG_BLUE = "\u001B[44m"
    val BG_MAGENTA = "\u001B[45m"
    val BG_CYAN = "\u001B[46m"
    val BG_WHITE = "\u001B[47m"
    // ANSI escape codes for text styles
    val BOLD = "\u001B[1m"
    val UNDERLINE = "\u001B[4m"
    val ITALIC = "\u001B[3m"

    /**
     * 修改字体颜色（256之一）
     *
     * @param color_code 颜色编码
     * @return
     */
    def color8bit(color_code: Int): String = {
        "\u001B[38;5;" + color_code + "m"
    }

    /**
     * 修改背景颜色（256之一）
     * @param color_code 颜色编码
     * @return
     */
    def color8bit_bg(color_code: Int): String = {
        "\u001B[48;5;" + color_code + "m"
    }

    /**
     * 修改字体颜色（24bit）
     * @param red 8bit red
     * @param green 8bit green
     * @param blue 8bit blue
     * @return
     */
    def color24bit(red: Int,green:Int,blue:Int): String = {
        "\u001B[38;2;" + red+";"+green+";"+blue + "m"
    }

    /**
     * 修改字体颜色（24bit）
     * @param red 8bit red
     * @param green 8bit green
     * @param blue 8bit blue
     * @return
     */
    def color24bit_bg(red: Int,green:Int,blue:Int): String = {
        "\u001B[48;2;" + red+";"+green+";"+blue + "m"
    }
    
}

/**
 * 用来包装文本为ansi格式（主要用于Command Line版本
 *
 * @param ansi_code ansi code
 */
class AnsiWrapper(ansi_code: String) {
    private var wrapper: AnsiWrapper = _

    def this(ansi_code: String, wrapper: AnsiWrapper) = {
        this(ansi_code)
        this.wrapper = wrapper
    }

    def wrap(text: String): String = {
        // 不允许ansi，直接返回
        if (!allow_ansi) {
            return text
        }
        // 无子wrapper：那就直接包装返回
        if (this.wrapper == null) {
            return ansi_code + text + RESET
        }
        // 有子wrapper：包装子类结果
        ansi_code + this.wrapper.wrap(text)
    }

    /**
     * 将目标中的target替换为带ansi的（为了字符串格式化，直接使用ansi会导致长度被吞
     *
     * @param source 要替换的目标
     * @param target 要替换的目标
     * @return
     */
    def wrap(source: String, target: String, replace_all: Boolean = true): String = {
        val wrapped = this.wrap(target)
        if (replace_all) {
            return source.replaceAll(target, wrapped)
        }
        source.replaceFirst(target, wrapped)
    }
}
