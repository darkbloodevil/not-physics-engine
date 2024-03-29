package npe.systems;


import npe.NotBox2D.*;
import com.artemis.ComponentMapper;
import npe.NotBox2D.DecouplingProcessor;
import npe.NotBox2D.EventEnum;
import npe.components.PhysicsBodyComponent;
import npe.components.PhysicsInstruction;
import npe.components.PropertyComponent;
import npe.components.TransformComponent;
import npe.tools.DeltaTimeRecorder;
import com.artemis.Aspect;
import com.artemis.EntitySystem;
import com.artemis.Entity;

import com.badlogic.gdx.physics.box2d.*;
import npe.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class PhysicsSystem extends EntitySystem {
    Logger logger = LoggerFactory.getLogger(PhysicsSystem.class);
    ComponentMapper<PhysicsBodyComponent> physicsBodyMapper;
    com.badlogic.gdx.physics.box2d.World PHYSICS_WORLD;
    private float TIME_STEP = .005f;
    private int VELOCITY_ITERATIONS = 8;
    private int POSITION_ITERATIONS = 10;

    private long world_state = System.currentTimeMillis();
    private final DecouplingProcessor decouplingProcessor = new DecouplingProcessor();

    //        int GRAVITY = 10;
    private float accumulator = 0.0f;

    public PhysicsSystem() {
        super(Aspect.all(PhysicsBodyComponent.class));
    }

    public void set_world(com.badlogic.gdx.physics.box2d.World world) {
        PHYSICS_WORLD = world;
        PHYSICS_WORLD.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                var entity1 = (Entity) contact.getFixtureA().getBody().getUserData();
                var entity2 = (Entity) contact.getFixtureB().getBody().getUserData();

                var pbc1 = physicsBodyMapper.get(entity1);
                var pbc2 = physicsBodyMapper.get(entity2);
                // 碰撞后 对两方一视同仁的设置碰撞间隔
                if (pbc2.last_contact() > 0 || pbc1.last_contact() > 0) {
                    return;
                }
                pbc2.last_contact_$eq(pbc2.contact_interval());
                pbc1.last_contact_$eq(pbc1.contact_interval());
                
                
                decouplingProcessor.decouple_and_process(EventEnum.CONTACT, Arrays.asList(entity1, entity2));
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    @Override
    public void processSystem() {
//        super.update(deltaTime)
        var deltaTime = (float) DeltaTimeRecorder.get_deltaTime(this).get();
//        logger.info("deltaTime "+deltaTime);
        world_state = System.currentTimeMillis();
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        var frameTime = Math.min(deltaTime, 0.25f);

        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            PHYSICS_WORLD.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            //PHYSICS_WORLD.step(deltaTime, 2, 2);
            accumulator -= TIME_STEP;
        }
        update_bodies(deltaTime);
    }

    /**
     * 处理运动的原动力
     * 并且与显示上的同步
     */
    void update_bodies(Float deltaTime) {
        var bodies = new com.badlogic.gdx.utils.Array<Body>();
        PHYSICS_WORLD.getBodies(bodies);
        for (var body : bodies) {
            var body_entity = (Entity) body.getUserData();
            if (body_entity != null) {
                //                val idk: IdontKnowComponent = idkMapper.get(body_entity)
                //                if (idk != null) body.setLinearVelocity(new Vector2(idk.x_v.toFloat, idk.y_v.toFloat))
                update_body(body_entity, body);
                update_instruction(body_entity);
                update_transform(body_entity, body);
                update_last_contact(body_entity, deltaTime);
            }
        }
    }

    /**
     * 更新body当前状态
     * @param body_entity
     * @param body
     */
    public void update_body(Entity body_entity, Body body) {
        var pbc = body_entity.getComponent(PropertyComponent.class);
        if (pbc == null)
            return;
        else {
            var pbp = pbc.property();
            if (pbp.has("direction")) {
                var direction = pbp.getString("direction");
                if (direction.equals("LEFT")) {
                    body.setLinearVelocity(10, 5);
                } else if (direction.equals("RIGHT")) {
                    body.setLinearVelocity(-10, -1);
                }
            }else {
                pbp.put("direction","LEFT");
            }
        }
    }

    /**
     * 依据指令更新实体
     *
     * @param body_entity
     */
    public void update_instruction(Entity body_entity) {
        var pic = body_entity.getComponent(PhysicsInstruction.class);
        if (pic == null) {
            return;
        }
        var instructions = pic.instructions();
        for (int i = 0; i < instructions.length(); i++) {
            var instruction = instructions.get(i);
            if (instruction instanceof String instruction_str) {
                if (instruction_str.equals("CHANGE_DIRECTION")) {
                    var pbc = body_entity.getComponent(PropertyComponent.class);
                    if(pbc!=null){
                        var pbp = pbc.property();
                        if (pbp.has("direction")) {
                            var direction = pbp.getString("direction");
                            if (direction.equals("LEFT")) {
                                pbp.put("direction","RIGHT");
                            } else if (direction.equals("RIGHT")) {
                                pbp.put("direction","LEFT");
                            }
                        }else {
                            pbp.put("direction","LEFT");
                        }
                    }
                    
                }
            }
        }
        // 删除PhysicsInstruction
        body_entity.edit().remove(PhysicsInstruction.class);
    }

    void update_transform(Entity body_entity, Body body) {
        var tc = body_entity.getComponent(TransformComponent.class);
        if (tc != null) // Update the entities/sprites position and angle
        {
            tc.pos().set(body.getPosition().x, body.getPosition().y, tc.pos().z);
            // We need to convert our angle from radians to degrees
            tc.rotation_$eq(body.getAngle());
            //Gdx.app.log("my","position "+b.getPosition().x+" "+ b.getPosition().y);
        }
    }

    void update_last_contact(Entity body_entity, Float deltaTime) {
        /**
         * 碰撞终止时间
         */
        var pbc = physicsBodyMapper.get(body_entity);

        pbc.physics_system_state_$eq(world_state);
        if (pbc.last_contact() > 0) {
            pbc.last_contact_$eq(pbc.last_contact() - deltaTime);
            if (pbc.last_contact() <= 0) {
                pbc.last_contact_$eq(0f);
            }
        }
        //        Gdx.app.log("PhysicsSystemUpdate","last_contact "+pbc.last_contact)
    }

}

