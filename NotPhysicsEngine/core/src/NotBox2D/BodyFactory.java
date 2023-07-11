package NotBox2D;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.JSONDemo;
import org.json.JSONArray;
import org.json.JSONObject;

public class BodyFactory {
    World world;
    public BodyFactory(World world){
        this.world=world;
    }
    public Body get_body(JSONObject jo){
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        if (jo.has("body_type")){
            String body_type=jo.getString("body_type");
            if (body_type.equalsIgnoreCase("dynamic")){
                bodyDef.type=BodyDef.BodyType.DynamicBody;
            } else if (body_type.equalsIgnoreCase("static")) {
                bodyDef.type=BodyDef.BodyType.StaticBody;
            } else if (body_type.equalsIgnoreCase("kinematic")) {
                bodyDef.type=BodyDef.BodyType.KinematicBody;
            }
        }
        if (jo.has("position")){
            JSONArray ja=jo.getJSONArray("position");
            bodyDef.position.set(ja.getFloat(0),ja.getFloat(1));
        }

        // Create our fixture and attach it to the body
        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        Shape shape1=null;
        if (jo.has("shape")){
            String shape=jo.getString("shape");
            if (shape.equalsIgnoreCase("circle")){
                // Create a circle shape and set its radius to 6
                System.out.println("shape1");
                shape1 = new CircleShape();
                shape1.setRadius(jo.getFloat("radius"));
                fixtureDef.shape = shape1;
            }
        }


        // Create a fixture definition to apply our shape to
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit


        body.createFixture(fixtureDef);

        shape1.dispose();

        return body;
    }
}
