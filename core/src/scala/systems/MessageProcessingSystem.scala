package systems

import NotBox2D.{ComponentsManager, EventEnum, GameWorld}
import com.badlogic.ashley.core.{ComponentMapper, Engine, Entity, EntitySystem, Family}
import com.badlogic.ashley.systems.IteratingSystem
import components.{GameMsgComponent, IdontKnowComponent, PhysicsBodyComponent, PhysicsInstruction, PropertyComponent}
import org.json.JSONArray
import com.badlogic.gdx.Gdx
import systems.MessageProcessingSystem.MESSAGE_PROCESSING_SYSTEM_TOKEN

object MessageProcessingSystem {
    val MESSAGE_PROCESSING_SYSTEM_TOKEN = "message processing system"
}

class MessageProcessingSystem(gameWorld: GameWorld) extends MutableIteratingSystem {
    private var gameMsgMapper: ComponentMapper[GameMsgComponent] = _
    private var propertyMapper: ComponentMapper[PropertyComponent] = _
    gameMsgMapper = ComponentMapper.getFor(classOf[GameMsgComponent])
    propertyMapper = ComponentMapper.getFor(classOf[PropertyComponent])
    
    family=Family.all(classOf[GameMsgComponent], classOf[PropertyComponent]).get()
    override def update(deltaTime: Float): Unit = {
        super.update(deltaTime)

    }

    override def processEntity(entity: Entity, deltaTime: Float): Unit = {
        val gameMsgComponent: GameMsgComponent = gameMsgMapper.get(entity)
        val propertyComponent: PropertyComponent = propertyMapper.get(entity)
        val msg = gameMsgComponent.msg
//        Gdx.app.log("MessageProcessingSystem", "processEntity")
        val property = propertyComponent.property
        if (msg.has(EventEnum.CONTACT.toString) && property.has("name")) {
            val contact_tar: String = msg.getString(EventEnum.CONTACT.toString)
            val name: String = property.getString("name")

            if (contact_tar.equals("wall") && name.equals("big")) {
//                Gdx.app.log("MessageProcessingSystem", "instruction given")
                val physicsInstruction = engine.createComponent(classOf[PhysicsInstruction])
                physicsInstruction.instructions.put("CHANGE_DIRECTION")
                entity.add(physicsInstruction)
            }
        }

        ComponentsManager.update_entity(entity,engine)
    }
}
