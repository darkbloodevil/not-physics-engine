package NotBox2D;

import com.badlogic.gdx.physics.box2d.Body;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

/**
 * create standardized objects
 */
public class StandardFactory {
    GameWorld gameWorld;
    JSONObject standard_jo;
    BodyFactory bodyFactory;
    static String JSONS = "jsons";

    public StandardFactory(GameWorld world) {
        this.gameWorld = world;

        load_local_standard_json("jsons/standard_file.json");
    }

    public StandardFactory(GameWorld world, String standard_path) {
        this.gameWorld = world;
        load_local_standard_json(standard_path);

    }

    /**
     * @param standard_path the path of standard file
     */
    private void load_local_standard_json(String standard_path) {
        String standard_file;
        try {
            standard_file = new String(Files.readAllBytes(Paths.get(standard_path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.standard_jo = JsonReader.read_str_json(standard_file);
        if (this.standard_jo.has(JSONS)) {
            ArrayList<String> searched=new ArrayList<>();
            Stack<String> to_loads=new Stack<>();
            for (Object i:this.standard_jo.getJSONArray(JSONS))
                to_loads.add((String) i);
            while (!to_loads.empty()){
                String top=to_loads.pop();
                searched.add(top);
                String json_file;
                try {
                    json_file = new String(Files.readAllBytes(Paths.get(top)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                JSONObject temp=JsonReader.read_str_json(json_file);
                if (temp.has(JSONS)){
                    for (Object i:this.standard_jo.getJSONArray(JSONS))
                    {
                        if (!to_loads.contains((String)i) && !searched.contains((String)i)){
                            to_loads.add((String) i);
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

    /**
     * get correspond body directly
     *
     * @param description the body name
     * @return the body
     */
    public Body getBody(String description) {
        return this.bodyFactory.get_body(this.standard_jo.getJSONObject(description));
    }

    /**
     * get a altered body
     *
     * @param description the body name
     * @param alter       the alter, for example, position : [0,10]
     * @return the body
     */
    public Body getAlteredBody(String description, JSONObject alter) {
        JSONObject altered = this.standard_jo.getJSONObject(description);
        for (String key : alter.keySet()) {
            alter.put(key, alter.get(key));
        }
        return this.bodyFactory.get_body(alter);
    }

}
