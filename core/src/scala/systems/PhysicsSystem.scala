package systems

import NotBox2D.{DecouplingProcessor, EventEnum, GameWorld}
import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array as GDXarray
import com.typesafe.scalalogging.Logger
import components.{IdontKnowComponent, PhysicsBodyComponent, PhysicsInstruction, PropertyComponent, TransformComponent}
import org.json.{JSONArray, JSONObject}
import demos.LogTester.getClass

import java.util

object PhysicsSystem{
    /**
     * 用于表明是物理系统
     */
    val PHYSICS_SYSTEM_TOKEN="physics system"
}

class PhysicsSystem(val gameWorld: GameWorld) extends EntitySystem {
    var PHYSICS_WORLD: World = _
    var engine: Engine=_
    private val logger=Logger(getClass.getName)

    private val TIME_STEP = .005f
    private val VELOCITY_ITERATIONS = 8
    private val POSITION_ITERATIONS = 10
    private var physicsBodyMapper: ComponentMapper[PhysicsBodyComponent] = _
    private var transformMapper: ComponentMapper[TransformComponent] = _
    private var idkMapper: ComponentMapper[IdontKnowComponent] = _
    private var propertyMapper:ComponentMapper[PropertyComponent]=_
    private var instructionMapper:ComponentMapper[PhysicsInstruction]=_

    private var world_state = System.currentTimeMillis()
    private var decouplingProcessor:DecouplingProcessor=_


    var GRAVITY = 10
    private var accumulator: Float = 0
    //     PHYSICS_WORLD = new World(new Vector2(0, GRAVITY), true)
    physicsBodyMapper = ComponentMapper.getFor(classOf[PhysicsBodyComponent])
    transformMapper = ComponentMapper.getFor(classOf[TransformComponent])
    propertyMapper=ComponentMapper.getFor(classOf[PropertyComponent])
    idkMapper = ComponentMapper.getFor(classOf[IdontKnowComponent])
    instructionMapper= ComponentMapper.getFor(classOf[PhysicsInstruction])

//    decouplingProcessor.physicsBodyMapper=physicsBodyMapper
//    decouplingProcessor.propertyMapper=propertyMapper
    def set_world(world: World): Unit = {
        PHYSICS_WORLD = world
        PHYSICS_WORLD.setContactListener(new ContactListener() {
            override def beginContact(contact: Contact): Unit = {
//                Gdx.app.log("PhysicsSystemContact","beginContact start")
                val entity1: Entity = contact.getFixtureA.getBody.getUserData.asInstanceOf[Entity]
                val entity2: Entity = contact.getFixtureB.getBody.getUserData.asInstanceOf[Entity]

                val pbc1 = physicsBodyMapper.get(entity1)
                val pbc2 = physicsBodyMapper.get(entity1)
                // 碰撞后 对两方一视同仁的设置碰撞间隔
                if (pbc2.last_contact > 0 || pbc1.last_contact > 0) {
                    return
                }
                pbc2.last_contact = pbc2.contact_interval
                pbc1.last_contact = pbc1.contact_interval

                decouplingProcessor.decouple_and_process(EventEnum.CONTACT,List(entity1,entity2))
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
        this.engine=engine
        this.decouplingProcessor=DecouplingProcessor(engine)
    }

    override def update(deltaTime: Float): Unit = {
        super.update(deltaTime)
        world_state = System.currentTimeMillis()
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
        update_bodies(deltaTime)
    }

    /**
     * 处理运动的原动力
     * 并且与显示上的同步
     */
    def update_bodies(deltaTime: Float): Unit = {
        val bodies = new GDXarray[Body];
        PHYSICS_WORLD.getBodies(bodies)
        for (body <- bodies.items) {

            val body_entity: Entity = body.getUserData.asInstanceOf[Entity]

            if (body_entity != null) {
//                val idk: IdontKnowComponent = idkMapper.get(body_entity)
//                if (idk != null) body.setLinearVelocity(new Vector2(idk.x_v.toFloat, idk.y_v.toFloat))
                update_transform(body_entity, body)
                update_last_contact(body_entity, deltaTime)
                update_instruction(body_entity,body)
            }
        }
    }
    def update_instruction(body_entity: Entity, body: Body): Unit = {
        val pic=instructionMapper.get(body_entity)
        if(pic==null){
            return
        }
        val instructions=pic.instructions
        for i<- 0 until instructions.length() do{
            val instruction = instructions.get(i)
            instruction match
                case instruction_str: String =>
                    if (instruction_str.equals("CHANGE_DIRECTION")) {
                        body.applyTorque(-20f,true)
//                        Gdx.app.log("PhysicsSystem","change direction")
                    }
                case _ =>
        }

    }

    def update_transform(body_entity: Entity, body: Body): Unit = {
        val tc = transformMapper.get(body_entity)
        if (tc != null) // Update the entities/sprites position and angle
        {
            tc.pos.x = body.getPosition.x
            tc.pos.y = body.getPosition.y
            // We need to convert our angle from radians to degrees
            tc.rotation = body.getAngle
            //Gdx.app.log("my","position "+b.getPosition().x+" "+ b.getPosition().y);
        }
    }

    def update_last_contact(body_entity: Entity, deltaTime: Float): Unit = {
        /**
         * 碰撞终止时间
         */
        val pbc = physicsBodyMapper.get(body_entity)

        pbc.physics_system_state = world_state
        if (pbc.last_contact > 0) {
            pbc.last_contact -= deltaTime
            if (pbc.last_contact <= 0) {
                pbc.last_contact = 0
            }
        }
//        Gdx.app.log("PhysicsSystemUpdate","last_contact "+pbc.last_contact)
    }

}
