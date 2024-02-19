package npe.NotBox2D;

import npe.NotBox2D.EntityGeneratorManager;
import com.artemis.Entity;
import com.artemis.WorldConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import npe.components.PhysicsBodyComponent;
import npe.components.PropertyComponent;
import npe.game.NotPhysicsEngineGUI;
import npe.tools.JsonReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import npe.systems.MessageProcessingSystem;
import npe.systems.PhysicsSystem;
import npe.tools.DeltaTimeRecorder;
import npe.tools.ExcelReader;
import npe.tools.IdGenerator;

import java.util.HashMap;
import java.util.Iterator;


/**
 * @author darkbloodevil
 * 表格转成实际的地图
 */
class TabularToMap {
    public float interval;
    public float center_x = 8, center_y = 4.5f, offset_x = -0.5f, offset_y = -0.5f;
    public static final String EMPTY = "[EMPTY]";
    GameWorld gameWorld;

    TabularToMap(GameWorld gameWorld) {
        interval = 1;
        this.gameWorld = gameWorld;
    }

    /**
     * 将表格画成地图
     *
     * @param tabular        表格
     * @param representation 表格中元素代表什么
     * @param alterMap       修改表格中内容
     */
    public void tabular_to_map(String[][] tabular, HashMap<String, String> representation,
                               HashMap<String, JSONObject> alterMap) {
        for (int j = 0; j < tabular.length; j++) {
            for (int i = 0; i < tabular[j].length; i++) {
                //获取表格tag对应的实体名
                String tag = tabular[tabular.length - j - 1][i];
                if (tag.equals(EMPTY)) continue;// 空就直接过
                String body_name = representation.get(tag);
                // 获取对应的位置
                float x = (i - offset_x) * interval - center_x;
                float y = (j - offset_y) * interval - center_y;
                //在原型上给予对应的改变量
                JSONObject alter = new JSONObject("{\"position\":[" + x + "," + y + "]}");
                if (alterMap.containsKey(tag)) {
                    alter = JsonReader.merge_json(alter, alterMap.get(tag));
                }
                gameWorld.getAlteredBody(body_name, alter);

            }
        }
    }
}


public class GameWorld {
    /**
     * 该game world的物理世界
     */
    public com.badlogic.gdx.physics.box2d.World world;
    /**
     * 用于表示该game world中万物原型的json
     */
    JSONObject world_prototype_json;

    StandardBuilder standardBuilder;
    BodyBuilder bodyFactory;

    Logger logger = LoggerFactory.getLogger(GameWorld.class);
    float gravity = -10;

    com.artemis.World engine;

    public GameWorld() {
        this.init();
        this.world_prototype_json = new JSONObject();
    }

    public GameWorld(JSONObject world_prototype_json) {
        this.init();
        this.world_prototype_json = world_prototype_json;
    }

    private void init() {


        this.world = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, gravity), true);
        this.initialize_engine();

        this.standardBuilder = new StandardBuilder();

    }

    private void initialize_engine() {
        PhysicsSystem ps = new PhysicsSystem();

        MessageProcessingSystem mps = new MessageProcessingSystem();
//        this.engine.addSystem(ps);
//        this.engine.addSystem(mps);
        this.engine = new com.artemis.World(new WorldConfiguration().setSystem(mps).setSystem(ps));
        ps.set_world(this.world);
        EntityGeneratorManager.add_engine(engine);
    }

    public void create() {
        bodyFactory = new BodyBuilder(this.world_prototype_json, this.world);
        this.standardBuilder.load_builders(bodyFactory);
        String frustum = "M", entity_size = "M";
        this.standardBuilder.standardize(frustum, entity_size);
        this.world_prototype_json = this.standardBuilder.merge_prototype_json(this.world_prototype_json);

        //set camera
        NotPhysicsEngineGUI.cameras.add(new OrthographicCamera(this.world_prototype_json.getFloat("frustum_width"),
                this.world_prototype_json.getFloat("frustum_height")));

        TabularToMap tabularToMap = new TabularToMap(this);

        HashMap<String, String> representation = new HashMap<>();
        HashMap<String, JSONObject> alterMap = new HashMap<>();
//        alterMap.put("s", new JSONObject("{\"body_type\":\"static\"}"));

        tabularToMap.center_x = this.world_prototype_json.getFloat("frustum_width") / 2;
        tabularToMap.center_y = this.world_prototype_json.getFloat("frustum_height") / 2;

        JSONObject jsonObject = ExcelReader.excel_to_json("excel_files/basic_game_map.xlsx");
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
//            System.out.println("\n" + alter_key);
            alterMap.put(alter_key, alter_jo.getJSONObject(alter_key));
        }


        // 实体间距
        tabularToMap.interval = world_prototype_json.getJSONObject("intervals").getFloat(entity_size) *
                                world_prototype_json.getJSONObject("intervals").getFloat("scale");
        tabularToMap.tabular_to_map(test_tabular, representation, alterMap);

//        id_to_body.keySet().forEach(System.out::println);
        //test_game();
    }

    public void update(float delta_time) {
//        engine.update(delta_time);
//        }
        DeltaTimeRecorder.set_deltaTime(engine, delta_time);
        engine.process();
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

        boolean waaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa = false;
        for (String key : alter.keySet()) {
            altered.put(key, alter.get(key));
            // 如果有对应eid，则加入
            if (key.equals("eid")) {
//                this.eid_to_id.put(alter.getString("eid"), body_id);
                waaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa = true;
            }

        }
        // 生成body
        Body body = this.bodyFactory.get_body(altered);
        Entity e = engine.createEntity();
        PhysicsBodyComponent pbc = e.edit().create(PhysicsBodyComponent.class);


        pbc.body_id_$eq(IdGenerator.INSTANCE.nextId());
        pbc.body_$eq(body);
//        e.add(pbc);
        if (waaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa) {
            PropertyComponent pc = e.edit().create(PropertyComponent.class);
            JSONObject property_json = new JSONObject();
            property_json.put("name", "big");
            pc.property_$eq(property_json);
//            e.add(pc);
        }
        body.setUserData(e);
//        // 给出id到body的map
//        this.id_to_body.put(body_id, body);

        return body;
    }

}
