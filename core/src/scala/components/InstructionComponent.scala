package components

import com.badlogic.ashley.core.Component
import org.json.JSONArray

class InstructionComponent extends Component {
    var instructions:JSONArray=new JSONArray()
}
class PhysicsInstruction extends InstructionComponent{

}