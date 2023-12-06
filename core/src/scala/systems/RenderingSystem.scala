package systems
import com.artemis.systems.EntityProcessingSystem
import com.artemis.{ComponentMapper, Entity, Aspect,World=>Engine}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import components.TransformComponent
class RenderingSystem(batch: SpriteBatch) extends EntityProcessingSystem(Aspect.all(classOf[TransformComponent])){
    private var transformMapper: ComponentMapper[TransformComponent] = _
    

    override def process(entity: Entity): Unit = {

    }
}
