package systems;

import com.artemis.Aspect;
import com.artemis.EntitySystem;

/**
 * 统一处理各种消息
 */
public class MessageHandlingSystem  extends EntitySystem {
    public MessageHandlingSystem(){
        super(Aspect.exclude());
    }

    @Override
    protected void processSystem() {
        
    }
}
