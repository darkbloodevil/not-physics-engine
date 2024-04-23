package npe.cmdLine


import org.json.{JSONArray, JSONObject}
import org.jline.reader.{Completer, LineReader, LineReaderBuilder}
import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.builtins.Completers
import org.jline.builtins.Completers.AnyCompleter
import org.jline.reader.impl.completer.*
import org.jline.widget.AutosuggestionWidgets

import scala.util.control.Breaks.*
import scala.jdk.CollectionConverters.*
import java.util
import npe.tools.JsonToolkits

object InputParser {
    val end_token: String = "|"
    val ZERO_TOKEN = "zero"
    val STR_TOKEN = "str"
    val INT_TOKEN = "int"
    val FLOAT_TOKEN = "float"
    val LIST_TOKEN = "list"
    val SPECIALS_TOKEN = "specials_token"
    // 特色的符号 放于specials中
    /**
     * 表明该列表的元素都只出现一次
     */
    val UNIQUE_LIST = "unique_list"
}

/**
 * 用于解析输入的内容。和应用绑定。注意，这边不判断用户输入合规与否
 *
 */
class InputParser {
    /**
     * 解析结构。
     * 譬如：
     * {
     * "fire":{
     * "int":[1,2,3], --表明含有int类型，可以接受1、2、3
     * “zero”:[], --表明也可以是zero类型
     * "str":["ala"], --表明可以接受ala作为一个指定名称
     * "list":["red","blue","green"], --表明可以接受red/blue/green 作为列表的指
     * "specials":{xx,xx}, --特殊符号集合
     * }
     * }
     *
     * 通过把结构存入该json来设定
     * ps. 上述列的结构中，不要在意列表可能的重复。simply ignore it
     */
    private var parser_struct: JSONObject = new JSONObject()
    
    private[this] var _used_words: Int = -1
    
    def used_words: Int = _used_words
    
    
    /**
     * 字面意思：把command在parser_struct给初始化一下。
     * 使得存在"command":{"specials":{}}
     *
     * @param command_name 命令名称
     */
    private def init_command_in_parser_struct(command_name: String): Unit = {
        if (!this.parser_struct.has(command_name)) {
            this.parser_struct.put(command_name, new JSONObject())
            this.parser_struct.getJSONObject(command_name).put(InputParser.SPECIALS_TOKEN, new JSONObject())
        }
    }
    
    /**
     * 将特殊符号加入某个command的special符号当中
     *
     * @param command_name 命令名称
     * @param name         special名称
     * @param content      内容
     */
    private def put_special(command_name: String, name: String, content: Object): Unit = {
        this.parser_struct.getJSONObject(command_name).getJSONObject(InputParser.SPECIALS_TOKEN).put(name, content)
    }
    
    /**
     * 加入应该无内容只有名称的指令
     *
     * @param command_name command_name
     */
    def add_zero_command(command_name: String): Unit = {
        this.init_command_in_parser_struct(command_name)
        this.parser_struct.getJSONObject(command_name).put(InputParser.ZERO_TOKEN, new JSONArray())
    }
    
    /**
     * 加入字符串命令（指定value可能的取值）
     *
     * @param command_name command_name
     * @param value_names  指定value可能的的取值
     */
    def add_str_command(command_name: String, value_names: Array[String]): Unit = {
        this.add_str_command(command_name)
        val str_arr: JSONArray = this.parser_struct.getJSONObject(command_name).getJSONArray(InputParser.STR_TOKEN)
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
        if (!this.parser_struct.getJSONObject(command_name).has(InputParser.STR_TOKEN)) {
            this.parser_struct.getJSONObject(command_name).put(InputParser.STR_TOKEN, new JSONArray())
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
        val int_arr: JSONArray = this.parser_struct.getJSONObject(command_name).getJSONArray(InputParser.INT_TOKEN)
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
        if (!this.parser_struct.getJSONObject(command_name).has(InputParser.INT_TOKEN)) {
            this.parser_struct.getJSONObject(command_name).put(InputParser.INT_TOKEN, new JSONArray())
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
        if (!this.parser_struct.getJSONObject(command_name).has(InputParser.FLOAT_TOKEN)) {
            this.parser_struct.getJSONObject(command_name).put(InputParser.FLOAT_TOKEN, new JSONArray())
        }
    }
    
    /**
     * list 型指令（带有指定类型。不过注意，对于list任意内容可以重复任意次以任意顺序）
     * 额外添加一个special token: unique_list=true
     *
     * @param command_name command_name
     * @param value_range  指定列表中可能的的取值（不过没法决定顺序）
     */
    def add_unique_list_command(command_name: String, value_range: Array[Any]): Unit = {
        this.add_list_command(command_name, value_range)
        this.put_special(command_name, InputParser.UNIQUE_LIST, java.lang.Boolean.TRUE)
    }
    
    /**
     * list 型指令（带有指定类型。不过注意，对于list任意内容可以重复任意次以任意顺序）
     *
     * @param command_name command_name
     * @param value_range  指定列表中可能的的取值（不过没法决定顺序）
     */
    def add_list_command(command_name: String, value_range: Array[Any]): Unit = {
        this.add_list_command(command_name)
        val list_arr: JSONArray = this.parser_struct.getJSONObject(command_name).getJSONArray(InputParser.LIST_TOKEN)
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
        if (!this.parser_struct.getJSONObject(command_name).has(InputParser.LIST_TOKEN)) {
            this.parser_struct.getJSONObject(command_name).put(InputParser.LIST_TOKEN, new JSONArray())
        }
    }
    
    /**
     * 传入一个json，然后解析出各类command格式（其实就是加入parser_struct）
     * 例如：
     * {
     * "fire":{
     * "int":[1,2,3], --表明含有int类型，可以接受1、2、3
     * “zero”:[], --表明也可以是zero类型
     * "str":["ala"], --表明可以接受ala作为一个指定名称
     * "list":["red","blue","green"], --表明可以接受red/blue/green 作为列表的指
     * }
     * }
     *
     * @param commands 用于解析的json
     */
    def add_commands(commands: JSONObject): Unit = {
        JsonToolkits.append_json(this.parser_struct, commands)
    }
    
    /**
     * 重置parser_struct
     * 格式例如】
     * {
     * "fire":{
     * "int":[1,2,3], --表明含有int类型，可以接受1、2、3
     * “zero”:[], --表明也可以是zero类型
     * "str":["ala"], --表明可以接受ala作为一个指定名称
     * "list":["red","blue","green"], --表明可以接受red/blue/green 作为列表的指
     * }
     * }
     *
     * @param parser_struct new parser struct
     */
    def set_parser_struct(parser_struct: JSONObject): Unit = {
        this.parser_struct = parser_struct
    }
    
    /**
     * 返回parser_struct
     * @return parser_struct
     */
    def get_parser_struct(): JSONObject = {
        this.parser_struct
    }
    
    /**
     * 清理掉parser_struct
     */
    def clear(): Unit = {
        this.parser_struct.clear()
    }
    
    /**
     * 生成list command data
     *
     * @param name   name
     * @param values 要求输入的是name以后的内容，并且全部输入进来。这边会对"|"进行判定
     * @return command data
     */
    private def generator_list_command(name: String, values: util.List[String]): CommandData = {
        for (i <- 0 until values.size()) {
            // 遇到end token，那就提前打断截止value。并且跳过"|"
            if (values.get(i).equals(InputParser.end_token)) {
                _used_words = 2 + i
                return CommandData(name, CommandData.LIST_TYPE, values.subList(0, i).toArray)
                
            }
        }
        // 未遇到end token 那就一路用完values
        _used_words = 1 + values.size()
        CommandData(name, CommandData.LIST_TYPE, values.toArray)
    }
    
    /**
     * 生成int类型的command data
     *
     * @param name  name
     * @param value int值 会被转换（但是不做正确性判断
     * @return command data
     */
    private def generator_int_command(name: String, value: String): CommandData = {
        this._used_words = 2
        CommandData(name, CommandData.INT_TYPE, value.toInt)
    }
    
    /**
     * 生成float类型的command data
     *
     * @param name  name
     * @param value float值 会被转换（但是不做正确性判断
     * @return command data
     */
    private def generator_float_command(name: String, value: String): CommandData = {
        this._used_words = 2
        CommandData(name, CommandData.FLOAT_TYPE, value.toFloat)
    }
    
    /**
     * 生成string类型的command data
     *
     * @param name  name
     * @param value value
     * @return command data
     */
    private def generator_str_command(name: String, value: String): CommandData = {
        this._used_words = 2
        CommandData(name, CommandData.STRING_TYPE, value)
    }
    
    /**
     * 生成zero类型的command data
     *
     * @param name name
     * @return command data
     */
    private def generator_zero_command(name: String): CommandData = {
        this._used_words = 1
        CommandData(name, CommandData.ZERO_TYPE, "")
    }
    
    /**
     * 获取当前用户输入的命令。
     * 解析优先级是：1.出现对应符号2.最长。
     * 首先检查list:[xxx] int:[xxx] str:[xxx] \ 中是否出现对应项
     *
     * 没有只要有能匹配list，那除非遇到"|"或终止就一路一直匹配
     * 不能匹配list，那就按int float string zero的顺序匹配
     *
     * 同时修改本地的_used_words
     *
     * @return Command
     */
    def get_command(words: util.List[String]): Option[CommandData] = {
        if (words == null || words.size() == 0) {
            this._used_words = 0
            return Option[CommandData](null)
        }
        val word = words.get(0)
        if (this.parser_struct.keySet().contains(word)) {
            // 如果这一组字符串的最后一个词 zero command
            if (words.size() == 1) {
                return Option[CommandData](generator_zero_command(word))
            }
            // 下一个词
            val next_word = words.get(1)
            // 下一个词是"|" 同样zero command
            if (next_word.equals(InputParser.end_token)) {
                return Option[CommandData](generator_zero_command(word))
            }
            
            // 获取parser_struct中word的json object
            val command_jo = this.parser_struct.getJSONObject(word)
            
            // 获取各类型的默认值
            val int_arr = if (command_jo.has(InputParser.INT_TOKEN)) command_jo.getJSONArray(InputParser.INT_TOKEN) else new JSONArray()
            val str_arr = if (command_jo.has(InputParser.STR_TOKEN)) command_jo.getJSONArray(InputParser.STR_TOKEN) else new JSONArray()
            val list_arr = if (command_jo.has(InputParser.LIST_TOKEN)) command_jo.getJSONArray(InputParser.LIST_TOKEN) else new JSONArray()
            
            //被包含在list_arr内，那就是list
            if (list_arr.toList.contains(next_word)) {
                // 切出list
                return Option[CommandData](generator_list_command(word, words.subList(1, words.size())))
            }
            //被包含在int_arr内，那就是int
            else if (next_word.toIntOption.isDefined && int_arr.toList.contains(next_word.toInt)) {
                return Option[CommandData](generator_int_command(word, next_word))
            }
            //被包含在str_arr内，那就是str
            else if (str_arr.toList.contains(next_word)) {
                return Option[CommandData](generator_str_command(word, next_word))
            }
            
            // 第一优先匹配list
            if (command_jo.has(InputParser.LIST_TOKEN)) {
                // 切出list
                return Option[CommandData](generator_list_command(word, words.subList(1, words.size())))
            }
            // 第二优先匹配int
            else if (command_jo.has(InputParser.INT_TOKEN) && next_word.toIntOption.isDefined) {
                return Option[CommandData](generator_int_command(word, next_word))
            }
            // 第三优先匹配float
            else if (command_jo.has(InputParser.FLOAT_TOKEN) && next_word.toFloatOption.isDefined) {
                return Option[CommandData](generator_float_command(word, next_word))
            }
            // 第四优先匹配str
            else if (command_jo.has(InputParser.STR_TOKEN)) {
                return Option[CommandData](generator_str_command(word, next_word))
            }
            // 再不济就zero了
            else if (command_jo.has(InputParser.ZERO_TOKEN)) {
                return Option[CommandData](generator_zero_command(word))
            }
            
        } // 如果word不存在于parser中，那就直接返回zero type
        else {
            return Option[CommandData](generator_zero_command(word))
        }
        Option[CommandData](CommandData.null_command())
        
    }
    
}