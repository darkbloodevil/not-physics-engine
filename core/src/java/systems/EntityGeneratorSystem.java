package systems;

import com.artemis.Aspect;
import com.artemis.EntitySystem;
import com.artemis.World;
import NotBox2D.EntityGenerator;

/**
 * 用于生成entity
 */
public class EntityGeneratorSystem extends EntitySystem {
    public EntityGeneratorSystem(){
        super(Aspect.exclude());
    }

    @Override
    protected void processSystem() {
        EntityGenerator.generate(this);
    }

}
