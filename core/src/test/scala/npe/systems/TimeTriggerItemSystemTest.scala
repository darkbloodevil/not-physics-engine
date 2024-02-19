package npe.systems

import com.artemis.systems.EntityProcessingSystem
import com.artemis.{Aspect, Entity, EntitySystem, WorldConfiguration, World as Engine}
import npe.components.{GameMsgComponent, TimeTriggerComponent, TimeTriggerItem}
import npe.systems.{SystemMsgT, TimeTriggerItemSystem}
import npe.tools.DeltaTimeRecorder
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.{Assertions, Test}
import org.scalatest.Assertions.*
import org.slf4j.LoggerFactory

class TimeTriggerItemSystemTest {
    private var logger = LoggerFactory.getLogger(classOf[TimeTriggerItemSystemTest])

    @Test
    @DisplayName("test_time_trigger")
    def test_time_trigger(): Unit = {
        val engine: Engine = Engine(WorldConfiguration().setSystem(TimeTriggerItemSystem()).setSystem(SystemMsgT()))
        val ttc = engine.createEntity().edit().create(classOf[TimeTriggerComponent])
        DeltaTimeRecorder.set_deltaTime(engine, 1.0f)
        ttc.timeTriggerItems.add(new TimeTriggerItem(1.2f, "A", 0L))
        ttc.timeTriggerItems.add(new TimeTriggerItem(2.2f, "B"))
        ttc.timeTriggerItems.add(new TimeTriggerItem(4.2f, "C"))

        var i = 0
        while (i < 5)do {
            engine.process()
            logger.info("%d second has passed".formatted(i + 1))
            i += 1
        }
    }

    @Test
    @DisplayName("test_time_trigger")
    def test_time_trigger2(): Unit = {
        val engine: Engine = Engine(WorldConfiguration().setSystem(SystemMsgT()).setSystem(TimeTriggerItemSystem()))
        val entity = engine.createEntity()
        val ttc = entity.edit().create(classOf[TimeTriggerComponent])
        val ttcMapper = engine.getMapper(classOf[TimeTriggerComponent])
        val msgMapper = engine.getMapper(classOf[GameMsgComponent])

        ttc.timeTriggerItems.add(TimeTriggerItem(1.2f, "A", 0L))
        DeltaTimeRecorder.set_deltaTime(engine, 1.0f)

        engine.process()
        logger.info("1 second has passed")
        Assertions.assertTrue(!msgMapper.has(entity), "triggered at 1.2 second")

        engine.process()
        logger.info("2 second has passed")
        Assertions.assertTrue(msgMapper.has(entity), "triggered at 1.2 second, sends message")
        Assertions.assertTrue(!ttcMapper.has(entity), "triggered at 1.2 second, remove time trigger")

        engine.process()
        logger.info("3 second has passed")
    }
}