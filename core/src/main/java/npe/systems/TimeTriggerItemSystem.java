package npe.systems;

import npe.NotBox2D.EventEnum;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import npe.components.GameMsgComponent;
import npe.components.TimeTriggerComponent;
import npe.components.TimeTriggerItem;
import org.json.JSONArray;
import npe.tools.DeltaTimeRecorder;


import java.util.Iterator;

public class TimeTriggerItemSystem extends EntityProcessingSystem {
    ComponentMapper<TimeTriggerComponent> timeTriggerMapper;
    public TimeTriggerItemSystem(){
        super(Aspect.all(TimeTriggerComponent.class));
    }
    @Override
    protected void process(Entity entity) {
        var ttc=timeTriggerMapper.get(entity);
        var deltaTime = (float) DeltaTimeRecorder.get_deltaTime(this).get();

        // 使用显式迭代器
        Iterator<TimeTriggerItem> iterator = ttc.timeTriggerItems().iterator();
        while (iterator.hasNext()) {
            TimeTriggerItem element = iterator.next();

            // 计算剩余时间
            var time_left=element.time()-deltaTime;
            //时间到了 触发
            if (time_left<=0){
                var game_msg = entity.edit().create(GameMsgComponent.class);
                var message=game_msg.msg();
                // 如果message没有time trigger这项
                if (!message.has(EventEnum.TIME_TRIGGER.toString())){
                    message.put(EventEnum.TIME_TRIGGER.toString(),new JSONArray());
                }
                message.getJSONArray(EventEnum.TIME_TRIGGER.toString()).put(element.item());

                //移除剩余时间为0的元素
                iterator.remove();
            }else {
                // 时间没到 更新时间
                element.time_$eq(time_left);
            }
        }
        
        // 空了就删除
        if (ttc.timeTriggerItems().isEmpty()){
            entity.edit().remove(ttc);
        }
    }
}
