package NotBox2D

import EventEnum.*
import com.badlogic.ashley.core.{ComponentMapper, Engine, Entity}
import components.{GameMsgComponent, PhysicsBodyComponent, PropertyComponent}


class DecouplingProcessor{
    var physicsBodyMapper:ComponentMapper[PhysicsBodyComponent] = _
    var propertyMapper:ComponentMapper[PropertyComponent]=_
    var gameMsgMapper:ComponentMapper[GameMsgComponent]=_
    var engine:Engine=_
    /**
     * 用来处理多个entity交互，去除其中交互的影响，剩下的再作为信息交付处理
     */
    def decouple_and_process(event: EventEnum, entities: List[Entity]): Unit = {
        event match
            case CONTACT => {
                val e1:Entity=entities.head
                val e2:Entity=entities.last

            }
    }
    def handle_big(e1:Entity, e2:Entity): Boolean = {
        if (propertyMapper.has(e1)) {
            val pc1 = propertyMapper.get(e1)
            if (pc1.property.has("name").equals("big")) {
                if (propertyMapper.has(e2)){
                    val pc2 = propertyMapper.get(e2)
                    if (pc2.property.has("name").equals("big")){

                    }
                }else{
                    if (!gameMsgMapper.has(e1)) {
                        val msgc=engine.createComponent(classOf[GameMsgComponent])
                        e1.add(msgc)
                    }
                    val game_msg=gameMsgMapper.get(e1)
                    game_msg.msg.put(CONTACT.toString,"wall")
                    return true
                }
            }
        }
        false
    }

}
