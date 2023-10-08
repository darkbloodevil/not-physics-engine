package NotBox2D;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import game.com.mygdx.NotPhysicsEngineMain;
import org.json.JSONArray;
import org.json.JSONObject;
import tools.IdGenerator;

import java.util.HashMap;
import java.util.Iterator;

public class GameWorld {
    /**
     * 该gameworld的物理世界
     */
    public World world;
    /**
     * 用于表示该gameworld中万物原型的json
     */
    JSONObject world_prototype_json;

    StandardBuilder standardBuilder;
    BodyBuilder bodyFactory;
    float gravity = -10;

    HashMap<Long, Body> id_to_body;
    HashMap<String, Long> eid_to_id;

    public GameWorld() {
        this.init();
        this.world_prototype_json = new JSONObject();
    }

    public GameWorld(JSONObject world_prototype_json) {
        this.init();
        this.world_prototype_json = world_prototype_json;
    }

    private void init() {
        this.id_to_body = new HashMap<>();
        this.eid_to_id = new HashMap<>();


        this.world = new World(new Vector2(0, gravity), true);

        this.standardBuilder = new StandardBuilder(this);


    }

    public void create() {
        bodyFactory = new BodyBuilder(this);
        this.standardBuilder.load_builders(bodyFactory);
        String frustum = "L", entity_size = "M";
        this.standardBuilder.standardize(frustum, entity_size);
        this.world_prototype_json = JsonReader.mergeJSONObject(this.standardBuilder.standard_jo, this.world_prototype_json);
        //set camera
        NotPhysicsEngineMain.cameras.add(new OrthographicCamera(this.world_prototype_json.getFloat("frustum_width"),
                this.world_prototype_json.getFloat("frustum_height")));

        TabularToMap tabularToMap = TabularToMap.INSTANCE;

        HashMap<String, String> representation = new HashMap<>();
        HashMap<String, JSONObject> alterMap = new HashMap<>();
//        alterMap.put("s", new JSONObject("{\"body_type\":\"static\"}"));

        tabularToMap.center_x = 8;
        tabularToMap.center_y = 4.5f;

        tabularToMap.offset_y = -0.5f;
        tabularToMap.offset_x = -0.5f;

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
        tabularToMap.interval = world_prototype_json.getJSONObject("intervals").getFloat(entity_size) *
                world_prototype_json.getJSONObject("intervals").getFloat("scale");
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
        // 改变量
        JSONObject altered = this.world_prototype_json.getJSONObject(description);
        // 对body给予一个id
        Long body_id = set_body_id(altered);
        for (String key : alter.keySet()) {
            altered.put(key, alter.get(key));
            // 如果有对应eid，则加入
            if (key.equals("eid")){
                this.eid_to_id.put(alter.getString("eid"), body_id);
            }
        }
        // 生成body
        Body body = this.bodyFactory.get_body(altered);
        // 给出id到body的map
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
