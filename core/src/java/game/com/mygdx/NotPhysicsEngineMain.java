package game.com.mygdx;

import NotBox2D.GameWorld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Stack;

public class NotPhysicsEngineMain extends Game {
    SpriteBatch batch;
    Texture img;
    GameWorld gameWorld;

    Box2DDebugRenderer debugRenderer;
    /**
     * @TODO 临时的记得改
     */
    public static Stack<OrthographicCamera> cameras=new Stack<>();
    /**
     * 是把屏幕划分成多少份
     */
    static final float FRUSTUM_WIDTH = 48;
    static final float FRUSTUM_HEIGHT = 27;

    @Override
    public void create() {


        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        gameWorld = new GameWorld();
        OrthographicCamera cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        cameras.add(cam);
        debugRenderer = new Box2DDebugRenderer();

        gameWorld.create();

    }

    @Override
    public void render() {
        OrthographicCamera cam=cameras.peek();
        batch.setProjectionMatrix(cam.combined);
        ScreenUtils.clear(.1f, 0.1f, 0.4f, 1);
//        gameWorld.world.step(1 / 60f, 6, 2);
        debugRenderer.render(gameWorld.world, cam.combined);
        batch.begin();
        batch.end();
        gameWorld.update(1/60f);
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}
