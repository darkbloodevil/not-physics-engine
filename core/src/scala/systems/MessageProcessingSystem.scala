package systems

import NotBox2D.{EventEnum, GameWorld}
import com.badlogic.ashley.core.{ComponentMapper, Engine, Entity, EntitySystem, Family}
import com.badlogic.ashley.systems.IteratingSystem
import components.{GameMsgComponent, IdontKnowComponent, PhysicsBodyComponent, PhysicsInstruction, PropertyComponent}
import org.json.JSONArray
import systems.MessageProcessingSystem.{MESSAGE_PROCESSING_SYSTEM_TOKEN, family}

object MessageProcessingSystem {
    val MESSAGE_PROCESSING_SYSTEM_TOKEN = "message processing system"
    var family: Family = Family.all(classOf[GameMsgComponent],classOf[PropertyComponent]).get()
}

class MessageProcessingSystem(gameWorld: GameWorld) extends IteratingSystem(MessageProcessingSystem.family) {
    private var gameMsgMapper: ComponentMapper[GameMsgComponent] = _
    private var propertyMapper:ComponentMapper[PropertyComponent]=_
    var engine:Engine=_
    gameMsgMapper=ComponentMapper.getFor(classOf[GameMsgComponent])
    propertyMapper=ComponentMapper.getFor(classOf[PropertyComponent])

    override def addedToEngine(engine: Engine): Unit = {
        super.addedToEngine(engine)
        this.engine=engine
    }

    override def update(deltaTime: Float): Unit = {
        super.update(deltaTime)

    }

    override def processEntity(entity: Entity, deltaTime: Float): Unit = {
        val gameMsgComponent: GameMsgComponent = gameMsgMapper.get(entity)
        val propertyComponent: PropertyComponent = propertyMapper.get(entity)
        val msg=gameMsgComponent.msg
        val property=propertyComponent.property
        if(msg.has(EventEnum.CONTACT.toString) &&property.has("name")){
            val contact_tar: String = msg.getString(EventEnum.CONTACT.toString)
            val name:String=property.getString("name")
            if(contact_tar.equals("wall") &&name.equals("big")) {
                val physicsInstruction=engine.createComponent(classOf[PhysicsInstruction])
                physicsInstruction.instructions.put("CHANGE_DIRECTION")
            }
        }
        entity.remove(classOf[GameMsgComponent])
    }
}
