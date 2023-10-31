package NotBox2D

import EventEnum.*
import com.badlogic.ashley.core.{ComponentMapper, Entity}
import components.{PhysicsBodyComponent, PropertyComponent}


class DecouplingProcessor{
    var physicsBodyMapper:ComponentMapper[PhysicsBodyComponent] = _
    var propertyMapper:ComponentMapper[PropertyComponent]=_
    /**
     * 用来处理多个entity交互，去除其中交互的影响，剩下的再作为信息交付处理
     */
    def decouple_and_process(event: EventEnum, entities: List[Entity]): Unit = {
        event match
            case CONTACT => {

            }
    }

}
