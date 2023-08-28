package com.mygdx.game;

import NotBox2D.BodyFactory;
import NotBox2D.GameWorld;
import NotBox2D.JsonReader;
import NotBox2D.StandardFactory;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import org.json.Cookie;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class NotPhysicsEngineMain extends Game {
    SpriteBatch batch;
    Texture img;
    GameWorld gameWorld;

    Box2DDebugRenderer debugRenderer;
    private OrthographicCamera cam;
    /**
     * 是把屏幕划分成多少份
     */
    static final float FRUSTUM_WIDTH = 48;
    static final float FRUSTUM_HEIGHT = 27;

    public SpriteBatch batcher;

    @Override
    public void create() {


        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        gameWorld = new GameWorld();
        cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        debugRenderer = new Box2DDebugRenderer();

        gameWorld.create();

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
