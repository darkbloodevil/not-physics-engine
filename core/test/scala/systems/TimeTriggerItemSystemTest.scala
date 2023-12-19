package systems

import com.artemis.systems.EntityProcessingSystem
import com.artemis.{Aspect, Entity, EntitySystem, WorldConfiguration, World as Engine}
import components.{GameMsgComponent, TimeTriggerComponent, TimeTriggerItem}
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.{Assertions, Test}
import org.scalatest.Assertions.*
import org.slf4j.LoggerFactory
import tools.DeltaTimeRecorder

class TimeTriggerItemSystemTest{
    var logger= LoggerFactory.getLogger(classOf[TimeTriggerItemSystemTest]);
    @Test
    @DisplayName("test_time_trigger")
    def test_time_trigger(): Unit = {
        val engine: Engine = Engine(WorldConfiguration().setSystem(TimeTriggerItemSystem()).setSystem(SystemMsgT()))
        val ttc=engine.createEntity().edit().create(classOf[TimeTriggerComponent])
        DeltaTimeRecorder.set_deltaTime(engine, 1.0f)
        ttc.timeTriggerItems.add(TimeTriggerItem(1.2f,"A"))
        ttc.timeTriggerItems.add(TimeTriggerItem(2.2f,"B"))
        ttc.timeTriggerItems.add(TimeTriggerItem(4.2f,"C"))
        for i <- 0 to 5 do{
            engine.process()
            logger.info("%d second has passed".formatted(i+1))
        }
    }

    @Test
    @DisplayName("test_time_trigger")
    def test_time_trigger2(): Unit = {
        val engine: Engine = Engine(WorldConfiguration().setSystem(SystemMsgT()).setSystem(TimeTriggerItemSystem()))
        val entity= engine.createEntity()
        val ttc = entity.edit().create(classOf[TimeTriggerComponent])
        val ttcMapper=engine.getMapper(classOf[TimeTriggerComponent])
        val msgMapper=engine.getMapper(classOf[GameMsgComponent])

        ttc.timeTriggerItems.add(TimeTriggerItem(1.2f, "A"))
        DeltaTimeRecorder.set_deltaTime(engine, 1.0f)
        
        engine.process()
        logger.info("1 second has passed")
        Assertions.assertTrue(!msgMapper.has(entity),"triggered at 1.2 second")
        
        engine.process()
        logger.info("2 second has passed")
        Assertions.assertTrue(msgMapper.has(entity), "triggered at 1.2 second, sends message")
        Assertions.assertTrue(!ttcMapper.has(entity), "triggered at 1.2 second, remove time trigger")
        
        engine.process()
        logger.info("3 second has passed")
    }
}