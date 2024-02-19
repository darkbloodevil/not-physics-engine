package npe.NotBox2D

import com.artemis.{BaseSystem, Entity, World => Engine}
import org.json.{JSONArray, JSONObject}
import npe.components._
import scala.collection.mutable
import npe.systems.EntityGeneratorSystem

import scala.collection.mutable.Queue


object EntityGeneratorManager {
    private var engine_to_generator: mutable.Map[Engine, EntityGenerator] = mutable.Map()

    def add_engine(engine: Engine): Unit = {
        if (!engine_to_generator.contains(engine)) {
            engine_to_generator += (engine -> new EntityGenerator(engine))
        }
    }

    /**
     * 加入instruction
     *
     * @param system         当前的system，用于定位engine
     * @param instruction_jo 指令json
     */
    def add_instruction(system: BaseSystem, instruction_jo: JSONObject): Unit = {
        EntityGeneratorManager.get_entity_generator(system).get.add(instruction_jo)
    }

    /**
     * 获取对应engine的generator
     *
     * @param system 调用的EntityGeneratorSystem，用于获取对应的世界engine
     * @return EntityGeneratorManager
     */
    def get_entity_generator(system: BaseSystem): Option[EntityGenerator] = {
        engine_to_generator.find { case (engine, _) =>
            engine != null && engine.getSystems != null && engine.getSystems.contains(system)
        } match {
            case Some((_, generator)) => Some(generator)
            case None => None
        }
    }

    def generate(system: EntityGeneratorSystem): Unit = {
        get_entity_generator(system).get.generate_process()
    }
}


