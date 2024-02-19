package npe.components

import com.artemis.Component
import org.json.JSONArray

/**
 * 指令组件
 */
class InstructionComponent extends Component {
    var instructions:JSONArray=new JSONArray()
}
class PhysicsInstruction extends InstructionComponent{

}