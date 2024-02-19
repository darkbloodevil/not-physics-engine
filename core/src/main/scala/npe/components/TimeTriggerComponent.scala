package npe.components

import com.artemis.Component
import org.json.JSONObject

import java.util


/**
 * 用于记录某条目剩余时间
 */
class TimeTriggerItem{
    var time: Float=0.0f
    var item: String=""
    var event_id:Long=0L

    /**
     * 
     * @param time 要多久后触发
     * @param item 触发什么
     * @param event_id 触发对应的事件
     */
    def this(time:Float,item:String,event_id:Long=0L)={
        this()
        this.time=time
        this.item=item
        this.event_id=event_id
    }
}

/**
 * 时间触发的component
 * 只标记条目名和剩余时间（的列表）。
 */
class TimeTriggerComponent extends Component{
    var timeTriggerItems:util.List[TimeTriggerItem]=new util.ArrayList[TimeTriggerItem]()
}

