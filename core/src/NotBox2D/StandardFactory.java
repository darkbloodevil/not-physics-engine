package NotBox2D;

import com.badlogic.gdx.physics.box2d.Body;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * create standardized objects
 */
public class StandardFactory {
    GameWorld gameWorld;
    /**
     * 标准化的各个物件。例如标准化的圆、方。是运行时使用的
     */
    JSONObject standard_jo;
    BodyFactory bodyFactory;
    static String JSONS = "jsons";

    public final static String XL="XL",L="L",M="M",S="S",SS="SS";


    public StandardFactory(GameWorld world) {
        this.gameWorld = world;

        load_local_standard_json("jsons/standard_file.json");
    }

    public StandardFactory(GameWorld world, String standard_path) {
        this.gameWorld = world;
        load_local_standard_json(standard_path);

    }

    /**
     * 读取本地的标准化文件
     * @param standard_path the path of standard file
     */
    private void load_local_standard_json(String standard_path) {
        // 读取标准化文件
        String standard_file;
        try {
            standard_file = new String(Files.readAllBytes(Paths.get(standard_path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.standard_jo = JsonReader.read_str_json(standard_file);
        // 读取标准化文件中链接的其他文件
        if (this.standard_jo.has(JSONS)) {
            ArrayList<String> searched=new ArrayList<>();
            Queue<String> to_loads=new LinkedList<>();
            for (Object i:this.standard_jo.getJSONArray(JSONS))
                to_loads.offer((String) i);
            while (!to_loads.isEmpty()){
                String top=to_loads.poll();
                searched.add(top);
                String json_file;
                try {
                    json_file = new String(Files.readAllBytes(Paths.get(top)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                JSONObject temp=JsonReader.read_str_json(json_file);
                // 读取链接文件中链接的文件
                if (temp.has(JSONS)){
                    for (Object i:this.standard_jo.getJSONArray(JSONS))
                    {
                        if (!to_loads.contains((String)i) && !searched.contains((String)i)){
                            to_loads.offer((String) i);
                        }
                    }
                }
                this.standard_jo=JsonReader.mergeJSONObject(this.standard_jo,temp);

            }
        }
    }

    /**
     * read json file from path
     * @param json_path the path of json file
     * @return the json object
     */
    private JSONObject read_json_from_path(String json_path){
        String standard_file;
        try {
            standard_file = new String(Files.readAllBytes(Paths.get(json_path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JsonReader.read_str_json(standard_file);

    }
    public void load_factories(BodyFactory bodyFactory) {
        this.bodyFactory = bodyFactory;
    }

    public void standardize(String particle_size){

    }

    /**
     * get correspond body directly
     * 直接获取指定的body
     *
     * @param description the body name
     * @return the body
     */
    public Body getBody(String description) {
        return this.bodyFactory.get_body(this.standard_jo.getJSONObject(description));
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
        JSONObject altered = this.standard_jo.getJSONObject(description);
        for (String key : alter.keySet()) {
            altered.put(key, alter.get(key));
        }
        return this.bodyFactory.get_body(altered);
    }

}
