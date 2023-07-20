package NotBox2D;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class GameWorld {
    public World world;
    public GameWorld(){
        this.world=new World(new Vector2(0, -10), true);
    }
}
