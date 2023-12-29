
package game;

import NotBox2D.GameWorld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Stack;

public class NotPhysicsEngineGUI extends Game {
    SpriteBatch batch;

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

        OrthographicCamera cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        cameras.add(cam);
        this.setScreen(new MainScreen(this));
    }

    @Override
    public void render() {
        super.render();

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
class MainScreen extends ScreenAdapter {
    GameWorld gameWorld;

    Box2DDebugRenderer debugRenderer;
    NotPhysicsEngineGUI game;
    MainScreen(NotPhysicsEngineGUI game){
        this.game = game;
        gameWorld = new GameWorld();
        debugRenderer = new Box2DDebugRenderer();

        gameWorld.create();
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        OrthographicCamera cam= NotPhysicsEngineGUI.cameras.peek();
        SpriteBatch batch=game.batch;
        batch.setProjectionMatrix(cam.combined);
        ScreenUtils.clear(.1f, 0.1f, 0.4f, 1);
//        gameWorld.world.step(1 / 60f, 6, 2);
        debugRenderer.render(gameWorld.world, cam.combined);
        batch.begin();
        batch.end();
        gameWorld.update(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
