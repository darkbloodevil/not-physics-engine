package tools

import com.artemis.{BaseSystem, World}

object DeltaTimeRecorder {
    var world_to_deltaTime: Map[World, Float] = Map()
    def set_deltaTime(world: World,deltaTime:Float): Unit = {
        world_to_deltaTime+=(world,deltaTime)
    }
    def get_deltaTime(system: BaseSystem): Option[Float] = {
        for(world<-world_to_deltaTime.keys) do {
            if (world_to_deltaTime.contains(world)) {
                return world_to_deltaTime.get(world)
            }
        }
        None
    }

}
