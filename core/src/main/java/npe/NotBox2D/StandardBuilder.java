package npe.NotBox2D;

import npe.tools.JsonReader;
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
    /**
     * 标准化的各个物件。例如标准化的圆、方。是运行时使用的
     */
    JSONObject standard_basic_entities_jo;
    BodyBuilder bodyBuilder;
    static String JSONS = "jsons";

    public final static String XL = "XL", L = "L", M = "M", S = "S", SS = "SS";
    public final static String[] SHAPE_SIZES = new String[]{"L", "M", "S"};

    String standard_json_path = "json_files/standard_file.json";

    /**
     * 从默认路径载入标准化配置
     */
    public StandardBuilder() {
        load_local_standard_json(standard_json_path);
        this.init();
    }

    /**
     * 从自定义路径载入标准化配置
     *
     * @param standard_path 自定义路径
     */
    public StandardBuilder(String standard_path) {
        load_local_standard_json(standard_path);
        this.init();
    }

    /**
     * 初始化（默认世界大小为M，粒子大小也为M）。如需修改则手动调用standardize函数
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
        this.standard_basic_entities_jo = JsonReader.standard_json_from_path(standard_path);
        // 读取标准化文件中链接的其他文件
        if (this.standard_basic_entities_jo.has(JSONS)) {
            ArrayList<String> searched = new ArrayList<>();
            Queue<String> to_loads = new LinkedList<>();
            for (String key : this.standard_basic_entities_jo.getJSONObject(JSONS).keySet()) {
                if (!key.equals("name"))
                    to_loads.offer(this.standard_basic_entities_jo.getJSONObject(JSONS).getString(key));
            }
            //to_loads.forEach(i->System.out.println(i));
            while (!to_loads.isEmpty()) {
                String top = to_loads.poll();
                searched.add(top);

                JSONObject temp = JsonReader.standard_json_from_path(top);
                // 读取链接文件中链接的文件
                if (temp.has(JSONS)) {
                    for (String key : temp.getJSONObject(JSONS).keySet()) {
                        String file_name = temp.getJSONObject(JSONS).getString(key);
                        if (!to_loads.contains(file_name) && !searched.contains(file_name)) {
                            to_loads.offer(file_name);
                        }
                    }
                }
                // 将链接的文件与现有的合并
                this.standard_basic_entities_jo = JsonReader.merge_json(this.standard_basic_entities_jo, temp);

            }
        }
    }

    /**
     * 载入工厂
     *
     * @param bodyBuilder body创建器
     */
    public void load_builders(BodyBuilder bodyBuilder) {
        this.bodyBuilder = bodyBuilder;
    }

    /**
     * 设置标准化参数（标准化正方形、三角形、……
     *
     * @param frustum     分成几块
     * @param entity_size 基本单位大小
     */
    public void standardize(String frustum, String entity_size) {
        //划分几块 进行标准化
        standard_basic_entities_jo.put("frustum_height", standard_basic_entities_jo.getJSONObject("frustums").get(frustum + "-FRUSTUM"));
        standard_basic_entities_jo.put("frustum_width", SCALE_WIDTH / SCALE_HEIGHT *
                                                        (int) standard_basic_entities_jo.getJSONObject("frustums").get(frustum + "-FRUSTUM"));

        //基本实体大小 进行标准化
        String[] shapes = new String[]{"circle", "square", "matrix-3h", "matrix-3v", "triangle",
                "left-triangle", "right-triangle", "equilateral-triangle"};

        for (String shape : shapes) {
            //放入标准化的entity（比如L是标准的，那circle就是L-circle）
            standard_basic_entities_jo.put(shape, standard_basic_entities_jo.get(entity_size + "-" + shape));
            for (String size : SHAPE_SIZES) {
                //放入全类型的entity（简单来说就是写的多一些）
                standard_basic_entities_jo.put(size + "-" + shape, standard_basic_entities_jo.get(size + "-" + shape));
            }
        }
    }

    /**
     * 合并读取标准化的内容和gameworld自带的内容
     *
     * @param world_origin gameworld自带的json内容
     * @return 合并后完整的内容
     */
    public JSONObject merge_prototype_json(JSONObject world_origin) {
        return JsonReader.merge_json(world_origin, this.standard_basic_entities_jo);
    }

}
