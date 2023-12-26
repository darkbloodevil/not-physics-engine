package NotBox2D

import com.artemis.Entity
import components.InstructionComponent
import org.json.{JSONArray, JSONObject}
import com.artemis.{BaseSystem, Entity, World => Engine}
import scala.collection.mutable.Queue

/**
 * 用于生成entity
 */
class EntityGenerator(engine: Engine) {
    private val instruction_queue: Queue[JSONObject] = Queue[JSONObject]()

    /**
     *
     */
    def generate_process(): Unit = {
        while (instruction_queue.nonEmpty) do {
            val instruction_jo: JSONObject = instruction_queue.dequeue()
            val entity: Entity = engine.createEntity()
            if (instruction_jo.get("name") == "test_instruction") {
                var test_instruction = entity.edit().create(classOf[InstructionComponent])
                var ja = new JSONArray()
                ja.put(instruction_jo)
                test_instruction.instructions = ja
            }

        }

    }

    def add(instruction_jo: JSONObject): Unit = {
        instruction_queue += instruction_jo
    }
}