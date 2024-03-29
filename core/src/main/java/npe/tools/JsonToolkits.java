package npe.tools;

import org.json.JSONObject;

public class JsonToolkits {
    /**
     * 将A与B合并，B覆盖A中的重名项
     *
     * @param ja json a
     * @param jb json b
     * @return merged
     */
    public static JSONObject merge_json(JSONObject ja, JSONObject jb) {
        // 将json object中内容转为string后重新转为json object，实现深拷贝
        JSONObject result = new JSONObject(ja.toString());
        JSONObject b = new JSONObject(jb.toString());
        for (String o : b.keySet()
        ) {
            result.put(o, b.get(o));
        }
        return result;
    }

    /**
     * 将B加入A，并且B会覆盖A中的重名项
     *
     * @param ja json a
     * @param jb json b
     */
    public static void append_json(JSONObject ja, JSONObject jb) {
        JSONObject b = new JSONObject(jb.toString());
        for (String o : b.keySet()) {
            ja.put(o, b.get(o));
        }
    }
}
