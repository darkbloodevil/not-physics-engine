package components

import com.artemis.Component
import org.json.JSONObject

/**
 * 用于传递信息（仅仅与该entity相关的信息
 */
class GameMsgComponent extends Component {
    var msg: JSONObject = new JSONObject()


    override def clone(): GameMsgComponent = {
        super.clone()
        val clone_result = new GameMsgComponent()
        clone_result.msg = new JSONObject(msg.toString)
        clone_result
    }
}
