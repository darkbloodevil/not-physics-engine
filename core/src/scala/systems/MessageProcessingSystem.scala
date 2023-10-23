package systems

import NotBox2D.GameWorld
import com.badlogic.ashley.core.{Engine, EntitySystem}
import org.json.JSONArray
import systems.MessageProcessingSystem.MESSAGE_PROCESSING_SYSTEM_TOKEN

object MessageProcessingSystem {
    val MESSAGE_PROCESSING_SYSTEM_TOKEN = "message processing system"
}

class MessageProcessingSystem(val gameWorld: GameWorld) extends EntitySystem {


    override def addedToEngine(engine: Engine): Unit = {
        super.addedToEngine(engine)
    }

    override def update(deltaTime: Float): Unit = {
        super.update(deltaTime)

    }
}
