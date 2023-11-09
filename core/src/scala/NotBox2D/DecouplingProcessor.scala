package NotBox2D

import EventEnum.*
import com.badlogic.ashley.core.{ComponentMapper, Engine, Entity}
import com.badlogic.gdx.Gdx
import components.{GameMsgComponent, PhysicsBodyComponent, PropertyComponent}


class DecouplingProcessor(engine:Engine){
    var physicsBodyMapper:ComponentMapper[PhysicsBodyComponent] = _
    var propertyMapper:ComponentMapper[PropertyComponent]=_
    var gameMsgMapper:ComponentMapper[GameMsgComponent]=_


    physicsBodyMapper = ComponentMapper.getFor(classOf[PhysicsBodyComponent])
    propertyMapper = ComponentMapper.getFor(classOf[PropertyComponent])
    gameMsgMapper=ComponentMapper.getFor(classOf[GameMsgComponent])
    /**
     * 用来处理多个entity交互，去除其中交互的影响，剩下的再作为信息交付处理
     */
    def decouple_and_process(event: EventEnum, entities: List[Entity]): Unit = {
//        Gdx.app.log("DecouplingProcessor", "decouple_and_process start!")
        event match
            case CONTACT => {
//                Gdx.app.log("DecouplingProcessor", "Contact!")
                val e1:Entity=entities.head
                val e2:Entity=entities.last
                handle_big(e1,e2)
                handle_big(e2,e1)
            }
    }
    def handle_big(e1:Entity, e2:Entity): Boolean = {
//        Gdx.app.log("DecouplingProcessor","handle_big")
        if (propertyMapper.has(e1)) {
//            Gdx.app.log("DecouplingProcessor","handle_big has entity")
            val pc1 = propertyMapper.get(e1)
            if (pc1.property.has("name")&&pc1.property.getString("name").equals("big")) {
//                Gdx.app.log("DecouplingProcessor","entity is big")
                if (propertyMapper.has(e2)){
                    val pc2 = propertyMapper.get(e2)
                    if (pc2.property.has("name")){
                        pc2.property.getString("name") match
                            case "big"=>{

                            }
                            case _=>Gdx.app.log("DecouplingProcessor",pc2.property.getString("name"))
                    }else {

                    }
                }else{
                    if (!gameMsgMapper.has(e1)) {
                        val msgc = engine.createComponent(classOf[GameMsgComponent])
                        e1.add(msgc)
                    }
                    val game_msg = gameMsgMapper.get(e1)
                    game_msg.msg.put(CONTACT.toString, "wall")
                    ComponentsManager.update_entity(e1,engine)
                    return true
                }
            }
        }
        false
    }

}
