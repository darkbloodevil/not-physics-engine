package npe.tools.cmdLine

import org.json.{JSONArray, JSONObject}
import org.jline.reader.{Completer, LineReader, LineReaderBuilder}
import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.builtins.Completers
import org.jline.builtins.Completers.AnyCompleter
import org.jline.reader.impl.completer.*

import scala.jdk.CollectionConverters.*
import java.util
import org.jline.widget.AutosuggestionWidgets


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
     * }
     * }
     *
     * 通过把结构存入该json来设定
     * ps. 上述列的结构中，不要在意列表可能的重复。simply ignore it
     */
    private val parser_struct: JSONObject = new JSONObject()
    private val end_token: String = "|"
    private val ZERO_TOKEN = "zero"
    private val STR_TOKEN = "str"
    private val INT_TOKEN = "int"
    private val FLOAT_TOKEN = "float"
    private val LIST_TOKEN = "list"
    private[this] var _used_words: Int = -1
    
    def used_words: Int = _used_words
    
    
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
        // 用于标注命令的编号
        var i = 0
        // 最外层 遍历command
        while (iterator.hasNext) {
            val command = iterator.next()
            i += 1
            // 标记命令的第二阶
            var j = 0
            // 将command加入comp
            comp.put("C" + i, new StringsCompleter(command))
            // 获取command的对象
            val command_jo = this.parser_struct.getJSONObject(command)
            
            val command_iterator = command_jo.keys()
            // 遍历command 内的各个type
            while (command_iterator.hasNext) {
                // 获取command中的某个type
                val value_type = command_iterator.next()
                // command中的某个type的具体值
                val value_arr = command_jo.getJSONArray(value_type)
                val value_iterator = value_arr.iterator()
                
                var regex_temp = ""
                
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
                // 如果实际上没有取值 那就任意匹配
                if (regex_temp.equals("")) {
                    j += 1
                    comp.put("C" + i + "D" + j, new AnyCompleter)
                    regex_temp = "C" + i + "D" + j
                }
                // list 匹配Ci ( xx | xx | ... )* Cn
                if (value_type.equals(LIST_TOKEN)) {
                    regex_temp = "(" + "C" + i + "(" + regex_temp + ")* Cn)"
                    
                }
                // 普通 匹配Ci ( xx | xx | ... ) Cn
                else {
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
            if (values.get(i).equals(end_token)) {
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
    def get_command(words: util.List[String]): CommandData = {
        if (words == null || words.size() == 0) {
            this._used_words = 0
            return CommandData.null_command()
        }
        val word = words.get(0)
        if (this.parser_struct.keySet().contains(word)) {
            // 如果这一组字符串的最后一个词 zero command
            if (words.size() == 1) {
                return generator_zero_command(word)
            }
            // 下一个词
            val next_word = words.get(1)
            // 下一个词是"|" 同样zero command
            if (next_word.equals(end_token)) {
                return generator_zero_command(word)
            }
            
            // 获取parser_struct中word的json object
            val command_jo = this.parser_struct.getJSONObject(word)
            
            // 获取各类型的默认值
            val int_arr = if (command_jo.has(INT_TOKEN)) command_jo.getJSONArray(INT_TOKEN) else new JSONArray()
            val str_arr = if (command_jo.has(STR_TOKEN)) command_jo.getJSONArray(STR_TOKEN) else new JSONArray()
            val list_arr = if (command_jo.has(LIST_TOKEN)) command_jo.getJSONArray(LIST_TOKEN) else new JSONArray()
            
            //被包含在list_arr内，那就是list
            if (list_arr.toList.contains(next_word)) {
                // 切出list
                return generator_list_command(word, words.subList(1, words.size()))
            }
            //被包含在int_arr内，那就是int
            else if (next_word.toIntOption.isDefined && int_arr.toList.contains(next_word.toInt)) {
                return generator_int_command(word, next_word)
            }
            //被包含在str_arr内，那就是str
            else if (str_arr.toList.contains(next_word)) {
                return generator_str_command(word, next_word)
            }
            
            // 第一优先匹配list
            if (command_jo.has(LIST_TOKEN)) {
                // 切出list
                return generator_list_command(word, words.subList(1, words.size()))
            }
            // 第二优先匹配int
            else if (command_jo.has(INT_TOKEN) && next_word.toIntOption.isDefined) {
                return generator_int_command(word, next_word)
            }
            // 第三优先匹配float
            else if (command_jo.has(FLOAT_TOKEN) && next_word.toFloatOption.isDefined) {
                return generator_float_command(word, next_word)
            }
            // 第四优先匹配str
            else if (command_jo.has(STR_TOKEN)) {
                return generator_str_command(word, next_word)
            }
            // 再不济就zero了
            else if (command_jo.has(ZERO_TOKEN)) {
                return generator_zero_command(word)
            }
            
        } // 如果word不存在于parser中，那就直接返回zero type
        else {
            return generator_zero_command(word)
        }
        CommandData.null_command()
        
    }
    
}