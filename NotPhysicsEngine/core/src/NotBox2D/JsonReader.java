package NotBox2D;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * json with extend: self, parent1 [parents of parent1], parent2 [parents of parent2], ...
 */
public class JsonReader {
    public static JSONObject read_str_json(String src_str) {
        JSONObject result = new JSONObject();
        JSONObject source = new JSONObject(src_str);
        for (String key : source.keySet()) {
            if (!result.has(key))
                jsonObject_json(result, key, source);
        }
        return result;
    }

    static JSONObject jsonObject_json(JSONObject full, String tar_str, JSONObject src_json) {
        JSONObject target = src_json.getJSONObject(tar_str);
        target.put("name",tar_str);
        if (target.has("extend")){
            JSONArray parents=target.getJSONArray("extend");
            for (Iterator<Object> it = parents.iterator(); it.hasNext(); ) {
                String parent = (String) it.next();
                JSONObject parent_json;
                if (full.has(parent))
                    parent_json=full.getJSONObject(parent);
                else parent_json=jsonObject_json(full,parent,src_json);
                for (String parent_key:parent_json.keySet()) {
                    if (!target.has(parent_key)){
                        target.put(parent_key,parent_json.get(parent_key));
                    }
                }
            }
        }
        full.put(tar_str,target);
        return target;
    }
}
