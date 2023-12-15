package tools;

import exceptions.JsonInheritanceException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * json with extend: self, parent1 [parents of parent1], parent2 [parents of parent2], ...
 * <p>
 * related:
 * <p>
 * first, then related
 * <p>
 * 核心是用来处理带有继承关系的json（所以不要任何地方都使用）
 *
 * @TODO circular extend
 */
public class JsonReader {
    static final String ON_PROCESSING_TOKEN = "ON PROCESSING";

    /**
     * 文本转json（要解析成规范json的，就是有命名而且展开extend）
     * translate the json in str to JSONObject and expand the "extend" tag to meet the requirement
     *
     * @param src_str source string
     * @return the result JSONObject
     */
    public static JSONObject read_str_json(String src_str){
        JSONObject result = new JSONObject();
        // the direct translation of str to json
        JSONObject source = new JSONObject(src_str);
        // Convert each key in the source json into a formatted JSONObject that meets the requirements
        for (String key : source.keySet()) {
            if (!result.has(key)) {
                try {
                    standardizing_json(result, key, source);
                } catch (JsonInheritanceException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    /**
     * 有点复杂，大概是有一个总的json，然后我们输入的tar_str是其中一个我们要展开的部分，然后full是我们展开完成后的
     *
     * @param full     the result JSON
     * @param tar_str  the target string to convert
     * @param src_json the json before reading the tar_str
     * @return tar_str展开后的json
     */
    private static JSONObject standardizing_json(JSONObject full, String tar_str, JSONObject src_json) throws JsonInheritanceException {
        // 获取目标 JSON 对象
        JSONObject target = src_json.getJSONObject(tar_str);
        /** 23/12/14更新，好像没什么用就去掉了
         * // 为目标 JSON 对象设置 "name" 属性，其值为目标字符串 tar_str
         *         target.put("name", tar_str);
         */

        // 如果目标 JSON 对象具有 "extend" 属性，表示存在继承关系
        if (target.has("extend")) {
            // 获取继承的父项数组。这类处理使其同时支持String和Array的写法
            Object parents_o = target.get("extend");
            JSONArray parents = new JSONArray();
            if (parents_o instanceof JSONArray) {
                parents = (JSONArray) parents_o;
            }else {
                parents.put(parents_o);
            }

            // 遍历继承的每一个父项
            for (Object o : parents) {
                String parent = (String) o;
                JSONObject parent_json;

                full.put(tar_str, ON_PROCESSING_TOKEN);
                // 如果全局 JSON 对象中已经存在这个父项，则直接获取
                if (full.has(parent)) {
                    if (full.get(parent).equals(ON_PROCESSING_TOKEN)) {
                        throw new JsonInheritanceException("circular inheritance");
                    }
                    parent_json = full.getJSONObject(parent);
                }
                // 否则，递归调用自身，处理父项 
                else parent_json = standardizing_json(full, parent, src_json);
                // 将父项的属性合并到目标 JSON 对象中，确保不覆盖目标对象已有的属性
                for (String parent_key : parent_json.keySet()) {
                    if (!target.has(parent_key)) {
                        target.put(parent_key, parent_json.get(parent_key));
                    }
                }
            }
        }

        full.put(tar_str, target);
        return target;
    }


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
     * 按照规范格式读取json文件
     * read json file from path
     *
     * @param json_path the path of json file。
     * @return the json object
     */
    public static JSONObject standard_json_from_path(String json_path) {
        String standard_file;
        try {
            standard_file = new String(Files.readAllBytes(Paths.get(json_path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JsonReader.read_str_json(standard_file);
    }

    /**
     * 直接读取json文件，不带任何额外判断
     * read json file from path
     *
     * @param json_path the path of json file。
     * @return the json object
     */
    public static JSONObject read_json_from_path(String json_path) {
        String standard_file;
        try {
            standard_file = new String(Files.readAllBytes(Paths.get(json_path)));
            return new JSONObject(standard_file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}


///**
// * 指定读出json的子项
// * @param full
// * @param tar_str the JSONObject
// * @param sub_str the sub
// * @return
// */
//public static JSONObject sub_jsonObject_json(JSONObject full, String tar_str,
//                                             String sub_str) {
//    // 获取目标 JSON 对象
//    JSONObject target = full.getJSONObject(tar_str);
//    JSONObject sub = target.getJSONObject(sub_str);
//    // 为目标 JSON 对象和子项都设置 "name" 属性
//    target.put("name", tar_str);
//    sub.put("name", sub_str);
//    // 如果子项具有 "extend" 属性，表示存在继承关系
//    if (sub.has("extend")) {
//        // 获取继承的父项数组
//        JSONArray sub_parents = sub.getJSONArray("extend");
//        // 遍历继承的每一个父项
//        for (Object o : sub_parents) {
//            String sub_parent = (String) o;
//            JSONObject sub_parent_json;
//            // 如果全局 JSON 对象中已经存在这个父项，则直接获取
//            if (full.has(sub_parent)) {
//                sub_parent_json = full.getJSONObject(sub_parent);
//                for (String parent_key : sub_parent_json.keySet()) {
//                    if (!sub.has(parent_key)) {
//                        sub.put(parent_key, sub_parent_json.get(parent_key));
//                    }
//                }
//            }
//        }
//    }
//    target.put(sub_str, sub);
//    full.put(tar_str, target);
//    return sub;
//}

//    /**
//     * '(', ')', '+', '-', '*', '/','%' are the math operators
//     * '$' to indicate num or float or double
//     * '[', some arrays ,']'
//     * '{', some dictionary,  '}'
//     */
//    static private final String OPERATOR_STR = "()+-*/%$[]{}";
//    static private final char[] OPERATORS = new char[]{'(', ')', '+', '-', '*', '/', '%', '$', '[', ']', '{', '}'};
//    static private final char LEFT_BRACKET = '(', RIGHT_BRACKET = '(', ADD = '+', MINUS = '-',
//            MULTIPLY = '*', DIVIDE = '/', MOD = '%', TO_NUM = '$', LEFT_ARRAY = '[', RIGHT_ARR = ']', LEFT_DICT = '{', RIGHT_DICT = '}';

//if (target.has("related")) {
//        JSONArray ja = target.getJSONArray("related");
//        for (Object relate : ja) {
//        JSONObject relate_jo = (JSONObject) relate;
//        String r_name = relate_jo.getString("name");
//        JSONArray content_ja = relate_jo.getJSONArray("content");
//
//        Object relate_content = handle_content(full, target, src_json, content_ja);
//
//        JSONObject usage_jo = relate_jo;
//        for (Object usage : relate_jo.getJSONArray("usage")) {
//        String usage_s = (String) usage;
//        // @TODO might cause null value error
//        // if the usage need the value, then get it
//        // else just pass it to the next, which means the property of the previous one
//        usage_jo = usage_jo.getJSONObject(usage_s);
//        if (usage_jo.has(r_name)) {
//        usage_jo.put(r_name, relate_content);
//        usage_jo = relate_jo;
//        }
//        }
//        }
//
//
//        }

//    /**
//     * <p>NOT YET DONE!</p>
//     * <p>RELATE TO SELF!!!<p>
//     *
//     *
//     * "daughter.radius","$2","/" means daughter.radius/2 (utilize the reverse-polish-notation )
//     *
//     *
//     *
//     * @param full       the full objects
//     * @param target     the target that needs to be handled
//     * @param src_json   source json, without the "extend" and "relate"
//     * @param content_ja content array
//     * @return the result
//     */
//    static Object handle_content(JSONObject full, JSONObject target, JSONObject src_json, JSONArray content_ja) {
//        Object relate_content = null;
//        Stack operator_stack = null;
//        char type=' ';
//
//        for (int i=0;i<content_ja.length();i++) {
//            String content_s=(String) content_ja.get(i);
//            switch (content_s.charAt(0)) {
//
//            }
//        }
//        return relate_content;
//    }
