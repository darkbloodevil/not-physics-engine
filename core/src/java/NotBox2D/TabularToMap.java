package NotBox2D;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author darkbloodevil
 * 表格转成实际的地图
 */
public enum TabularToMap {
    INSTANCE;
    public float interval;
    public float center_x=8,center_y=4.5f,offset_x=-0.5f,offset_y=-0.5f;
    public static final String EMPTY="[EMPTY]";

    TabularToMap(){
        interval=1;
    }

    /**
     * 将表格画成地图
     * @param tabular
     * @param representation
     * @param alterMap
     * @param gameWorld
     */
    public void tabular_to_map(String[][] tabular, HashMap<String,String> representation,
                               HashMap<String,JSONObject> alterMap,GameWorld gameWorld){
        for (int j = 0; j < tabular.length; j++) {
            for (int i = 0; i < tabular[j].length; i++) {
                //获取表格tag对应的实体名
                String tag=tabular[tabular.length-j-1][i];
                if (tag.equals(EMPTY))continue;// 空就直接过
                String body_name=representation.get(tag);
                // 获取对应的位置
                float x=(i-offset_x)*interval-center_x;
                float y=(j-offset_y)*interval-center_y;
                //在原型上给予对应的改变量
                JSONObject alter=new JSONObject("{\"position\":["+x+","+y+"]}");
                if (alterMap.containsKey(tag)){
                    alter=JsonReader.mergeJSONObject(alter,alterMap.get(tag));
                }
                gameWorld.getAlteredBody(body_name,alter);

            }
        }
    }
}
