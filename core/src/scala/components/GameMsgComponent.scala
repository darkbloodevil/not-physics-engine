package components

import com.badlogic.ashley.core.Component
import org.json.JSONObject


/**
 * 用来表示游戏消息。
 * 例如，与某某相撞了，就发送一个message
 */
class GameMsgComponent(var target_system:String,var message_json:JSONObject) extends Component{
}

