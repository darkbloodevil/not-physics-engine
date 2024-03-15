package npe.tools.cmdLine

import org.json.{JSONArray, JSONObject}
import org.jline.reader.{Completer, LineReader, LineReaderBuilder}
import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.builtins.Completers
import org.jline.reader.impl.completer._
import scala.jdk.CollectionConverters.*


import java.util
import org.jline.widget.AutosuggestionWidgets


/**
 * 用于解析输入的内容。和应用绑定。注意，这边不判断用户输入合规与否
 *
 */
class InputParser() {
    /**
     * 解析结构。
     * 譬如：
     * {
     * "fire":{
     * "int":[1,2,3], --表明含有int类型，可以接受1、2、3
     * “zero”:[], --表明也可以是zero类型
     * "str":["ala"], --表明可以接受ala作为一个指定名称
     * "list":["red","blue","green"], --表明可以接受red/blue/green 作为列表的指
     * }
     * }
     *
     * 通过把结构存入该json来设定
     * ps. 上述列的结构中，不要在意列表可能的重复。simply ignore it
     */
    private val parser_struct: JSONObject = new JSONObject()
    val end_token: String = "|"
    private val ZERO_TOKEN = "zero"
    private val STR_TOKEN = "str"
    private val INT_TOKEN = "int"
    private val FLOAT_TOKEN = "float"
    private val LIST_TOKEN = "list"
    
    
    /**
     * 字面意思：把command在parser_struct给初始化一下
     *
     * @param command_name 命令名称
     */
    private def init_command_in_parser_struct(command_name: String): Unit = {
        if (!this.parser_struct.has(command_name)) {
            this.parser_struct.put(command_name, new JSONObject())
        }
    }
    
    /**
     * 加入应该无内容只有名称的指令
     *
     * @param command_name command_name
     */
    def add_zero_command(command_name: String): Unit = {
        this.init_command_in_parser_struct(command_name)
        this.parser_struct.getJSONObject(command_name).put(ZERO_TOKEN, new JSONArray())
    }
    
    /**
     * 加入字符串命令（指定value可能的取值）
     *
     * @param command_name command_name
     * @param value_names  指定value可能的的取值
     */
    def add_str_command(command_name: String, value_names: Array[String]): Unit = {
        this.add_str_command(command_name)
        val str_arr: JSONArray = this.parser_struct.getJSONObject(command_name).getJSONArray(STR_TOKEN)
        value_names.foreach((item: String) => {
            str_arr.put(item)
        })
    }
    
    /**
     * 加入字符串命令（无指定value，可以为任意string）
     *
     * @param command_name command_name
     */
    def add_str_command(command_name: String): Unit = {
        this.init_command_in_parser_struct(command_name)
        if (!this.parser_struct.getJSONObject(command_name).has(STR_TOKEN)) {
            this.parser_struct.getJSONObject(command_name).put(STR_TOKEN, new JSONArray())
        }
    }
    
    /**
     * 加入字符串命令（指定value）
     *
     * @param command_name command_name
     * @param value_range  指定value可能的的取值
     */
    def add_int_command(command_name: String, value_range: Array[Int]): Unit = {
        this.add_int_command(command_name)
        val int_arr: JSONArray = this.parser_struct.getJSONObject(command_name).getJSONArray(INT_TOKEN)
        value_range.foreach((item: Int) => {
            int_arr.put(item)
        })
    }
    
    /**
     * int 型指令
     *
     * @param command_name command_name
     */
    def add_int_command(command_name: String): Unit = {
        this.init_command_in_parser_struct(command_name)
        if (!this.parser_struct.getJSONObject(command_name).has(INT_TOKEN)) {
            this.parser_struct.getJSONObject(command_name).put(INT_TOKEN, new JSONArray())
        }
    }
    
    /**
     * float 型指令
     *
     * @param command_name command_name
     *
     */
    def add_float_command(command_name: String): Unit = {
        this.init_command_in_parser_struct(command_name)
        if (!this.parser_struct.getJSONObject(command_name).has(FLOAT_TOKEN)) {
            this.parser_struct.getJSONObject(command_name).put(FLOAT_TOKEN, new JSONArray())
        }
    }
    
    /**
     * list 型指令（带有指定类型。不过注意，对于list任意内容可以重复任意次以任意顺序）
     *
     * @param command_name command_name
     * @param value_range  指定列表中可能的的取值（不过没法决定顺序）
     */
    def add_list_command(command_name: String, value_range: Array[Any]): Unit = {
        this.add_list_command(command_name)
        val list_arr: JSONArray = this.parser_struct.getJSONObject(command_name).getJSONArray(LIST_TOKEN)
        value_range.foreach((item: Any) => {
            list_arr.put(item)
        })
    }
    
    /**
     * list 型指令
     *
     * @param command_name command_name
     */
    def add_list_command(command_name: String): Unit = {
        this.init_command_in_parser_struct(command_name)
        if (!this.parser_struct.getJSONObject(command_name).has(LIST_TOKEN)) {
            this.parser_struct.getJSONObject(command_name).put(LIST_TOKEN, new JSONArray())
        }
    }
    
    /**
     * 补全器
     */
    def get_completer(): Completers.RegexCompleter = {
        val comp: util.HashMap[String, Completer] = new util.HashMap[String, Completer]
        var regex_str = ""
        val iterator = this.parser_struct.keys()
        // 提前加入终止符号
        comp.put("Cn", new StringsCompleter(end_token))
        
        var i = 0
        // 最外层 遍历command
        while (iterator.hasNext) {
            val command = iterator.next()
            i += 1
            var j = 0
            comp.put("C" + i, new StringsCompleter(command))
            
            val command_jo = this.parser_struct.getJSONObject(command)
            val command_iterator = command_jo.keys()
            // 遍历command 内的各个type
            while (command_iterator.hasNext) {
                val value_type = command_iterator.next()
                
                val value_arr = command_jo.getJSONArray(value_type)
                val value_iterator = value_arr.iterator()
                
                var regex_temp = ""
                // list 匹配Ci ( xx | xx | ... )* Cn
                if (value_type.equals(LIST_TOKEN)) {
                    
                    // 遍历各个value可能取值 用于加入comp
                    while (value_iterator.hasNext) {
                        j += 1
                        val value = value_iterator.next().toString
                        comp.put("C" + i + "D" + j, new StringsCompleter(value))
                        if (regex_temp.equals("")) {
                            regex_temp = "C" + i + "D" + j
                        } else {
                            regex_temp += "|C" + i + "D" + j
                        }
                    }
                    regex_temp = "(" + "C" + i + "(" + regex_temp + ")* Cn)"
                }
                // 普通 匹配Ci  xx | xx | ...  Cn
                else {
                    // 遍历各个value可能取值 用于加入comp
                    while (value_iterator.hasNext) {
                        j += 1
                        val value = value_iterator.next().toString
                        comp.put("C" + i + "D" + j, new StringsCompleter(value))
                        if (regex_temp.equals("")) {
                            regex_temp = "C" + i + "D" + j
                        } else {
                            regex_temp += "|C" + i + "D" + j
                        }
                    }
                    regex_temp = "(" + "C" + i + " (" + regex_temp + ") Cn)"
                }
                if (regex_str.equals("")) {
                    regex_str = regex_temp
                } else {
                    regex_str += "|" + regex_temp
                }
            }
        }
        val regex_c: Completers.RegexCompleter = new Completers.RegexCompleter(regex_str, comp.get)
        regex_c
    }
    
    
    def get_command(terminal: Terminal): CommandData = {
        var lineReader: LineReader = LineReaderBuilder.builder.terminal(terminal).completer(this.get_completer()).build
//        val autosuggestionWidgets: AutosuggestionWidgets = new AutosuggestionWidgets(lineReader)
//        // Enable autosuggestions
//        autosuggestionWidgets.enable()
        val input = lineReader.readLine("MyApp> ")
        lineReader.getParsedLine.words.forEach((word: String) => {
            println(word)
        })
        CommandData.null_command()
    }
}