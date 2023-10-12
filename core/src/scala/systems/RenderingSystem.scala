package systems
import com.badlogic.ashley.core._
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import components.TransformComponent
class RenderingSystem(batch: SpriteBatch) extends IteratingSystem(Family.all(classOf[TransformComponent]).get){
    private var transformMapper: ComponentMapper[TransformComponent] = _


    transformMapper = ComponentMapper.getFor(classOf[TransformComponent])

    override def update(deltaTime: Float): Unit ={

    }

    override def processEntity(entity: Entity, deltaTime: Float): Unit = {

    }
}
