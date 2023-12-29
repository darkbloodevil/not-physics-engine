package game

import NotBox2D.GameWorld
import com.badlogic.gdx.Game
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.ScreenUtils

import java.util
import java.util.Stack


object NotPhysicsEngineGUI {
    /**
     * @TODO 临时的记得改
     */
    var cameras = new util.Stack[OrthographicCamera]
    /**
     * 是把屏幕划分成多少份
     */
    private[game] val FRUSTUM_WIDTH = 48
    private[game] val FRUSTUM_HEIGHT = 27
}

class NotPhysicsEngineGUI extends Game {
    private[game] var batch: SpriteBatch = _

    override def create(): Unit = {
        batch = new SpriteBatch
        val cam = new OrthographicCamera(NotPhysicsEngineGUI.FRUSTUM_WIDTH, NotPhysicsEngineGUI.FRUSTUM_HEIGHT)
        NotPhysicsEngineGUI.cameras.add(cam)
        this.setScreen(new MainScreen(this))
    }

    override def render(): Unit = {
        super.render()
    }

    override def dispose(): Unit = {
        batch.dispose()
    }
}

class MainScreen extends ScreenAdapter {

    var gameWorld: GameWorld;

    var debugRenderer:Box2DDebugRenderer;
    var game:NotPhysicsEngineGUI;

    def this(game:NotPhysicsEngineGUI) {
        this()
        this.game = game;
        gameWorld = new GameWorld();
        debugRenderer = new Box2DDebugRenderer();

        gameWorld.create();
    }
    override def show(): Unit = {
    }

    override def render(delta: Float): Unit = {
        val cam = game.cameras.peek
        val batch = game.batch
        batch.setProjectionMatrix(cam.combined)
        ScreenUtils.clear(.1f, 0.1f, 0.4f, 1)
        //        gameWorld.world.step(1 / 60f, 6, 2);
        debugRenderer.render(gameWorld.world, cam.combined)
        batch.begin()
        batch.end()
        gameWorld.update(delta)
    }

    override def resize(width: Int, height: Int): Unit = {
    }

    override def pause(): Unit = {
    }

    override def resume(): Unit = {
    }

    override def hide(): Unit = {
    }

    override def dispose(): Unit = {
    }
}
