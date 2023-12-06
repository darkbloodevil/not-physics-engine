import com.artemis.{Aspect, EntitySystem, WorldConfiguration, World as Engine}
import com.artemis.ComponentMapper
import com.typesafe.scalalogging.Logger
import tools.DeltaTimeRecorder
import org.scalatest.funsuite.AnyFunSuite
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.{Assertions, Test}
class DeltaTimeRecorderTest {
    @Test
    @DisplayName("test_get_delta_time")
    def test_get_delta_time(): Unit = {
        val st:SystemT=new SystemT()
        val engine:Engine=Engine(new WorldConfiguration().setSystem(st))
        DeltaTimeRecorder.set_deltaTime(engine,2.0f)
        engine.process()
        Assertions.assertEquals(2.0f,st.current_dt,"")
    }
}
class SystemT extends EntitySystem(Aspect.all(classOf[ComponentX])){
    val logger:Logger=Logger(getClass)
    var current_dt: Float = 0f
    var cmx:ComponentMapper[ComponentX]=_
    /**
     * Override to implement behavior when this system is called by the world.
     */
    protected def processSystem(): Unit = {
        logger.info("processing "+DeltaTimeRecorder.get_deltaTime(this))
        current_dt=DeltaTimeRecorder.get_deltaTime(this).get
    }
}