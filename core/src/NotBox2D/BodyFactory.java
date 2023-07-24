package NotBox2D;

import com.badlogic.gdx.physics.box2d.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

public class BodyFactory {
    GameWorld gameWorld;
    public static String BODY_TYPE="body_type";
    public static String BODY_POSITION="position";
    public static String BODY_SHAPE="shape";

    public static String CIRCLE_RADIUS="radius";

    public static String POLYGON_WIDTH="width";
    public static String POLYGON_HEIGHT="height";

    public BodyFactory(GameWorld world){
        this.gameWorld =world;
    }

    /**
     * using a JSONObject to create body
     * @param jo the json of body data
     * @return the body you create
     */
    public Body get_body(JSONObject jo){
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();

        // set body type
        if (jo.has(BODY_TYPE)){
            String body_type=jo.getString(BODY_TYPE);
            if (body_type.equalsIgnoreCase("dynamic")){
                bodyDef.type=BodyDef.BodyType.DynamicBody;
            } else if (body_type.equalsIgnoreCase("static")) {
                bodyDef.type=BodyDef.BodyType.StaticBody;
            } else if (body_type.equalsIgnoreCase("kinematic")) {
                bodyDef.type=BodyDef.BodyType.KinematicBody;
            }
        }
        // set position
        if (jo.has(BODY_POSITION)){
            JSONArray ja=jo.getJSONArray(BODY_POSITION);
            bodyDef.position.set(ja.getFloat(0),ja.getFloat(1));
        }

        // create the body in the game world
        Body body = gameWorld.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        // body shape
        Shape shape1=null;
        if (jo.has(BODY_SHAPE)){
            String shape=jo.getString(BODY_SHAPE);
            if (shape.equalsIgnoreCase("circle")){
                shape1 = new CircleShape();
                shape1.setRadius(jo.getFloat(CIRCLE_RADIUS));
            } else if (shape.equalsIgnoreCase("polygon")){
                shape1 = new PolygonShape();
                ((PolygonShape)shape1).setAsBox(jo.getFloat(POLYGON_WIDTH), jo.getFloat(POLYGON_HEIGHT));
            }else if (shape.equalsIgnoreCase("chain")){
                shape1 = new ChainShape();
                double[] temp=jo.getJSONArray("vertices").toList().stream().mapToDouble(i->Double.parseDouble((i).toString())).toArray();
                float[] temp_f=new float[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    temp_f[i]=(float) temp[i];
                }
                ((ChainShape)shape1).createChain(temp_f);

            }else if (shape.equalsIgnoreCase("edge")){
                shape1 = new EdgeShape();
                double[] temp=jo.getJSONArray("vertices").toList().stream().mapToDouble(i->Double.parseDouble((i).toString())).toArray();
                float[] temp_f=new float[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    temp_f[i]=(float) temp[i];
                }
                ((EdgeShape)shape1).set(temp_f[0],temp_f[1],temp_f[2],temp_f[3]);
            }
            fixtureDef.shape = shape1;
        }


        // density kg/m^2
        if(jo.has("density")){
            fixtureDef.density =jo.getFloat("density");
        }else fixtureDef.density = 0.5f;

        // friction  [0,1]
        if(jo.has("friction")){
            fixtureDef.friction =jo.getFloat("friction");
        }else fixtureDef.friction = 0.5f;

        // bounce [0,1]
        if(jo.has("restitution")){
            fixtureDef.restitution =jo.getFloat("restitution");
        }else fixtureDef.restitution = 0.5f;

        // A sensor shape collects contact information but never generates a collision response.
        if(jo.has("isSensor")){
            fixtureDef.isSensor =jo.getBoolean("isSensor");
        }

        body.createFixture(fixtureDef);

        assert shape1 != null;
        shape1.dispose();

        // body awake
        if (jo.has("awake")){
            if (jo.getBoolean("awake")){
                body.setAwake(true);
            }
        }



        return body;
    }

    /**
     * 通过world里面的json自动加载所有body
     * @return result
     */
    public HashMap<String,Body> auto_get_bodies(){
        JSONArray ja=gameWorld.world_json.getJSONArray("body_list");
        HashMap<String,Body> result;
        result = new HashMap<>();
        for (Object o:ja) {
            String a=(String) o;
            result.put(a,get_body(gameWorld.world_json.getJSONObject(a)));
        }
        return result;
    }
}
