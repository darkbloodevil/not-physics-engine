package NotBox2D

import EventEnum.*
import com.badlogic.gdx.Gdx
import components.{GameMsgComponent, PhysicsBodyComponent, PropertyComponent}
import com.artemis.{Aspect, Entity, World as Engine}
import org.json.{JSONArray, JSONObject}
import tools.IdGenerator


class DecouplingProcessor {
    private var msgCreator: MessageCreator = _


    /**
     * 用来处理多个entity交互，去除其中交互的影响，剩下的再作为信息交付处理
     */
    def decouple_and_process(event: EventEnum, entities: java.util.List[Entity]): Unit = {

        event match
            case CONTACT => {

                val e1: Entity = entities.get(0)
                val e2: Entity = entities.get(1)
                this.msgCreator = MessageCreator(IdGenerator.INSTANCE.nextId())
                handle_big(e1, e2)
                handle_big(e2, e1)
            }
    }

    /**
     * @TODO 改msg！！！
     * @param e1
     * @param e2
     * @return
     */
    private def handle_big(e1: Entity, e2: Entity): Boolean = {
        val pc1 = e1.getComponent(classOf[PropertyComponent])
        if (pc1 != null && pc1.property.has("name") && pc1.property.getString("name").equals("big")) {
            val pc2 = e2.getComponent(classOf[PropertyComponent])
            if (pc2 != null) {
                if (pc2.property.has("name")) {
                    pc2.property.getString("name") match
                        case "big" => {
                            //                            val game_msg: GameMsgComponent = entity.edit().create(classOf[GameMsgComponent])
                            //                            game_msg.msg.put(CONTACT.toString, "big")
                            //                            game_msg.msg.put("position", new JSONObject("""{"x":1,"y":2}"""))
                            val msg_jo = createMsg(e1, CONTACT.toString)
                            msg_jo.put("target", "big")
                            msg_jo.put("position", new JSONObject("""{"x":1,"y":2}"""))
                        }
                } else {

                }
            } else {
                //                val game_msg: GameMsgComponent = e1.edit().create(classOf[GameMsgComponent])
                //                game_msg.msg.put(CONTACT.toString, "wall")
                val msg_jo = createMsg(e1, CONTACT.toString)
                msg_jo.put("target", "wall")
                //                    ComponentsManager.update_entity(e1,engine)
                return true
            }
        }

        false
    }


}
