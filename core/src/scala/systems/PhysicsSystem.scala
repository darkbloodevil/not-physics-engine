package systems

import com.badlogic.ashley.core._
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.utils.{Array => GDXarray}
import components.{IdontKnowComponent, PhysicsBodyComponent, TransformComponent}



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
    def set_world(world: World): Unit ={
        PHYSICS_WORLD=world
        PHYSICS_WORLD.setContactListener(new ContactListener() {
            override def beginContact(contact: Contact): Unit = {
                val entity1: Entity = contact.getFixtureA.getBody.getUserData.asInstanceOf[Entity]
                val entity2: Entity = contact.getFixtureA.getBody.getUserData.asInstanceOf[Entity]
                val idk1:Option[IdontKnowComponent] =Option(idkMapper.get(entity1))
                val idk2:Option[IdontKnowComponent] =Option(idkMapper.get(entity2))
                if(idk2.isDefined){
                    idk2.get.x_v = (2*(scala.util.Random.nextInt(1)-0.5)).toInt*idk2.get.y_v+scala.util.Random.nextInt(3)
                    idk2.get.y_v = (2*(scala.util.Random.nextInt(1)-0.5)).toInt*idk2.get.x_v+scala.util.Random.nextInt(1)
                }
                if(idk1.isDefined){
                    idk1.get.x_v = (2*(scala.util.Random.nextInt(1)-0.5)).toInt*idk1.get.y_v+scala.util.Random.nextInt(5)
                    idk1.get.y_v = (2*(scala.util.Random.nextInt(1)-0.5)).toInt*idk1.get.x_v+scala.util.Random.nextInt(5)
                }
            }

            override def endContact(contact: Contact): Unit = {

            }

            override def preSolve(contact: Contact, oldManifold: Manifold): Unit = {
            }

            override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = {
            }
        })

    }

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
                    body.setLinearVelocity(new Vector2(idk.x_v.toFloat, idk.y_v.toFloat))
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
