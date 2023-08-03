package NotBox2D;

import com.badlogic.gdx.physics.box2d.Body;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * create standardized objects
 */
public class StandardFactory {
    GameWorld gameWorld;
    JSONObject standard_jo;
    BodyFactory bodyFactory;

    public StandardFactory(GameWorld world) {
        this.gameWorld = world;

        String standard_file;
        try {
            standard_file = new String(Files.readAllBytes(Paths.get("jsons/standard_file.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.standard_jo = JsonReader.read_str_json(standard_file);

    }

    public StandardFactory(GameWorld world, String standard_path) {
        this.gameWorld = world;

        String standard_file;
        try {
            standard_file = new String(Files.readAllBytes(Paths.get(standard_path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.standard_jo = JsonReader.read_str_json(standard_file);
    }

    public void load_factories(BodyFactory bodyFactory) {
        this.bodyFactory = bodyFactory;
    }

    /**
     * @return
     * @TODO
     */
    public Body getBody(String description) {
        return null;
    }

}
