package systems

import com.badlogic.ashley.core._
import com.badlogic.ashley.core._
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.utils.{Array => GDXarray}
import components.{IdontKnowComponent, PhysicsBodyComponent}

import java.util


class PhysicsSystem(var engine: Engine) extends EntitySystem {
    var PHYSICS_WORLD: World = null
    //float kind message needs to add an F
    val MESSAGE_APPLY_FORCE = "apply_force"
    val MESSAGE_END = "end"
    val MESSAGE_DIRECTION_X = "direction_x"
    val MESSAGE_DIRECTION_Y = "direction_y"
    val MESSAGE_POSITION_X = "position_x"
    val MESSAGE_POSITION_Y = "position_y"
    val MESSAGE_STRENGTH = "strength"
    private val TIME_STEP = .005f
    private val VELOCITY_ITERATIONS = 8
    private val POSITION_ITERATIONS = 10
    private var physicsBodyMapper: ComponentMapper[PhysicsBodyComponent] = null
    private var idkMapper: ComponentMapper[IdontKnowComponent] = null

    var GRAVITY = 10
    private var accumulator = 0
//     PHYSICS_WORLD = new World(new Vector2(0, GRAVITY), true)
    physicsBodyMapper = ComponentMapper.getFor(classOf[PhysicsBodyComponent])
    idkMapper=ComponentMapper.getFor(classOf[IdontKnowComponent])

    override def addedToEngine(engine: Engine): Unit = {
        super.addedToEngine(engine)
    }

    override def update(deltaTime: Float): Unit = {
        super.update(deltaTime)
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        val frameTime = Math.min(deltaTime, 0.25f)

        accumulator += frameTime
        while ( {
            accumulator >= TIME_STEP
        }) {
            PHYSICS_WORLD.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
            //PHYSICS_WORLD.step(deltaTime, 2, 2);
            accumulator -= TIME_STEP
        }
    }
    def update_body_data(): Unit ={
        val bodies=new GDXarray[Body];
        PHYSICS_WORLD.getBodies(bodies)
        for(body <- bodies.items)
            {
                val body_entity: Entity = body.getUserData.asInstanceOf[Entity]
                if(body_entity !=null){
                    val idk: IdontKnowComponent = idkMapper.get(body_entity)
                    if (idk!=null){
                        body.applyForceToCenter(new Vector2(10, 10), true)
                    }
                }
            }
    }
}
