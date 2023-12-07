import com.artemis.{Aspect, Entity, EntitySystem, WorldConfiguration, World as Engine}
import com.artemis.systems.EntityProcessingSystem
import com.typesafe.scalalogging.Logger
import NotBox2D.EntityGenerator
import components.{InstructionComponent, *}
import org.json.{JSONArray, JSONObject}
import org.scalatest.funsuite.AnyFunSuite
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.{Assertions, Test}
import systems.EntityGeneratorSystem

class EntityGeneratorTest {

    @Test
    @DisplayName("test_entity_generator")
    def test_entity_generator(): Unit = {
        val egs:EntityGeneratorSystem=EntityGeneratorSystem()
        val systemT:SystemT2=SystemT2()
        val engine:Engine=Engine(WorldConfiguration().setSystem(SystemU()).setSystem(egs).setSystem(systemT))
        EntityGenerator.add_engine(engine)
        engine.process()
        engine.process()
        EntityGenerator.add_instruction(egs,JSONObject(
            """{"name":"test_instruction","instruction":"generate!"}"""))
        engine.process()
        Assertions.assertTrue(systemT.has_component,"有没有生成呢")
    }

}
class SystemT2 extends EntityProcessingSystem(Aspect.all(classOf[InstructionComponent])){
    val logger:Logger=Logger(getClass)
    var has_component=false
    override def process(entity: Entity): Unit = {
        logger.info(entity.getComponent(classOf[InstructionComponent]).instructions.toString)
        has_component=true
    }
}
class SystemU extends EntitySystem(Aspect.exclude()){
    val logger:Logger=Logger(getClass)
    var i=0

    override def processSystem(): Unit = {
        i+=1
        logger.info("ohhhhhh "+i)
    }
}