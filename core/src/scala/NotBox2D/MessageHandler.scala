package NotBox2D

import org.json.JSONObject
import com.artemis.*
/**
 * 用于处理单一的message
 * @param property 对应的property
 */
class MessageHandler(property:JSONObject) {
    def handle_message(event_str: String, message: JSONObject): Unit = {

    }
}
class MessageHandlerManager{

}

/**
 * 存储message向指令转化过程的中间量
 * @param entity 要执行instruction的entity
 * @param instruction instruction描述
 * @param event_id 触发的事件（默认0L就不管了）
 */
abstract class InstructionCarrier(entity:Entity, instruction:JSONObject, event_id:Long=0L){
    /**
     * 实际的分发instruction
     */
    def send_instruction(): Unit
}