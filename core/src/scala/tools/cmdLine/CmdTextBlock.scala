package tools.cmdLine

import java.util.regex._
import scala.util.control.Breaks._


object CmdTextBlock {
    val EDGE_TAR = "EDGE"
    val KEY_TAR = "KEY"
    val VALUE_TAR = "VALUE"
}

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
    private var key_ansi: Map[String, AnsiWrapper] = Map()
    /**
     * 某个key对应的value的颜色
     */
    private var value_ansi: Map[String, AnsiWrapper] = Map()
    /**
     * 边缘颜色
     */
    private var edge_ansi: AnsiWrapper = AnsiWrapper(AnsiWrapper.RESET)

    /**
     * 边缘是否可见（主要用于竖向的，横向的由symbols_order设定
     */
    private var edge_visible = true

    /**
     * 各个符号排序
     */
    var symbols_order: Array[String] = _

    /**
     * 竖向的边标记
     */
    var edge_symbol = "|"

    /**
     * 已经渲染的高度
     */
    var rendered_lines = 0

    /** *
     * 画出横向的线条
     *
     * @param builder builder
     * @param symbol  横线标记
     */
    def draw_line(builder: StringBuilder, symbol: String): Unit = {
        rendered_lines += 1
        if (this.edge_visible) {
            builder.append(this.edge_ansi.wrap(edge_symbol + symbol * this.width + edge_symbol))
        }
        else {
            builder.append(this.edge_ansi.wrap(symbol * this.width))
        }
    }

    /**
     * 计算文本长度。中文算2英文算1
     *
     * @param text 要计算长度的文本
     * @return 长度
     */
    def count_length(text: String): Int = {
        val patternChinese = Pattern.compile("[\u4e00-\u9fa5]")
        val matcherChinese = patternChinese.matcher(text)
        var chineseLength = 0
        while (matcherChinese.find) chineseLength += matcherChinese.group.length // Chinese characters are 2 bytes long
        chineseLength + text.length
    }

    /**
     * 获取包装后的边缘
     */
    def get_wrapped_edge(): String = {
        if (edge_visible) {
            return this.edge_ansi.wrap(edge_symbol)
        }
        ""
    }


    /**
     * 将string截断使得满足长度要求
     *
     * @param text 要截断的string
     * @return 截断的位置
     */
    def cut_string(text: String): Int = {
        val patternChinese = Pattern.compile("[\u4e00-\u9fa5]")
        var current_len = 0
        var current_index = 0
        for i <- text.toCharArray do {
            current_len += 1
            val matcherChinese = patternChinese.matcher(String.valueOf(i))
            if (matcherChinese.find()) {
                current_len += 1
            }
            if (current_len < width) {
                current_index += 1
            } else {
                return current_index
            }
        }
        current_index
    }

    /**
     * 绘制多行
     *
     * @param text             文本
     * @param color            颜色
     * @param builder_iterator 迭代器
     */
    def multiline_render(text: String, color: AnsiWrapper, builder_iterator: Iterator[StringBuilder]): Unit = {
        if (!builder_iterator.hasNext) {
            return
        }
        if (rendered_lines >= height) {
            return
        }
        rendered_lines += 1
        val builder = builder_iterator.next()
        var current = "%-" + width + "s"
        if (count_length(text) > width) {
            val cut_index = cut_string(text)
            val head = text.substring(0, cut_index)
            val tail = text.substring(cut_index)
            current = current.format(head)
            // 递归调用
            multiline_render(tail, color, builder_iterator)
        } else {
            current = current.format(text)
        }
        builder.append(get_wrapped_edge() + color.wrap(current) + get_wrapped_edge())
    }

    /**
     * 画某个属性
     *
     * @param key              key
     * @param builder_iterator 迭代器
     */
    def draw_property(key: String, builder_iterator: Iterator[StringBuilder]): Unit = {
        val value = String.valueOf(this.properties.apply(key))
        val key_len = count_length(key)
        val value_len = count_length(value)
        if (!builder_iterator.hasNext) {
            return
        }
        val builder = builder_iterator.next()
        if (rendered_lines >= height) {
            return
        }
        if (key_len + value_len + 2 < width) {
            var temp_str = "%-" + width + "s"
            temp_str = temp_str.format(key + ": " + value)
            // 上色
            temp_str = temp_str.replaceFirst(key, key_ansi.getOrElse(key, AnsiWrapper(AnsiWrapper.RESET)).wrap(key))
            temp_str = temp_str.replaceFirst(value, value_ansi.getOrElse(key, AnsiWrapper(AnsiWrapper.RESET)).wrap(value))
            // 放入builder
            builder.append(get_wrapped_edge() + temp_str + get_wrapped_edge())
            rendered_lines += 1
        } else {
            var temp_str = "%-" + width + "s"
            // 长度不够：只放key
            temp_str = temp_str.format(key + ": ")
            // 上色
            temp_str = temp_str.replaceFirst(key, key_ansi.getOrElse(key, AnsiWrapper(AnsiWrapper.RESET)).wrap(key))
            // 放入builder
            builder.append(get_wrapped_edge() + temp_str + get_wrapped_edge())
            rendered_lines += 1
            //调用绘制多行
            multiline_render(value, value_ansi.getOrElse(key, AnsiWrapper(AnsiWrapper.RESET)), builder_iterator)
        }
    }

    /**
     * 不考虑顺序的绘制
     *
     * @param builder_iterator builder迭代器
     */
    def render_disordered(builder_iterator: Iterator[StringBuilder]): Unit = {
        if (edge_visible) {
            if (!builder_iterator.hasNext) {
                return
            }
            draw_line(builder_iterator.next(), "=")
        }
        for key <- this.properties.keys do {
            draw_property(key, builder_iterator)
        }
        fill_block(builder_iterator)
        if (edge_visible) {
            if (!builder_iterator.hasNext || rendered_lines >= height) {
                return
            }
            draw_line(builder_iterator.next(), "=")
        }
    }

    /**
     * 考虑顺序的绘制
     *
     * @param builder_iterator builder迭代器
     */
    def render_ordered(builder_iterator: Iterator[StringBuilder]): Unit = {
        var last_line = ""
        var drew_line = true
        for symbol <- symbols_order do {
            if (!drew_line) {
                draw_line(builder_iterator.next(), last_line)
                drew_line = true
            }
            if (!builder_iterator.hasNext || rendered_lines >= height) {
                return
            }
            if (this.properties.contains(symbol)) {
                draw_property(symbol, builder_iterator)
            } else {
                // 如果线在最后，那要等到高度够了才算画完，所以要先加载后画
                if (symbol.equals("")) {
                    last_line = "="
                } else {
                    last_line = symbol
                }
                drew_line = false
            }
        }
        if (!builder_iterator.hasNext || rendered_lines >= height) {
            return
        }
        // 填充block
        if (!drew_line) {
            fill_block(builder_iterator)
            draw_line(builder_iterator.next(), last_line)
            drew_line = true
        }
    }


    /**
     * 将block填满
     *
     * @param builder_iterator 迭代器
     */
    def fill_block(builder_iterator: Iterator[StringBuilder]): Unit = {
        while (builder_iterator.hasNext && rendered_lines < height - 1) {
            builder_iterator.next().append(get_wrapped_edge() + " " * width + get_wrapped_edge())
            rendered_lines += 1
        }
    }

    /**
     * 将剩余的空间填满
     *
     * @param builder_iterator 迭代器
     */
    def fill_none(builder_iterator: Iterator[StringBuilder]): Unit = {
        while (builder_iterator.hasNext) {
            if (edge_visible) {
                builder_iterator.next().append(" " * (2 + width))
            } else {
                builder_iterator.next().append(" " * width)
            }
        }
    }

    override def render(builders: IndexedSeq[StringBuilder]): Unit = {
        val builder_iterator = builders.iterator
        rendered_lines = 0
        if (symbols_order == null) {
            render_disordered(builder_iterator)

        } else {
            render_ordered(builder_iterator)
        }
        fill_none(builder_iterator)
    }

    /**
     * 更新属性
     *
     * @param key   属性的key
     * @param value 属性的value
     */
    def update_properties(key: String, value: Any): Unit = {
        this.properties = this.properties + (key -> value)
    }

    /**
     * 删除属性
     *
     * @param key 属性的key
     */
    def remove_properties(key: String): Unit = {
        this.properties = this.properties - key
    }

    /**
     * 更新颜色
     *
     * @param key         目标key
     * @param color       颜色
     * @param target_type 要改什么的颜色
     */
    def update_color(key: String, color: AnsiWrapper, target_type: String): Unit = {
        if (target_type.equals(CmdTextBlock.EDGE_TAR)) {
            this.edge_ansi = color
        } else if (target_type.equals(CmdTextBlock.KEY_TAR)) {
            this.key_ansi = this.key_ansi + (key -> color)
        } else if (target_type.equals(CmdTextBlock.VALUE_TAR)) {
            this.value_ansi = this.value_ansi + (key -> color)
        }
    }

    /**
     * 设置边缘是否可见
     *
     * @param visible 边缘可见性
     */
    def set_edge_visible(visible: Boolean): Unit = {
        this.edge_visible = visible
    }

    /**
     * 设置符号顺序
     */
    def set_symbols_order(symbols_order: Array[String]): Unit = {
        this.symbols_order = symbols_order
    }
}
