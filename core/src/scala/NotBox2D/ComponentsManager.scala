package NotBox2D

import com.badlogic.ashley.core.{Engine, Entity}
import com.badlogic.gdx.Gdx
import components.LinkedComponent

object ComponentsManager {
    def update_entity(entity:Entity, engine:Engine): Unit = {
        val temp=engine.createEntity()
        val components=entity.getComponents
        for i<- 0 until components.size() do{
            val component=components.get(i)
            temp.add(component)
            component match
                case component1: LinkedComponent =>
                    component1.change_entity(temp)
//                    Gdx.app.log("ComponentsManager","LinkedComponent")
                case _ =>
        }
        engine.removeEntity(entity)
        engine.addEntity(temp)
        
    }
}
