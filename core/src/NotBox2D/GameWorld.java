package NotBox2D;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameWorld {
    public World world;
    JSONObject world_json;
    public GameWorld(){
        this.world=new World(new Vector2(0, -10), true);
        this.world_json=new JSONObject();
    }
    public GameWorld(JSONObject world_json){
        this.world=new World(new Vector2(0, -10), true);
        this.world_json=world_json;
    }
    public void create(){
        BodyFactory bf = new BodyFactory(this);

        String game_content;
        try {
            game_content = new String(Files.readAllBytes(Paths.get("game.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject game = JsonReader.read_str_json(game_content);

        JSONObject jo = game.getJSONObject("character");
        bf.get_body(jo);


        bf.get_body(game.getJSONObject("ground"));

//        bf.get_body(game.getJSONObject("chain_test")).setAwake(true);

        //Body bodyA=bf.get_body(game.getJSONObject("edge_test"));
        //bodyA.setAwake(true);

        bf.get_body(game.getJSONObject("wall1"));
        bf.get_body(game.getJSONObject("wall2"));

        JsonReader.sub_jsonObject_json(game,"joint","bodyA");
        JsonReader.sub_jsonObject_json(game,"joint","bodyB");
        bf.get_joint(game.getJSONObject("joint"));
//        DistanceJointDef defJoint = new DistanceJointDef ();
//        defJoint.length = 0;
//        defJoint.initialize(bodyA, bodyB, new Vector2(0,0), new Vector2(128, 0));
    }
}
