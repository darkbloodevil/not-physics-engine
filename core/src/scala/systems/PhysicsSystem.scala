package systems

import com.badlogic.ashley.core._
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.utils.{Array => GDXarray}
import components.{IdontKnowComponent, PhysicsBodyComponent, TransformComponent}
import org.etsi.uri.x01903.v13.impl.EncapsulatedPKIDataTypeImpl

import java.util
import scala.tools.nsc.doc.model


class PhysicsSystem(var engine: Engine) extends EntitySystem {
    var PHYSICS_WORLD: World = _
    private val TIME_STEP = .005f
    private val VELOCITY_ITERATIONS = 8
    private val POSITION_ITERATIONS = 10
    private var physicsBodyMapper: ComponentMapper[PhysicsBodyComponent] = _
    private var transformMapper: ComponentMapper[TransformComponent] = _
    private var idkMapper: ComponentMapper[IdontKnowComponent] = _

    var GRAVITY = 10
    private var accumulator: Float = 0
    //     PHYSICS_WORLD = new World(new Vector2(0, GRAVITY), true)
    physicsBodyMapper = ComponentMapper.getFor(classOf[PhysicsBodyComponent])
    transformMapper = ComponentMapper.getFor(classOf[TransformComponent])
    idkMapper = ComponentMapper.getFor(classOf[IdontKnowComponent])

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
        update_bodies()
    }

    /**
     * 处理运动的原动力
     * 并且与显示上的同步
     */
    def update_bodies(): Unit = {
        val bodies = new GDXarray[Body];
        PHYSICS_WORLD.getBodies(bodies)
        for (body <- bodies.items) {
            val body_entity: Entity = body.getUserData.asInstanceOf[Entity]
            if (body_entity != null) {
                val idk: IdontKnowComponent = idkMapper.get(body_entity)
                if (idk != null) {
                    body.applyForceToCenter(new Vector2(30, 20), true)
                }
            }

            val tc = transformMapper.get(body_entity)
            if (tc != null) { // Update the entities/sprites position and angle
                tc.pos.x = body.getPosition.x
                tc.pos.y = body.getPosition.y
                // We need to convert our angle from radians to degrees
                tc.rotation = body.getAngle
                //Gdx.app.log("my","position "+b.getPosition().x+" "+ b.getPosition().y);
            }

        }
    }

}
