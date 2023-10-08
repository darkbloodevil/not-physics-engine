package NotBox2D;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author darkbloodevil
 * 创建标准化的基本组件，用于构筑基本的游戏元素。
 * create standardized objects
 */
public class StandardBuilder {
    float SCALE_WIDTH = 16, SCALE_HEIGHT = 9;
    GameWorld gameWorld;
    /**
     * 标准化的各个物件。例如标准化的圆、方。是运行时使用的
     */
    JSONObject standard_jo;
    BodyBuilder bodyBuilder;
    static String JSONS = "jsons";

    public final static String XL = "XL", L = "L", M = "M", S = "S", SS = "SS";


    public StandardBuilder(GameWorld world) {
        this.gameWorld = world;
        load_local_standard_json("json_files/standard_file.json");
        this.init();
    }

    public StandardBuilder(GameWorld world, String standard_path) {
        this.gameWorld = world;
        load_local_standard_json(standard_path);
        this.init();
    }

    /**
     * 初始化（默认世界大小为M，粒子大小也为M）
     */
    private void init() {
        this.standardize("M", "M");
    }

    /**
     * 读取本地的标准化文件，会将链接的内容一并读入并且合并
     *
     * @param standard_path the path of standard file
     */
    private void load_local_standard_json(String standard_path) {
        // 读取标准化文件
        this.standard_jo = JsonReader.read_json_from_path(standard_path);
        // 读取标准化文件中链接的其他文件
        if (this.standard_jo.has(JSONS)) {
            ArrayList<String> searched = new ArrayList<>();
            Queue<String> to_loads = new LinkedList<>();
            for (String key : this.standard_jo.getJSONObject(JSONS).keySet()) {
                if (!key.equals("name"))
                    to_loads.offer(this.standard_jo.getJSONObject(JSONS).getString(key));
            }
            //to_loads.forEach(i->System.out.println(i));
            while (!to_loads.isEmpty()) {
                String top = to_loads.poll();
                searched.add(top);

                JSONObject temp = JsonReader.read_json_from_path(top);
                // 读取链接文件中链接的文件
                if (temp.has(JSONS)) {
                    for (String key : temp.getJSONObject(JSONS).keySet()) {
                        String file_name=temp.getJSONObject(JSONS).getString(key);
                        if (!to_loads.contains(file_name) && !searched.contains(file_name)) {
                            to_loads.offer(file_name);
                        }
                    }
                }
                // 将链接的文件与现有的合并
                this.standard_jo = JsonReader.mergeJSONObject(this.standard_jo, temp);

            }
        }
    }

    /**
     * 载入工厂
     * @param bodyBuilder body创建器
     */
    public void load_builders(BodyBuilder bodyBuilder) {
        this.bodyBuilder = bodyBuilder;
    }

    /**
     * @param frustum     分成几块
     * @param entity_size 基本单位大小
     */
    public void standardize(String frustum, String entity_size) {
        //划分几块 进行标准化
        standard_jo.put("frustum_height", standard_jo.getJSONObject("frustums").get(frustum + "-FRUSTUM"));
        standard_jo.put("frustum_width", SCALE_WIDTH / SCALE_HEIGHT *
                (int) standard_jo.getJSONObject("frustums").get(frustum + "-FRUSTUM"));

        //基本实体大小 进行标准化
        standard_jo.put("circle", standard_jo.get(entity_size + "-circle"));
        standard_jo.put("square", standard_jo.get(entity_size + "-square"));
        standard_jo.put("matrix-3h", standard_jo.get(entity_size + "-matrix-3h"));
        standard_jo.put("matrix-3v", standard_jo.get(entity_size + "-matrix-3v"));
        standard_jo.put("triangle", standard_jo.get(entity_size + "-triangle"));
        standard_jo.put("left-triangle", standard_jo.get(entity_size + "-left-triangle"));
        standard_jo.put("right-triangle", standard_jo.get(entity_size + "-right-triangle"));
        standard_jo.put("equilateral-triangle", standard_jo.get(entity_size + "-equilateral-triangle"));

    }


}
