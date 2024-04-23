package npe.cmdLine.completers

import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine
import org.jline.reader.impl.completer.StringsCompleter
import org.json.JSONArray
import org.json.JSONObject
import java.util
import java.util.Collections
import npe.cmdLine.InputParser

import scala.jdk.CollectionConverters._

class ArgumentLine(_word: String, _cursor: Int) extends ParsedLine {
    
    override def word(): String = _word
    
    override def cursor(): Int = _cursor
    override def wordCursor: Int = _cursor
    
    override def wordIndex = 0
    
    override def words: util.List[String] = Collections.singletonList(word)
    
    override def line: String = word
}

class JSONCompleter(private val parser_struct: JSONObject) extends Completer {
    override def complete(reader: LineReader, line: ParsedLine, candidates: util.List[Candidate]): Unit = {
        val words = line.words.subList(0, line.wordIndex)
        // 标记words读到哪里。由0逐渐指向用户要输入的那格
        var index = 0
        var current_word = ""
        // 用来判断是否解析的是parser_struct的key
        var is_parser_key = words.isEmpty
        while (true) {
            // 起始(或者终结)
            if (words.isEmpty || (is_parser_key && index == words.size)) {
                for (parser_key <- parser_struct.keySet.asScala) {
                    val completer = new StringsCompleter(parser_key)
                    completer.complete(reader, new ArgumentLine(line.word, line.wordCursor), candidates)
                }
                // 如果是words空 那就break
                return
                
            }
            // 走一格子，获取对应的word并且index++
            if (index != words.size) current_word = words.get(index)
            index += 1
            // 如果当前的词语是parser_struct的key（也就是command名称）
            if (parser_struct.has(current_word)) {
                // 当前的命令
                val current_struct = parser_struct.getJSONObject(current_word)
                val specials = if (current_struct.has(InputParser.SPECIALS_TOKEN)) current_struct.getJSONObject(InputParser.SPECIALS_TOKEN)
                else new JSONObject
                // 如果完了
                if (index == words.size) {
                    // 把当前命令的子项尽数输出
                    for (type_key <- current_struct.keySet.asScala) {
                        //对于非special处理（以及第一次出现list的处理）
                        if (!(type_key == InputParser.SPECIALS_TOKEN)) {
                            val values = current_struct.getJSONArray(type_key)
                            for (value <- values.toList.asScala) {
                                val completer = new StringsCompleter(String.valueOf(value))
                                completer.complete(reader, new ArgumentLine(line.word, line.wordCursor), candidates)
                            }
                        }
                    }
                    // 有可能在此结束，加入end_token
                    val end_completer = new StringsCompleter(InputParser.end_token)
                    end_completer.complete(reader, new ArgumentLine(line.word, line.wordCursor), candidates)
                    return //todo: break is not supported
                    
                }
                else {
                    var is_end_token = false
                    // 对于是list
                    if (current_struct.has(InputParser.LIST_TOKEN)) {
                        // 先获取当前出现了的element
                        val elements = new util.ArrayList[String]
                        var is_break = false
                        while (index < words.size && !is_break) {
                            val element = words.get(index)
                            elements.add(element)
                            if (element == InputParser.end_token) {
                                is_end_token = true
                                is_break = true //todo: break is not supported
                                
                            } else {
                                index += 1
                            }
                            
                            
                        }
                        // 出现end_token，开新的命令（注意 出现is_end_token就不会超）
                        if (is_end_token) {
                            is_parser_key = true
                        } else {
                            // 如果是unique_list
                            if (specials.has(InputParser.UNIQUE_LIST) && specials.getBoolean(InputParser.UNIQUE_LIST)) {
                                val parser_elements = current_struct.getJSONArray(InputParser.LIST_TOKEN).toList
                                // 从parser中逐步找出哪一些是被用过哪些没有
                                for (pe <- parser_elements.iterator().asScala) {
                                    val value = pe.asInstanceOf[String]
                                    if (!elements.contains(value)) {
                                        val completer = new StringsCompleter(String.valueOf(value))
                                        completer.complete(reader, new ArgumentLine(line.word, line.wordCursor), candidates)
                                    }
                                }
                                // 有可能在此结束，加入end_token
                                val end_completer = new StringsCompleter(InputParser.end_token)
                                end_completer.complete(reader, new ArgumentLine(line.word, line.wordCursor), candidates)
                                return //todo: break is not supported
                                
                            }else
                            {
                                val values = current_struct.getJSONArray(InputParser.LIST_TOKEN)
                                for (value <- values.toList.asScala) {
                                    val completer = new StringsCompleter(String.valueOf(value))
                                    completer.complete(reader, new ArgumentLine(line.word, line.wordCursor), candidates)
                                }
                                // 有可能在此结束，加入end_token
                                val end_completer = new StringsCompleter(InputParser.end_token)
                                end_completer.complete(reader, new ArgumentLine(line.word, line.wordCursor), candidates)
                                return //todo: break is not supported
                                
                            }
                        }
                    }
                    else if (!(current_word == InputParser.SPECIALS_TOKEN)) {
                        index += 1
                        // 表明开新的命令
                        is_parser_key = true
                    }
                }
            }
            else is_parser_key = true
        }
    }
}

