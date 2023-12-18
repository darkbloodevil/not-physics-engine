package components

import com.artemis.Component
import org.json.JSONObject

import java.util


/**
 * 用于记录某条目剩余时间
 */
class TimeTriggerItem{
    var time: Float
    var item: String
}

/**
 * 时间触发的component
 * 只标记条目名和剩余时间（的列表）。
 */
class TimeTriggerComponent extends Component{
    var timeTriggerItems:util.List[TimeTriggerItem]=util.ArrayList[TimeTriggerItem]()
}

