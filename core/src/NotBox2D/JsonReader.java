package NotBox2D;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Stack;

/**
 * json with extend: self, parent1 [parents of parent1], parent2 [parents of parent2], ...
 * <p>
 * related:
 * <p>
 * first, then related
 *
 * @TODO circular extend
 */
public class JsonReader {
    /**
     * '(', ')', '+', '-', '*', '/','%' are the math operators
     * '$' to indicate num or float or double
     * '[', some arrays ,']'
     * '{', some dictionary,  '}'
     */
    static private final String OPERATOR_STR = "()+-*/%$[]{}";
    static private final char[] OPERATORS = new char[]{'(', ')', '+', '-', '*', '/', '%', '$', '[', ']', '{', '}'};
    static private final char LEFT_BRACKET = '(', RIGHT_BRACKET = '(', ADD = '+', MINUS = '-',
            MULTIPLY = '*', DIVIDE = '/', MOD = '%', TO_NUM = '$', LEFT_ARRAY = '[', RIGHT_ARR = ']', LEFT_DICT = '{', RIGHT_DICT = '}';

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
        target.put("name", tar_str);
        if (target.has("extend")) {
            JSONArray parents = target.getJSONArray("extend");
            for (Object o : parents) {
                String parent = (String) o;
                JSONObject parent_json;
                if (full.has(parent))
                    parent_json = full.getJSONObject(parent);
                else parent_json = jsonObject_json(full, parent, src_json);
                for (String parent_key : parent_json.keySet()) {
                    if (!target.has(parent_key)) {
                        target.put(parent_key, parent_json.get(parent_key));
                    }
                }
            }
        }

        if (target.has("related")) {
            JSONArray ja = target.getJSONArray("related");
            for (Object relate : ja) {
                JSONObject relate_jo = (JSONObject) relate;
                String r_name = relate_jo.getString("name");
                JSONArray content_ja = relate_jo.getJSONArray("content");

                Object relate_content = handle_content(full, target, src_json, content_ja);

                JSONObject usage_jo = relate_jo;
                for (Object usage : relate_jo.getJSONArray("usage")) {
                    String usage_s = (String) usage;
                    // @TODO might cause null value error
                    // if the usage need the value, then get it
                    // else just pass it to the next, which means the property of the previous one
                    usage_jo = usage_jo.getJSONObject(usage_s);
                    if (usage_jo.has(r_name)) {
                        usage_jo.put(r_name, relate_content);
                        usage_jo = relate_jo;
                    }
                }
            }


        }

        full.put(tar_str, target);
        return target;
    }

    /**
     * <p>NOT YET DONE!</p>
     * <p>RELATE TO SELF!!!<p>
     *
     *
     * "daughter.radius","$2","/" means daughter.radius/2 (utilize the reverse-polish-notation )
     *
     *
     *
     * @param full       the full objects
     * @param target     the target that needs to be handled
     * @param src_json   source json, without the "extend" and "relate"
     * @param content_ja content array
     * @return the result
     */
    static Object handle_content(JSONObject full, JSONObject target, JSONObject src_json, JSONArray content_ja) {
        Object relate_content = null;
        Stack operator_stack = null;
        char type=' ';

        for (int i=0;i<content_ja.length();i++) {
            String content_s=(String) content_ja.get(i);
            switch (content_s.charAt(0)) {

            }
        }
        return relate_content;
    }

}
