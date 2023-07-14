package com.mygdx.game;

import NotBox2D.BodyFactory;
import NotBox2D.GameWorld;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import org.json.Cookie;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

public class NotPhysicsEngineMain extends Game {
    SpriteBatch batch;
    Texture img;
    GameWorld gameWorld;
    Box2DDebugRenderer debugRenderer;
    private OrthographicCamera cam;
    /**
     * 描述游戏中宽度（米）
     */
    static final float FRUSTUM_WIDTH = 27;
    static final float FRUSTUM_HEIGHT = 18;

    public SpriteBatch batcher;

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        gameWorld=new GameWorld();
        cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        debugRenderer = new Box2DDebugRenderer();


        BodyFactory bf=new BodyFactory(gameWorld);
        String cookie = "somehow=false; " +
                "body_type = dynamic; " +
                "shape = circle; " +
                "radius =1f";
        JSONObject jo=Cookie.toJSONObject(cookie);
        jo.put("position", new JSONArray().put(0).put(0));
        bf.get_body(jo);

        // Create our body definition
        BodyDef groundBodyDef = new BodyDef();
// Set its world position
        groundBodyDef.position.set(new Vector2(0, -10));

// Create a body from the definition and add it to the world
        Body groundBody = gameWorld.world.createBody(groundBodyDef);

// Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
// Set the polygon shape as a box which is twice the size of our view port and 20 high
// (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(cam.viewportWidth, 2.0f);
// Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0.0f);
// Clean up after ourselves
        groundBox.dispose();

    }

    @Override
    public void render() {
        batch.setProjectionMatrix(cam.combined);
        ScreenUtils.clear(.1f, 0.1f, 0.4f, 1);
        gameWorld.world.step(1 / 60f, 6, 2);
        debugRenderer.render(gameWorld.world, cam.combined);
        batch.begin();
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}
