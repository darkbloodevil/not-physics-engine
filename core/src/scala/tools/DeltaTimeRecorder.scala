package tools

import com.artemis.{BaseSystem, World=>Engine}

/**
 * 用来记录各个engine的deltaTime。
 * 
 * update engine时，因为没办法传入update的deltaTime，所以就由这边传入，然后在system中，由system找到对应的engine再对应到deltaTime
 */
object DeltaTimeRecorder {
    private var engine_to_deltaTime: Map[Engine, Float] = Map()
    def set_deltaTime(engine: Engine,deltaTime:Float): Unit = {
        engine_to_deltaTime=engine_to_deltaTime.updated(engine , deltaTime)
    }

    def get_deltaTime(system: BaseSystem): Option[Float] = {
        engine_to_deltaTime.find { case (engine, _) =>
            engine != null && engine.getSystems != null && engine.getSystems.contains(system)
        } match {
            case Some((_, deltaTime)) => Some(deltaTime)
            case None => None
        }
    }
//    def get_deltaTime(system: BaseSystem): Option[Float] = {
//        for(engine<-engine_to_deltaTime.keys) do {
//            if (engine.getSystems.contains(system)) {
//                return engine_to_deltaTime.get(engine)
//            }
//        }
//        None
//    }

}
