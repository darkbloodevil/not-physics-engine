package components

import com.badlogic.ashley.core.Component
import org.json.JSONObject

/**
 * 特性component
 *
 * 就像主动天赋、被动天赋
 */
class PropertyComponent extends Component{
    var property:JSONObject=_


    override def clone(): PropertyComponent = {
        super.clone()
        val clone_result = new PropertyComponent()
        clone_result.property=new JSONObject(property.toString)
        clone_result
    }
}