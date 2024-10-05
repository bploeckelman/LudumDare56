package lando.systems.ld56.screens;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import lando.systems.ld56.Config;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Assets;

public abstract class BaseScreen implements Disposable {

    public final Main game;
    public final Assets assets;
    public final SpriteBatch batch;
    public final TweenManager tween;
    public final OrthographicCamera windowCamera;

    public OrthographicCamera worldCamera;
    public boolean exitingScreen = false;
    public Vector3 vec3 = new Vector3();

    public BaseScreen() {
        this.game = Main.game;
        this.assets = game.assets;
        this.batch = game.assets.batch;
        this.tween = game.tween;
        this.windowCamera = game.windowCamera;

        this.worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, Config.Screen.framebuffer_width, Config.Screen.framebuffer_height);
        worldCamera.update();
    }

    @Override
    public void dispose() {}

    public void alwaysUpdate(float delta) {}

    public void update(float delta) {
        windowCamera.update();
        if (worldCamera != null) {
            worldCamera.update();
        }
    }

    public void renderFrameBuffers(SpriteBatch batch ) {}

    public abstract void render(SpriteBatch batch);

    public void initializeUI() {}
}
