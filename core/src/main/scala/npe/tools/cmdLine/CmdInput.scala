package npe.tools.cmdLine

import org.jline.builtins.Completers
import org.jline.reader.{Completer, LineReader, LineReaderBuilder}
import org.jline.reader.impl.completer.{ArgumentCompleter, NullCompleter, StringsCompleter}
import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.widget.AutosuggestionWidgets

import java.util
import java.util.{ArrayList, HashMap, List, Map}

/**
 * 对于玩家输入进行解析
 * 例如：当前的场景是玩家面前有敌人编号（1,2,3）
 * 玩家的攻击方式有fire（对单），thunder（对群），同时玩家可以选择escape（进入其他的操作）
 * 玩家输入 fire 1，就是火焰攻击1号敌人，而如果输出fire 1 thunder，那就是玩家先攻击敌人1，然后下次要玩家输入时，自动使用thunder（相当于预先输入指令）
 *
 * 对于程序的层面，只在乎它能提供next_command
 */
class CmdInput {
    var user_inputs: util.ArrayList[String] = util.ArrayList[String]()
    // Create a terminal
    private var parser: InputParser = InputParser()
    val terminal: Terminal = TerminalBuilder.terminal
    var words: java.util.List[String] = _
    var app_name = "Cli"
    
    /**
     * 获取用户指令。对于用户一次输入了多个指令来说，程序可以逐步调用这个依次取出，直到没有任何指令后，再去请求用户新的输入。
     * 会存储words的历史记录
     * 使用默认的解析规则
     *
     * @return command data
     */
    def next_command(): CommandData = {
        if (words == null || words.size() == 0) {
            
            val lineReader: LineReader = LineReaderBuilder.builder.terminal(terminal).completer(this.parser.get_completer()).build
            val autosuggestionWidgets = new AutosuggestionWidgets(lineReader)
            autosuggestionWidgets.enable()
            val input = lineReader.readLine("%s> ".formatted(app_name))
            val temp = util.ArrayList[String]()
            temp.addAll(lineReader.getParsedLine.words)
            
            temp.remove("")
            words = temp
        } else {
            println(words)
        }
        val command_data = this.parser.get_command(words)
        // 去掉用过的部分
        if (words != null && words.size() != 0) {
            this.words = words.subList(this.parser.used_words, words.size())
        }
        
        command_data
    }
    
    /**
     * 设置解析规则，其余和next_command想通
     *
     * @return command data
     */
    def next_command(parser: InputParser): CommandData = {
        this.parser = parser
        next_command()
    }
    
    /**
     * 设置parser
     *
     * @param parser parser
     */
    def set_parser(parser: InputParser): Unit = {
        this.parser = parser
    }
}
