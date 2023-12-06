package NotBox2D

import EventEnum.*
import com.badlogic.gdx.Gdx
import components.{GameMsgComponent, PhysicsBodyComponent, PropertyComponent}
import com.artemis.{Entity, Aspect, World => Engine}


class DecouplingProcessor {
    //    var physicsBodyMapper:ComponentMapper[PhysicsBodyComponent] = _
    //    var propertyMapper:ComponentMapper[PropertyComponent]=_
    //    var gameMsgMapper:ComponentMapper[GameMsgComponent]=_
    //
    //
    //    physicsBodyMapper = ComponentMapper.getFor(classOf[PhysicsBodyComponent])
    //    propertyMapper = ComponentMapper.getFor(classOf[PropertyComponent])
    //    gameMsgMapper=ComponentMapper.getFor(classOf[GameMsgComponent])

    /**
     * 用来处理多个entity交互，去除其中交互的影响，剩下的再作为信息交付处理
     */
    def decouple_and_process(event: EventEnum, entities:java.util.List[Entity]): Unit = {
        //        Gdx.app.log("DecouplingProcessor", "decouple_and_process start!")
        event match
            case CONTACT => {
                //                Gdx.app.log("DecouplingProcessor", "Contact!")
                val e1: Entity = entities.get(0)
                val e2: Entity = entities.get(1)
                handle_big(e1, e2)
                handle_big(e2, e1)
            }
    }

    def handle_big(e1: Entity, e2: Entity): Boolean = {
        //        Gdx.app.log("DecouplingProcessor","handle_big")
        //            Gdx.app.log("DecouplingProcessor","handle_big has entity")
        val pc1 = e1.getComponent(classOf[PropertyComponent])
        if (pc1 != null && pc1.property.has("name") && pc1.property.getString("name").equals("big")) {
            //                Gdx.app.log("DecouplingProcessor","entity is big")
            val pc2 = e2.getComponent(classOf[PropertyComponent])
            if (pc2 != null) {
                if (pc2.property.has("name")) {
                    pc2.property.getString("name") match
                        case "big" => {

                        }
                        case _ => Gdx.app.log("DecouplingProcessor", pc2.property.getString("name"))
                } else {

                }
            } else {
                val game_msg: GameMsgComponent = e1.edit().create(classOf[GameMsgComponent])
                game_msg.msg.put(CONTACT.toString, "wall")
                //                    ComponentsManager.update_entity(e1,engine)
                return true
            }
        }

        false
    }

}
