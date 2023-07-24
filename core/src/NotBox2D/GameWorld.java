package NotBox2D;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.json.JSONObject;

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
}
