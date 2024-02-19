package npe.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import npe.components.InstructionComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemInstructionT extends EntityProcessingSystem {
    ComponentMapper<InstructionComponent> instructionMapper;
    Logger logger = LoggerFactory.getLogger(SystemInstructionT.class);
    public SystemInstructionT(){
        Aspect.all(InstructionComponent.class);
    }

    @Override
    protected void process(Entity entity) {
        logger.info(instructionMapper.get(entity).instructions().toString());
        instructionMapper.remove(entity);
    }

}
