package NotBox2D

import com.artemis.{BaseSystem, Entity, World as Engine}
import org.json.{JSONArray, JSONObject}
import systems.EntityGeneratorSystem
import components.*

import scala.collection.mutable.Queue

object EntityGenerator{
    private var engine_to_generator: Map[Engine, EntityGenerator] = Map()
    def add_engine(engine: Engine): Unit = {
        if(!engine_to_generator.contains(engine)){
            engine_to_generator+=(engine,EntityGenerator(engine))
        }
    }

    /**
     * 加入instruction
     * @param system 当前的system，用于定位engine
     * @param instruction_jo 指令json
     */
    def add_instruction(system:BaseSystem,instruction_jo: JSONObject): Unit = {
        EntityGenerator.get_entity_generator(system).get.add(instruction_jo)
    }

    /**
     * 获取对应engine的generator
     * @param system 调用的EntityGeneratorSystem，用于获取对应的世界engine
     * @return EntityGenerator
     */
    def get_entity_generator(system:BaseSystem): Option[EntityGenerator] = {
        for (engine <- engine_to_generator.keys) do {
            if (engine.getSystems.contains(system)) {
                return engine_to_generator.get(engine)
            }
        }
        None
    }
    def generate(system:EntityGeneratorSystem): Unit = {
        get_entity_generator(system).get.generate_process()
    }
}

/**
 * 用于生成entity
 */
class EntityGenerator(engine: Engine) {
    val instruction_queue:Queue[JSONObject]=Queue[JSONObject]()
    /**
     *
     */
    def generate_process(): Unit = {
        while (!instruction_queue.isEmpty) do{
            val instruction_jo:JSONObject=instruction_queue.dequeue
            val entity: Entity = engine.createEntity()
            if(instruction_jo.get("name")=="test_instruction"){
                var test_instruction=entity.edit().create(classOf[InstructionComponent])
                var ja=JSONArray()
                ja.put(instruction_jo)
                test_instruction.instructions=ja
            }

        }

    }
    def add(instruction_jo: JSONObject): Unit = {
        instruction_queue+=instruction_jo
    }
}
