package systems;


import NotBox2D.*;
import com.artemis.*;
import com.artemis.systems.EntityProcessingSystem;
import components.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一处理msg
 */
public class MessageProcessingSystem extends EntitySystem {
    public static String MESSAGE_PROCESSING_SYSTEM_TOKEN = "message processing system";

    Logger logger = LoggerFactory.getLogger(MessageProcessingSystem.class);

    ComponentMapper<GameMsgComponent> gameMsgMapper;
    ComponentMapper<PropertyComponent> propertyMapper;

    public MessageProcessingSystem(GameWorld gameWorld) {
        super(Aspect.all(GameMsgComponent.class, PropertyComponent.class));

    }

    @Override
    protected void processSystem() {
        this.getEntities().forEach(this::process_single);
    }

    protected void process_single(Entity entity) {

        GameMsgComponent gameMsgComponent = gameMsgMapper.get(entity);
        PropertyComponent propertyComponent = propertyMapper.get(entity);
        JSONObject msg = gameMsgComponent.msg();
        JSONObject property = propertyComponent.property();
        if (msg.has(EventEnum.CONTACT.toString()) && property.has("name")) {
            String contact_tar = msg.getString(EventEnum.CONTACT.toString());
            String name = property.getString("name");
            //
            if (name.equals("big")) {
                if (contact_tar.equals("wall")) {
                    var physicsInstruction = entity.edit().create(PhysicsInstruction.class);
                    physicsInstruction.instructions().put("CHANGE_DIRECTION");
                } else if (contact_tar.equals("big")) {
                    //                    var instruction_str = " {\"name\":\"small\",\"position\":%s} ";
                    var instruction_str = """
                            {"name":"small","position":%s}
                            """;
                    EntityGenerator.add_instruction(this,
                            new JSONObject(String.format(instruction_str,
                                    msg.getJSONObject("position").toString())));
                }
            }

        }

    }
}
/*

      @Override
 *     protected void process(Entity entity) {
 *
 *         GameMsgComponent gameMsgComponent = gameMsgMapper.get(entity);
 *         PropertyComponent propertyComponent = propertyMapper.get(entity);
 *         JSONObject msg = gameMsgComponent.msg();
 *         JSONObject property = propertyComponent.property();
 *
 *         if (msg.has(EventEnum.CONTACT.toString()) && property.has("name")) {
 *             String contact_tar = msg.getString(EventEnum.CONTACT.toString());
 *             String name = property.getString("name");
 *             //
 *             if (name.equals("big")) {
 *                 if (contact_tar.equals("wall")) {
 *                     var physicsInstruction = entity.edit().create(PhysicsInstruction.class);
 *
 *                     physicsInstruction.instructions().put("CHANGE_DIRECTION");
 *                 } else if (contact_tar.equals("big")) {
 * //                    var instruction_str = " {\"name\":\"small\",\"position\":%s} ";
 *                     var instruction_str = """
 *                             {"name":"small","position":%s}
 *                             """;
 *                     EntityGenerator.add_instruction(this,
 *                             new JSONObject(String.format(instruction_str,
 *                                     msg.getJSONObject("position").toString())));
 *                 }
 *             }
 *
 *         }
 *
 *     }
 */