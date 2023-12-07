package systems;


import NotBox2D.*;
import com.artemis.*;
import com.artemis.systems.EntityProcessingSystem;
import components.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageProcessingSystem extends EntityProcessingSystem {
    public static String MESSAGE_PROCESSING_SYSTEM_TOKEN = "message processing system";
    ComponentMapper<GameMsgComponent> gameMsgMapper;
    ComponentMapper<PropertyComponent> propertyMapper;
    Logger logger = LoggerFactory.getLogger(Object.class);

    public MessageProcessingSystem(GameWorld gameWorld) {
        super(Aspect.all(GameMsgComponent.class, PropertyComponent.class));
    }

    @Override
    protected void process(Entity entity) {
//        logger.info("message processing");
        
        GameMsgComponent gameMsgComponent = gameMsgMapper.get(entity);
        PropertyComponent propertyComponent = propertyMapper.get(entity);
        JSONObject msg = gameMsgComponent.msg();
        JSONObject property = propertyComponent.property();
        if (msg.has(EventEnum.CONTACT.toString()) && property.has("name")) {
            String contact_tar = msg.getString(EventEnum.CONTACT.toString());
            String name = property.getString("name");

            if (contact_tar.equals("wall") && name.equals("big")) {
//                Gdx.app.log("MessageProcessingSystem", "instruction given")
                var physicsInstruction = entity.edit().create(PhysicsInstruction.class);
                physicsInstruction.instructions().put("CHANGE_DIRECTION");
            }
        }

//        ComponentsManager.update_entity(entity,engine)
    }
}
