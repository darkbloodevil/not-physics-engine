package systems

import NotBox2D.GameWorld
import com.badlogic.ashley.core.{ComponentMapper, Engine, Entity, EntitySystem, Family}
import com.badlogic.ashley.systems.IteratingSystem
import components.{GameMsgComponent, IdontKnowComponent, PhysicsBodyComponent}
import org.json.JSONArray
import systems.MessageProcessingSystem.{MESSAGE_PROCESSING_SYSTEM_TOKEN, family}

object MessageProcessingSystem {
    val MESSAGE_PROCESSING_SYSTEM_TOKEN = "message processing system"
    var family: Family = Family.all(classOf[GameMsgComponent]).get()
}

class MessageProcessingSystem(gameWorld: GameWorld) extends IteratingSystem(MessageProcessingSystem.family) {
    private var gameMsgMapper: ComponentMapper[GameMsgComponent] = _
    gameMsgMapper=ComponentMapper.getFor(classOf[GameMsgComponent])


    override def addedToEngine(engine: Engine): Unit = {
        super.addedToEngine(engine)
    }

    override def update(deltaTime: Float): Unit = {
        super.update(deltaTime)

    }

    override def processEntity(entity: Entity, deltaTime: Float): Unit = {

    }
}
