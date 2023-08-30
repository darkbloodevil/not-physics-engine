package NotBox2D;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.mygdx.game.NotPhysicsEngineMain;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class GameWorld {
    public World world;
    JSONObject world_json;

    StandardFactory standardFactory;
    BodyFactory bodyFactory;
    float gravity=-10;

    public GameWorld(){
        this.init();
        this.world_json=new JSONObject();
    }
    public GameWorld(JSONObject world_json){
        this.init();
        this.world_json=world_json;
    }
    private void init()
    {
        this.world=new World(new Vector2(0, gravity), true);

        this.standardFactory=new StandardFactory(this);

    }
    public void create(){
        bodyFactory = new BodyFactory(this);
        this.standardFactory.load_factories(bodyFactory);
        this.standardFactory.standardize("XL","M");
        this.world_json=JsonReader.mergeJSONObject(this.standardFactory.standard_jo,this.world_json);
        //set camera
//        NotPhysicsEngineMain.cameras.add(new OrthographicCamera(this.world_json.getFloat("frustum_width"),
//                this.world_json.getFloat("frustum_height")));

        TabularToMap tabularToMap=TabularToMap.INSTANCE;
        String[][] test_tabular=new String[][]{
                new String[]{"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"},
                new String[]{"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"},
                new String[]{"s","s","s","c","s","s","s","s","s","s","s","s","s","s","s","s"},
                new String[]{"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"},
                new String[]{"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"},
                new String[]{"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"},
                new String[]{"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"},
                new String[]{"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"},
                new String[]{"s","s","s","c","s","s","s","s","s","s","s","s","s","s","s","s"},
        };
        String[][] tabular=new String[9][6];
        for (int i = 0; i < tabular.length; i++) {
            for (int j = 0; j < tabular[0].length; j++) {
                tabular[i][j]=test_tabular[8-j][i];
            }
        }
        System.out.println(world_json.getJSONObject("circle").toString());
        System.out.println(world_json.getJSONObject("square").toString());
        System.out.println(world_json.getJSONObject("triangle").toString());

        HashMap<String,String> representation=new HashMap<>();
        representation.put("s","square");
        representation.put("c","circle");
        representation.put("t","triangle");
        HashMap<String,JSONObject> alterMap=new HashMap<>();
        alterMap.put("s",new JSONObject("{\"body_type\":\"static\"}"));

        tabularToMap.center_x=8;
        tabularToMap.center_y=4.5f;
//        tabularToMap.offset_x=-4;
        tabularToMap.offset_y=-3;
        tabularToMap.offset_x=0;
//        tabularToMap.offset_y=0;
        tabularToMap.interval=2f;
        tabularToMap.tabular_to_map(test_tabular,representation,alterMap,this);


        //test_game();
    }

    /**
     * @deprecated
     */
    private void test_game(){
        JSONObject game = JsonReader.read_json_from_path("game.json");

        JSONObject jo = game.getJSONObject("character");
        bodyFactory.get_body(jo);

        bodyFactory.get_body(game.getJSONObject("character_s"));

        bodyFactory.get_body(game.getJSONObject("ground"));

        bodyFactory.get_body(game.getJSONObject("chain_test")).setAwake(true);

        //Body bodyA=bf.get_body(game.getJSONObject("edge_test"));
        //bodyA.setAwake(true);

        bodyFactory.get_body(game.getJSONObject("wall1"));
        bodyFactory.get_body(game.getJSONObject("wall2"));

        JsonReader.sub_jsonObject_json(game,"joint","bodyA");
        JsonReader.sub_jsonObject_json(game,"joint","bodyB");
        bodyFactory.get_joint(game.getJSONObject("joint"));
//        DistanceJointDef defJoint = new DistanceJointDef ();
//        defJoint.length = 0;
//        defJoint.initialize(bodyA, bodyB, new Vector2(0,0), new Vector2(128, 0));
    }

    /**
     * get correspond body directly
     * 直接获取指定的body
     *
     * @param description the body name
     * @return the body
     */
    public Body getBody(String description) {
        return this.bodyFactory.get_body(this.world_json.getJSONObject(description));
    }

    /**
     * get a altered body
     * 通过输入的改变量调整body
     *
     * @param description the body name
     * @param alter       the alter, for example, position : [0,10]
     * @return the body
     */
    public Body getAlteredBody(String description, JSONObject alter) {
        JSONObject altered = this.world_json.getJSONObject(description);
        for (String key : alter.keySet()) {
            altered.put(key, alter.get(key));
        }
        return this.bodyFactory.get_body(altered);
    }
}
