package systems;

import com.artemis.Aspect;
import com.artemis.EntitySystem;
import NotBox2D.EntityGeneratorManager;

/**
 * 用于生成entity(事实上写在EntityGenerator里边)
 */
public class EntityGeneratorSystem extends EntitySystem {
    public EntityGeneratorSystem(){
        super(Aspect.exclude());
    }

    /**
     * 调用专门的生成实体模块
     */
    @Override
    protected void processSystem() {
        EntityGeneratorManager.generate(this);
    }

}
