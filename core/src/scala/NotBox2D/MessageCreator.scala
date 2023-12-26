package NotBox2D

import components.GameMsgComponent
import com.artemis.{Aspect, Entity}
import org.json.{JSONArray, JSONObject}


/**
 * 生成某个统一的事件的msg
 * 关键在于event_id是相同的
 */
class MessageCreator {
    var event_id:Long=0L
    private val EVENT_ID = "event_id"

    /**
     * 用于记录该事件的id
     * @param event_id 事件的重复性id，如果不需要修改就为0L
     */
    def this(event_id:Long=0L)= {
        this()
        this.event_id=event_id
    }
    
    /**
     * 虽然写一下entity获取msg并不复杂，但是为了确保有加入事件id就要写函数里
     * 关键是加入event_id和同一个key下面是一列表的数据
     *
     * @param entity 附着msg的entity
     * @param key    msg类型
     * @return 返回用于填入数据的JSONObject
     */
    def createMsg(entity: Entity, key: String): JSONObject = {
        val msg = entity.edit().create(classOf[GameMsgComponent])
        // 创建一个新的JSON对象，用于存储消息内容
        val content = new JSONObject()

        // 检查消息中是否已经存在指定的键
        if (msg.msg.has(key)) {
            // 如果存在，将新的内容添加到该键对应的JSON数组中
            msg.msg.getJSONArray(key).put(content)
        } else {
            // 如果不存在，创建一个新的JSON数组，并将新的内容添加到数组中
            val ja = new JSONArray()
            ja.put(content)
            // 将新的JSON数组添加到消息中，键为指定的字符串
            msg.msg.put(key, ja)
        }

        // 将事件ID添加到内容中
        content.put(EVENT_ID, event_id)
        // 返回创建的内容
        content
    }

}
