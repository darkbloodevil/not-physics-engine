package NotBox2D;

import com.badlogic.gdx.physics.box2d.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;

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
    public Body get_body(JSONObject jo){
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
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
        if (jo.has(BODY_POSITION)){
            JSONArray ja=jo.getJSONArray(BODY_POSITION);
            bodyDef.position.set(ja.getFloat(0),ja.getFloat(1));
        }

        // Create our fixture and attach it to the body
        Body body = gameWorld.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        Shape shape1=null;
        if (jo.has(BODY_SHAPE)){
            String shape=jo.getString(BODY_SHAPE);
            if (shape.equalsIgnoreCase("circle")){
                shape1 = new CircleShape();
                shape1.setRadius(jo.getFloat(CIRCLE_RADIUS));
                fixtureDef.shape = shape1;
            } else if (shape.equalsIgnoreCase("polygon")){
                shape1 = new PolygonShape();
                ((PolygonShape)shape1).setAsBox(jo.getFloat(POLYGON_WIDTH), jo.getFloat(POLYGON_HEIGHT));
                fixtureDef.shape = shape1;
            }else if (shape.equalsIgnoreCase("chain")){
                shape1 = new ChainShape();
                double[] temp=jo.getJSONArray("vertices").toList().stream().mapToDouble(i->Double.parseDouble((i).toString())).toArray();
                float[] temp_f=new float[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    temp_f[i]=(float) temp[i];
                }
                ((ChainShape)shape1).createChain(temp_f);
                fixtureDef.shape = shape1;

            }else if (shape.equalsIgnoreCase("edge")){
                shape1 = new EdgeShape();
                double[] temp=jo.getJSONArray("vertices").toList().stream().mapToDouble(i->Double.parseDouble((i).toString())).toArray();
                float[] temp_f=new float[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    temp_f[i]=(float) temp[i];
                }
                ((EdgeShape)shape1).set(temp_f[0],temp_f[1],temp_f[2],temp_f[3]);
                fixtureDef.shape = shape1;

            }
        }


        // Create a fixture definition to apply our shape to
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit


        body.createFixture(fixtureDef);

        assert shape1 != null;
        shape1.dispose();

        return body;
    }
}
