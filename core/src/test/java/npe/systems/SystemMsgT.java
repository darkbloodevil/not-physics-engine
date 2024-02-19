package npe.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import npe.components.GameMsgComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用来作为测试时截取msg的系统
 * 输出msg然后删除
 */
public class SystemMsgT extends EntityProcessingSystem {
    ComponentMapper<GameMsgComponent> gameMsgMapper;
    Logger logger = LoggerFactory.getLogger(SystemMsgT.class);
    public SystemMsgT(){
        super(Aspect.all(GameMsgComponent.class));
    }

    @Override
    protected void process(Entity entity) {
        logger.info(gameMsgMapper.get(entity).msg().toString());
        gameMsgMapper.remove(entity);
    }
}
