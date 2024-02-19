package npe.systems

import com.artemis.{Aspect, Entity, EntitySystem, WorldConfiguration, World as Engine}
import npe.components.{GameMsgComponent, TimeTriggerComponent, TimeTriggerItem}
import npe.systems.MessageProcessingSystem
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.{Assertions, Test}
import org.scalatest.Assertions.*
import org.slf4j.LoggerFactory

class MessageProcessingSystemTest {
    private val logger= LoggerFactory.getLogger(classOf[TimeTriggerItemSystemTest])
    @Test
    @DisplayName("test_message_processing")
    def test_message_processing(): Unit = {
        val engine: Engine = Engine(WorldConfiguration().setSystem(MessageProcessingSystem()))
        var e1:Entity= engine.createEntity()
        var property1=e1.edit()
        engine.process()

    }

}
