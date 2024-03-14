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
    parser.add_str_command("fire",Array("holy","evil"))
    
    /**
     * 获取用户指令。对于用户一次输入了多个指令来说，程序可以逐步调用这个依次取出，直到没有任何指令后，再去请求用户新的输入。
     * 使用默认的解析规则
     */
    def next_command(): Unit = {
        val terminal = TerminalBuilder.terminal
        this.parser.get_command(terminal)
        
    }
    
}
