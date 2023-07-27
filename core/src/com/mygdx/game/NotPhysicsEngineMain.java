package com.mygdx.game;

import NotBox2D.BodyFactory;
import NotBox2D.GameWorld;
import NotBox2D.JsonReader;
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
     * 描述游戏中宽度（米）
     */
    static final float FRUSTUM_WIDTH = 27;
    static final float FRUSTUM_HEIGHT = 18;

    public SpriteBatch batcher;

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        gameWorld = new GameWorld();
        cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        debugRenderer = new Box2DDebugRenderer();


        BodyFactory bf = new BodyFactory(gameWorld);

        String game_content;
        try {
            game_content = new String(Files.readAllBytes(Paths.get("game.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject game = JsonReader.read_str_json(game_content);

        JSONObject jo = game.getJSONObject("character");
        bf.get_body(jo);


        bf.get_body(game.getJSONObject("ground"));

//        bf.get_body(game.getJSONObject("chain_test")).setAwake(true);

        //Body bodyA=bf.get_body(game.getJSONObject("edge_test"));
        //bodyA.setAwake(true);

        bf.get_body(game.getJSONObject("wall1"));
        bf.get_body(game.getJSONObject("wall2"));

        bf.get_joint(game.getJSONObject("joint"));
//        DistanceJointDef defJoint = new DistanceJointDef ();
//        defJoint.length = 0;
//        defJoint.initialize(bodyA, bodyB, new Vector2(0,0), new Vector2(128, 0));

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
