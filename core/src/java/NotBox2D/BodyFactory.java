package NotBox2D;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class BodyFactory {
    GameWorld gameWorld;
    public static String BODY_TYPE = "body_type";
    public static String BODY_POSITION = "position";
    public static String BODY_SHAPE = "shape";

    public static String CIRCLE_RADIUS = "radius";
    public static String POLYGON_WIDTH = "width";
    public static String POLYGON_HEIGHT = "height";

    public static String JOINT_TYPE = "joint_type";

    public BodyFactory(GameWorld world) {
        this.gameWorld = world;
    }

    /**
     * using a JSONObject to create body
     *
     * @param jo the json of body data
     * @return the body you create
     */
    public Body get_body(JSONObject jo) {
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();

        // set body type
        if (jo.has(BODY_TYPE)) {
            String body_type = jo.getString(BODY_TYPE);
            if (body_type.equalsIgnoreCase("dynamic")) {
                bodyDef.type = BodyDef.BodyType.DynamicBody;
            } else if (body_type.equalsIgnoreCase("static")) {
                bodyDef.type = BodyDef.BodyType.StaticBody;
            } else if (body_type.equalsIgnoreCase("kinematic")) {
                bodyDef.type = BodyDef.BodyType.KinematicBody;
            }
        }
        // set position
        if (jo.has(BODY_POSITION)) {
            JSONArray ja = jo.getJSONArray(BODY_POSITION);
            bodyDef.position.set(ja.getFloat(0), ja.getFloat(1));
        }

        // create the body in the game world
        Body body = gameWorld.world.createBody(bodyDef);
        body.setFixedRotation(false);


        FixtureDef fixtureDef = new FixtureDef();
        // body shape
        Shape shape1 = null;
        if (jo.has(BODY_SHAPE)) {
            String shape = jo.getString(BODY_SHAPE);
            if (shape.equalsIgnoreCase("circle")) {
                shape1 = new CircleShape();
                shape1.setRadius(jo.getFloat(CIRCLE_RADIUS));
            } else if (shape.equalsIgnoreCase("polygon")) {
                shape1 = new PolygonShape();
                if(jo.has(POLYGON_WIDTH)){
                    ((PolygonShape) shape1).setAsBox(jo.getFloat(POLYGON_WIDTH), jo.getFloat(POLYGON_HEIGHT));
                }else if(jo.has("vertices")){
                    double[] temp = jo.getJSONArray("vertices").toList().stream().mapToDouble(i -> Double.parseDouble((i).toString())).toArray();
                    float[] temp_f = new float[temp.length];
                    for (int i = 0; i < temp.length; i++) {
                        temp_f[i] = (float) temp[i];
                    }
                    ((PolygonShape) shape1).set(temp_f);
                }
            } else if (shape.equalsIgnoreCase("chain")) {
                shape1 = new ChainShape();
                double[] temp = jo.getJSONArray("vertices").toList().stream().mapToDouble(i -> Double.parseDouble((i).toString())).toArray();
                float[] temp_f = new float[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    temp_f[i] = (float) temp[i];
                }
                ((ChainShape) shape1).createChain(temp_f);

            } else if (shape.equalsIgnoreCase("edge")) {
                shape1 = new EdgeShape();
                double[] temp = jo.getJSONArray("vertices").toList().stream().mapToDouble(i -> Double.parseDouble((i).toString())).toArray();
                float[] temp_f = new float[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    temp_f[i] = (float) temp[i];
                }
                ((EdgeShape) shape1).set(temp_f[0], temp_f[1], temp_f[2], temp_f[3]);
            }
            fixtureDef.shape = shape1;
        }else {
            throw new NullPointerException("in BodyFactory creating body, what's the shape?");
        }


        // density kg/m^2
        if (jo.has("density")) {
            fixtureDef.density = jo.getFloat("density");
        } else fixtureDef.density = 0.5f;

        // friction  [0,1]
        if (jo.has("friction")) {
            fixtureDef.friction = jo.getFloat("friction");
        } else fixtureDef.friction = 0.5f;

        // bounce [0,1]
        if (jo.has("restitution")) {
            fixtureDef.restitution = jo.getFloat("restitution");
        } else fixtureDef.restitution = 0.5f;

        // A sensor shape collects contact information but never generates a collision response.
        if (jo.has("isSensor")) {
            fixtureDef.isSensor = jo.getBoolean("isSensor");
        }

        body.createFixture(fixtureDef);

        assert shape1 != null;
        shape1.dispose();

        // body awake
        if (jo.has("awake")) {
            body.setAwake(jo.getBoolean("awake"));
        }

        if (jo.has("id")){
            body.setUserData(jo.get("id"));
        }

        return body;
    }

    /**
     * automatically load all the bodies
     * 通过world里面的json自动加载所有body
     *
     * @return result
     */
    public HashMap<String, Body> auto_get_bodies() {
        assert gameWorld.world_json.has("body_list");
        JSONArray ja = gameWorld.world_json.getJSONArray("body_list");
        HashMap<String, Body> result;
        result = new HashMap<>();
        for (Object o : ja) {
            String a = (String) o;
            result.put(a, get_body(gameWorld.world_json.getJSONObject(a)));
        }
        return result;
    }

    /**
     * automatically load all the bodies in the specific group
     * 通过world里面的json自动加载指定组里面的body
     *
     * @param group tha target group
     * @return result
     */
    public HashMap<String, Body> auto_get_bodies(String group) {
        assert gameWorld.world_json.has(group);
        JSONArray ja = gameWorld.world_json.getJSONArray(group);
        HashMap<String, Body> result;
        result = new HashMap<>();
        for (Object o : ja) {
            String a = (String) o;
            result.put(a, get_body(gameWorld.world_json.getJSONObject(a)));
        }
        return result;
    }

    public Joint get_joint(JSONObject jo) {
        JointDef jointDef = new JointDef();
        boolean initialized = false;

        Body bodyA = get_body(jo.getJSONObject("bodyA"));
        Body bodyB = get_body(jo.getJSONObject("bodyB"));

        JSONArray anchorA_arr = jo.getJSONArray("anchorA"), anchorB_arr = jo.getJSONArray("anchorB");
        Vector2 anchorA = new Vector2(anchorA_arr.getFloat(0), anchorA_arr.getFloat(1));
        Vector2 anchorB = new Vector2(anchorB_arr.getFloat(0), anchorB_arr.getFloat(1));

        //https://javadoc.io/doc/com.badlogicgames.gdx/gdx-box2d/latest/com/badlogic/gdx/physics/box2d/JointDef.html
        if (jo.has(JOINT_TYPE)) {
            if (jo.getString(JOINT_TYPE).equals("distance")) {
                jointDef = new DistanceJointDef();
                if (jo.has("length")) ((DistanceJointDef) jointDef).length = jo.getFloat("length");
                if (jo.has("damping")) ((DistanceJointDef) jointDef).dampingRatio = jo.getFloat("damping");
                if (jo.has("frequency")) ((DistanceJointDef) jointDef).frequencyHz = jo.getFloat("frequency");
                ((DistanceJointDef) jointDef).initialize(bodyA, bodyB, anchorA, anchorB);
            } else if (jo.getString(JOINT_TYPE).equals("friction")) {
                jointDef = new FrictionJointDef();
                if (jo.has("force")) ((FrictionJointDef) jointDef).maxForce = jo.getFloat("force");
                if (jo.has("torque")) ((FrictionJointDef) jointDef).maxTorque = jo.getFloat("torque");
                ((FrictionJointDef) jointDef).initialize(bodyA, bodyB, anchorA);
            } else if (jo.getString(JOINT_TYPE).equals("gear")) {
                jointDef = new GearJointDef();
                if (jo.has("ratio")) ((GearJointDef) jointDef).ratio = jo.getFloat("ratio");
                if (jo.has("joint1")) ((GearJointDef) jointDef).joint1 = get_joint(jo.getJSONObject("joint1"));
                if (jo.has("joint2")) ((GearJointDef) jointDef).joint2 = get_joint(jo.getJSONObject("joint2"));
            } else if (jo.getString(JOINT_TYPE).equals("motor")) {
                jointDef = new MotorJointDef();
                if (jo.has("angular")) ((MotorJointDef) jointDef).angularOffset = jo.getFloat("angular");
                if (jo.has("correction")) ((MotorJointDef) jointDef).correctionFactor = jo.getFloat("correction");
                if (jo.has("force")) ((MotorJointDef) jointDef).maxForce = jo.getFloat("force");
                if (jo.has("torque")) ((MotorJointDef) jointDef).maxTorque = jo.getFloat("torque");
                ((MotorJointDef) jointDef).initialize(bodyA, bodyB);
            } else if (jo.getString(JOINT_TYPE).equals("prismatic")) {
                jointDef = new PrismaticJointDef();
                if (jo.has("enableLimit")) ((PrismaticJointDef) jointDef).enableLimit = jo.getBoolean("enableLimit");
                if (jo.has("enableMotor")) ((PrismaticJointDef) jointDef).enableMotor = jo.getBoolean("enableMotor");
                if (jo.has("lowerTranslation"))
                    ((PrismaticJointDef) jointDef).lowerTranslation = jo.getFloat("lowerTranslation");
                if (jo.has("upperTranslation"))
                    ((PrismaticJointDef) jointDef).upperTranslation = jo.getFloat("upperTranslation");
                if (jo.has("force")) ((PrismaticJointDef) jointDef).maxMotorForce = jo.getFloat("force");
                if (jo.has("speed")) ((PrismaticJointDef) jointDef).motorSpeed = jo.getFloat("speed");
                if (jo.has("angle")) ((PrismaticJointDef) jointDef).referenceAngle = jo.getFloat("angle");
                ((PrismaticJointDef) jointDef).initialize(bodyA, bodyB, anchorA, anchorB);
            } else if (jo.getString(JOINT_TYPE).equals("pulley")) {
                jointDef = new PulleyJointDef();
                if (jo.has("lengthA")) ((PulleyJointDef) jointDef).lengthA = jo.getFloat("lengthA");
                if (jo.has("lengthB")) ((PulleyJointDef) jointDef).lengthB = jo.getFloat("lengthB");

                JSONArray groundAnchorA_arr = jo.getJSONArray("ground_anchorA"), groundAnchorB_arr = jo.getJSONArray("ground_anchorB");
                Vector2 groundAnchorA = new Vector2(groundAnchorA_arr.getFloat(0), groundAnchorA_arr.getFloat(1));
                Vector2 groundAnchorB = new Vector2(groundAnchorB_arr.getFloat(0), groundAnchorB_arr.getFloat(1));

                ((PulleyJointDef) jointDef).initialize(bodyA, bodyB,
                        groundAnchorA, groundAnchorB, anchorA, anchorB, jo.getFloat("ratio"));
            } else if (jo.getString(JOINT_TYPE).equals("revolute")) {
                jointDef = new RevoluteJointDef();
                if (jo.has("enableLimit")) ((RevoluteJointDef) jointDef).enableLimit = jo.getBoolean("enableLimit");
                if (jo.has("enableMotor")) ((RevoluteJointDef) jointDef).enableMotor = jo.getBoolean("enableMotor");
                if (jo.has("lowerAngle"))
                    ((RevoluteJointDef) jointDef).lowerAngle = jo.getFloat("lowerAngle");
                if (jo.has("upperAngle"))
                    ((RevoluteJointDef) jointDef).upperAngle = jo.getFloat("upperAngle");
                if (jo.has("torque")) ((RevoluteJointDef) jointDef).maxMotorTorque = jo.getFloat("torque");
                if (jo.has("speed")) ((RevoluteJointDef) jointDef).motorSpeed = jo.getFloat("speed");
                if (jo.has("angle")) ((RevoluteJointDef) jointDef).referenceAngle = jo.getFloat("angle");
                ((RevoluteJointDef) jointDef).initialize(bodyA, bodyB, anchorA);
            } else if (jo.getString(JOINT_TYPE).equals("rope")) {
                jointDef = new RopeJointDef();
                if (jo.has("length")) ((RopeJointDef) jointDef).maxLength = jo.getFloat("length");
            } else if (jo.getString(JOINT_TYPE).equals("weld")) {
                jointDef = new WeldJointDef();
                if (jo.has("damping")) ((WeldJointDef) jointDef).dampingRatio = jo.getFloat("damping");
                if (jo.has("frequency")) ((WeldJointDef) jointDef).frequencyHz = jo.getFloat("frequency");
                if (jo.has("angle")) ((WeldJointDef) jointDef).referenceAngle = jo.getFloat("angle");

                ((WeldJointDef) jointDef).initialize(bodyA, bodyB, anchorA);
            } else if (jo.getString(JOINT_TYPE).equals("wheel")) {
                jointDef = new WheelJointDef();
                if (jo.has("damping")) ((WheelJointDef) jointDef).dampingRatio = jo.getFloat("damping");
                if (jo.has("frequency")) ((WheelJointDef) jointDef).frequencyHz = jo.getFloat("frequency");
                if (jo.has("enableMotor")) ((WheelJointDef) jointDef).enableMotor = jo.getBoolean("enableMotor");
                if (jo.has("torque")) ((WheelJointDef) jointDef).maxMotorTorque = jo.getFloat("torque");
                if (jo.has("speed")) ((WheelJointDef) jointDef).motorSpeed = jo.getFloat("speed");
                ((WheelJointDef) jointDef).initialize(bodyA, bodyB, anchorA, anchorB);
            }
        }


        return this.gameWorld.world.createJoint(jointDef);
    }

}
