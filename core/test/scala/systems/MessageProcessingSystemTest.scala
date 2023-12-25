package systems

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class MessageProcessingSystemTest {
    private val logger= LoggerFactory.getLogger(classOf[TimeTriggerItemSystemTest])
    @Test
    @DisplayName("test_message_processing")
    def test_message_processing(): Unit = {
        val engine: Engine = Engine(WorldConfiguration().setSystem(MessageProcessingSystem()))
        
        engine.process()
        
    }
    
}
