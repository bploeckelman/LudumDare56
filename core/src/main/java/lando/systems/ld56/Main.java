package lando.systems.ld56;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.assets.Transition;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.entities.EntityData;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.scene.Scene;
import lando.systems.ld56.screens.*;
import lando.systems.ld56.utils.Time;
import lando.systems.ld56.utils.accessors.CameraAccessor;
import lando.systems.ld56.utils.accessors.ColorAccessor;
import lando.systems.ld56.utils.accessors.RectangleAccessor;
import lando.systems.ld56.utils.accessors.Vector2Accessor;
import lando.systems.ld56.utils.accessors.Vector3Accessor;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    public static Main game;

    public Assets assets;
    public AudioManager audioManager;
    public TweenManager tween;
    public FrameBuffer frameBuffer;
    public TextureRegion frameBufferRegion;
    public OrthographicCamera windowCamera;
    public EntityData entityData;

    public BaseScreen currentScreen;

    public Main() {
        Main.game = this;
    }

    @Override
    public void create() {
        Time.init();

        assets = new Assets();
        entityData = new EntityData();

        tween = new TweenManager();
        Tween.setWaypointsLimit(4);
        Tween.setCombinedAttributesLimit(4);
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        Tween.registerAccessor(Vector3.class, new Vector3Accessor());
        Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());

        audioManager = new AudioManager(assets, tween);

        var format = Pixmap.Format.RGBA8888;
        int width = Config.Screen.framebuffer_width;
        int height = Config.Screen.framebuffer_height;
        boolean hasDepth = true;

        frameBuffer = new FrameBuffer(format, width, height, hasDepth);
        var frameBufferTexture = frameBuffer.getColorBufferTexture();
        frameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        frameBufferRegion = new TextureRegion(frameBufferTexture);
        frameBufferRegion.flip(false, true);

        windowCamera = new OrthographicCamera();
        windowCamera.setToOrtho(false, Config.Screen.window_width, Config.Screen.window_height);
        windowCamera.update();

        var showLaunchScreen = (Gdx.app.getType() == Application.ApplicationType.WebGL || Config.Debug.show_launch_screen);
        var startingScreen = showLaunchScreen ? new LaunchScreen() : new TitleScreen();
        if (Config.Debug.start_on_game_screen && !showLaunchScreen) {
            startingScreen = new CharacterSelectScreen(Scene.Type.MICROBIOME);
        }
        setScreen(startingScreen);
    }

    public void update(float delta) {
        // handle top level input
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            Config.Debug.general = !Config.Debug.general;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            nextScreen();
            return;
        }

        // update things that must update every tick
        Time.update();
        tween.update(Time.delta);
        currentScreen.alwaysUpdate(Time.delta);
        Transition.update(Time.delta);

        // handle a pause
        if (Time.pause_timer > 0) {
            Time.pause_timer -= Time.delta;
            if (Time.pause_timer <= -0.0001f) {
                Time.delta = -Time.pause_timer;
            } else {
                // skip updates if we're paused
                return;
            }
        }
        Time.millis += Time.delta;
        Time.previous_elapsed = Time.elapsed_millis();

        currentScreen.update(delta);
    }

    @Override
    public void render() {
        update(Time.delta);

        ScreenUtils.clear(Color.DARK_GRAY);
        if (Transition.inProgress()) {
            Transition.render(assets.batch);
        } else {
            currentScreen.renderFrameBuffers(assets.batch);
            currentScreen.render(assets.batch);
        }
    }

    public boolean setScreen(BaseScreen newScreen) {
        return setScreen(newScreen, null, 0.5f);
    }

    public boolean setScreen(BaseScreen newScreen, Transition.Type transitionType, float transitionSpeed) {
        // nothing to transition from, just set the current screen
        if (currentScreen == null) {
            currentScreen = newScreen;
            return true;
        }

        // only one transition allowed at a time
        if (Transition.inProgress()) return false;

        Transition.to(newScreen, transitionType, transitionSpeed);
        return true;
    }

    public static void playSound(AudioManager.Sounds soundOption) {
        Main.game.audioManager.playSound(soundOption);
    }

    public static void removeCollider(Collider collider) {
        Main.game.entityData.remove(collider, Collider.class);
    }

    // debug code
    private int sceneIndex = -1;
    private void nextScreen() {
        // last screen doesn't work at all
        if (++sceneIndex == Scene.Type.values().length) {
            sceneIndex = 0;
        }
        setScreen(new CharacterSelectScreen(Scene.Type.values()[sceneIndex]));
        //setScreen(new CharacterSelectScreen(Scene.Type.NEIGHBORHOOD));
    }
}
