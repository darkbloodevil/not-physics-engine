package components

import com.artemis.Component
import org.json.JSONObject

import java.util


/**
 * 用于记录某条目剩余时间
 */
class TimeTriggerItem{
    var time: Float=0.0f
    var item: String=""
    def this(time:Float,item:String)={
        this()
        this.time=time
        this.item=item
    }
}

/**
 * 时间触发的component
 * 只标记条目名和剩余时间（的列表）。
 */
class TimeTriggerComponent extends Component{
    var timeTriggerItems:util.List[TimeTriggerItem]=util.ArrayList[TimeTriggerItem]()
}

