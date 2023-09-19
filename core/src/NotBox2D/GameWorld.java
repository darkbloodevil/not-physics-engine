package NotBox2D;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.mygdx.game.NotPhysicsEngineMain;
import org.json.JSONArray;
import org.json.JSONObject;
import tools.IdGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GameWorld {
    public World world;
    JSONObject world_json;

    StandardFactory standardFactory;
    BodyFactory bodyFactory;
    float gravity = -10;

    HashMap<Long, Body> id_to_body;
    HashMap<String, Long> eid_to_id;

    public GameWorld() {
        this.init();
        this.world_json = new JSONObject();
    }

    public GameWorld(JSONObject world_json) {
        this.init();
        this.world_json = world_json;
    }

    private void init() {
        this.id_to_body = new HashMap<>();
        this.eid_to_id = new HashMap<>();


        this.world = new World(new Vector2(0, gravity), true);

        this.standardFactory = new StandardFactory(this);


    }

    public void create() {
        bodyFactory = new BodyFactory(this);
        this.standardFactory.load_factories(bodyFactory);
        String frustum = "L", entity_size = "M";
        this.standardFactory.standardize(frustum, entity_size);
        this.world_json = JsonReader.mergeJSONObject(this.standardFactory.standard_jo, this.world_json);
        //set camera
        NotPhysicsEngineMain.cameras.add(new OrthographicCamera(this.world_json.getFloat("frustum_width"),
                this.world_json.getFloat("frustum_height")));

        TabularToMap tabularToMap = TabularToMap.INSTANCE;
//        String[][] test_tabular = new String[][]{
//                new String[]{"s", "s", "s", "c", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s"},
//                new String[]{"s", "0", "0", "0", "0", "mh", "0", "c", "0", "0", "0", "0", "0", "0", "0", "s"},
//                new String[]{"s", "0", "0", "0", "0", "0", "c", "0", "0", "0", "0", "0", "0", "0", "0", "s"},
//                new String[]{"s", "0", "0", "0", "0", "0", "rt", "0", "0", "0", "0", "0", "0", "0", "0", "s"},
//                new String[]{"s", "0", "0", "0", "0", "0", "c", "0", "0", "0", "0", "0", "0", "0", "0", "s"},
//                new String[]{"s", "0", "0", "0", "0", "0", "c", "0", "0", "0", "0", "0", "0", "0", "0", "s"},
//                new String[]{"s", "0", "0", "0", "0", "0", "c", "0", "0", "0", "0", "0", "0", "0", "0", "s"},
//                new String[]{"s", "0", "0", "0", "0", "0", "c", "0", "0", "0", "0", "0", "0", "0", "0", "s"},
//                new String[]{"s", "s", "s", "c", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s"},
//        };


//        String[][] tabular = new String[9][6];
//        for (int i = 0; i < tabular.length; i++) {
//            for (int j = 0; j < tabular[0].length; j++) {
//                tabular[i][5-j] = test_tabular[8 - j][i];
//            }
//        }


        HashMap<String, String> representation = new HashMap<>();
//        representation.put("s", "square");
//        representation.put("c", "circle");
//        representation.put("t", "triangle");
//        representation.put("rt", "left-triangle");
//        representation.put("mh", "matrix-3h");
        HashMap<String, JSONObject> alterMap = new HashMap<>();
//        alterMap.put("s", new JSONObject("{\"body_type\":\"static\"}"));

        tabularToMap.center_x = 8;
        tabularToMap.center_y = 4.5f;
//        tabularToMap.offset_x=-4;

        tabularToMap.offset_y = -0.5f;
        tabularToMap.offset_x = -0.5f;
//        tabularToMap.offset_x = this.world_json.getFloat("frustum_width")/2;
//        tabularToMap.offset_y = -this.world_json.getFloat("frustum_height")/2;

        JSONObject jsonObject = ExcelReader.excel_to_json("excel_test.xlsx");
        JSONArray jsonArray = jsonObject.getJSONArray("game_map");
        String[][] test_tabular = new String[jsonObject.getInt("height")][jsonObject.getInt("width")];
        for (int i = 0; i < jsonObject.getInt("height"); i++) {
            for (int j = 0; j < jsonObject.getInt("width"); j++) {
                test_tabular[i][j] = jsonArray.getJSONArray(i).getString(j);
            }
        }
        JSONObject represent_jo = jsonObject.getJSONObject("represent");
        for (Iterator<String> it = represent_jo.keys(); it.hasNext(); ) {
            String represent_key = it.next();
            representation.put(represent_key, represent_jo.getString(represent_key));
        }
        JSONObject alter_jo = jsonObject.getJSONObject("alter");
        for (Iterator<String> it = alter_jo.keys(); it.hasNext(); ) {
            String alter_key = it.next();
            System.out.println("\n" + alter_key);
            alterMap.put(alter_key, alter_jo.getJSONObject(alter_key));
        }


        // 实体间距
        tabularToMap.interval = world_json.getJSONObject("intervals").getFloat(entity_size) *
                world_json.getJSONObject("intervals").getFloat("scale");
        tabularToMap.tabular_to_map(test_tabular, representation, alterMap, this);

        id_to_body.keySet().forEach(System.out::println);
        //test_game();
    }

    /**
     * @TODO delete
     */
    private void test_game() {
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

        JsonReader.sub_jsonObject_json(game, "joint", "bodyA");
        JsonReader.sub_jsonObject_json(game, "joint", "bodyB");
        bodyFactory.get_joint(game.getJSONObject("joint"));
//        DistanceJointDef defJoint = new DistanceJointDef ();
//        defJoint.length = 0;
//        defJoint.initialize(bodyA, bodyB, new Vector2(0,0), new Vector2(128, 0));
    }

//    /**
//     * @TODO
//     * @return
//     */
//    private String[][] get_game_map(){
//
//
//        return test_tabular;
//    }

    public void update() {
        for (String eid : eid_to_id.keySet()) {
            Body aaa=this.id_to_body.get(this.eid_to_id.get(eid));
            aaa.applyForceToCenter(new Vector2(10,10),true);
        }
    }

    /**
     * get correspond body directly
     * 直接获取指定的body
     *
     * @param description the body name
     * @return the body
     */
    public Body getBody(String description) {
        return getAlteredBody(description, new JSONObject());
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
        Long body_id = set_body_id(altered);
        for (String key : alter.keySet()) {
            altered.put(key, alter.get(key));
            if (key.equals("eid")){
                this.eid_to_id.put(alter.getString("eid"), body_id);

            }
        }


        Body body = this.bodyFactory.get_body(altered);
        this.id_to_body.put(body_id, body);

        return body;
    }

    /**
     * 给予目标id（id:xxxxx）
     *
     * @param jo 被赐予id的目标
     * @return body id
     */
    Long set_body_id(JSONObject jo) {
        Long body_id = IdGenerator.INSTANCE.nextId();
        jo.put("id", body_id);
        return body_id;
    }
}
