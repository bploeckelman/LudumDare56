package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.Config;
import lando.systems.ld56.assets.Patches;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.Structure;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.particles.effects.BiteEffect;
import lando.systems.ld56.particles.effects.ParticleEffectType;
import lando.systems.ld56.particles.effects.RatSwarmEffect;
import lando.systems.ld56.scene.Scene;
import lando.systems.ld56.utils.Calc;
import text.formic.Stringf;

public class GameScreen extends BaseScreen {

    public Scene scene;
    public GridPoint2 mouseGridPos;
    public ParticleManager particles;

    public GameScreen(Scene.Type type, Player.CreatureType creatureType) {
        particles = new ParticleManager(assets);
        this.scene = new Scene(this, type, creatureType);
//        this.scene = new Scene(this, Scene.Type.NEIGHBORHOOD);
//        this.scene = new Scene(this, Scene.Type.CITY);
        this.mouseGridPos = new GridPoint2();

        var inputMux = new InputMultiplexer(input);
        Gdx.input.setInputProcessor(inputMux);

        audioManager.playMusic(AudioManager.Musics.mainMusic);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // NOTE: put update code after the check for 'stepFrame' so that its paused when stepping

        // update the grid coords that the mouse is currently positioned at
        vec3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldCamera.unproject(vec3);
        var worldGridX = (int) Calc.floor(vec3.x) / scene.levelMap.solidCollider.grid.tileSize;
        var worldGridY = (int) Calc.floor(vec3.y) / scene.levelMap.solidCollider.grid.tileSize;
        mouseGridPos.set(worldGridX, worldGridY);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        // debug toggles
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) { Config.Debug.frame_by_frame = !Config.Debug.frame_by_frame; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) { Config.Debug.general = !Config.Debug.general; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) { Config.Debug.render = !Config.Debug.render; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) { Config.Debug.ui = !Config.Debug.ui; }
        // collapse structure
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            for (Structure structure : scene.structures ) {
                structure.collapse();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            scene.emitPlayerSegment_TEST();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            game.setScreen(new IntroScreen());
        }

        // early out if we're in 'frame by frame' mode, so we can step a frame at a time via keypress
        // NOTE: the position of this block in this method is important!!!
        //  ***don't move this*** (and most update code should go below it)
        var stepFrame = Gdx.input.isKeyJustPressed(Input.Keys.NUM_9);
        if (Config.Debug.frame_by_frame && !stepFrame) {
            return;
        }

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            var particleEffect = particles.effects.get(ParticleEffectType.BITE);
            particleEffect.spawn(new BiteEffect.Params(vec3.x, vec3.y));
        }

        var goToEndScreen = false; // TODO: set true based on 'completing' the game, whatever that will mean
        if (!exitingScreen && goToEndScreen) {
            exitingScreen = game.setScreen(new EndingScreen());
        }
        if (!exitingScreen && scene.gameOver() && Gdx.input.justTouched()) {
            switch (scene.type) {
                case MICROBIOME:
                    exitingScreen = game.setScreen(new CharacterSelectScreen(Scene.Type.NEIGHBORHOOD));
                    break;
                case NEIGHBORHOOD:
                    exitingScreen = game.setScreen(new CharacterSelectScreen(Scene.Type.CITY));
                    break;
                case CITY:
                    exitingScreen = game.setScreen(new EndingScreen());
                    break;
                case MUSHROOM_KINGDOM:
                    // no idea
                    break;
            }
        }

        particles.update(delta);
        scene.update(delta, scene.gameOver());

        if (Config.Debug.general) {
            Gdx.app.log("debug", scene.player.debugString());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        var camera = worldCamera;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            scene.render(batch);
            particles.draw(batch, ParticleManager.Layer.FOREGROUND);

            if (Config.Debug.render) {
                scene.renderDebug(batch, assets.shapes);
            }

            assets.font.getData().setScale(1f);
            assets.layout.setText(assets.font, "Game", Color.WHITE, camera.viewportWidth, Align.center, false);
            assets.font.draw(batch, assets.layout, 0, camera.viewportHeight - assets.layout.height);
            assets.font.getData().setScale(1f);
        }
        batch.end();

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {

            if (scene.gameOver()) {
                assets.layout.setText(assets.font, "Congratz!\nWe need text here\n Click to continue or something", Color.WHITE, windowCamera.viewportWidth/2f, Align.center, true);
                assets.font.draw(batch, assets.layout, windowCamera.viewportWidth/4f, windowCamera.viewportHeight/2f);
            }
            if (Config.Debug.shouldShowDebugUi()) {
                var lines = 0;
                if (Config.Debug.general) lines++;
                if (Config.Debug.render) lines++;
                if (Config.Debug.ui) lines++;
                if (Config.Debug.logging) lines++;
                if (Config.Debug.frame_by_frame) lines++;

                var layout = assets.layout;
                var font = assets.fontChrustySm;

                int margin = 5;
                int width = (int) windowCamera.viewportWidth / 3;
                int targetWidth = width - (2 * margin);
                layout.setText(font, "Debug:", Color.WHITE, targetWidth, Align.left, false);
                int lineHeight = (int) layout.height;
                int height = (lineHeight * lines) + (2 * margin);

                int dialogX = margin;
                int dialogY = (int) worldCamera.viewportHeight - margin - height;
                int textX = dialogX + margin;
                int textY = dialogY + lineHeight + margin;

                var patch = Patches.get(Patches.Type.PLAIN_DIM);
                patch.draw(batch, dialogX, dialogY, width, height);

                String str;

                if (Config.Debug.general) {
                    str = Stringf.format("General - %s", Config.Debug.general);
                    layout.setText(font, str, Color.WHITE, targetWidth, Align.left, false);
                    font.draw(batch, layout, textX, textY);
                    textY += lineHeight;
                }

                if (Config.Debug.render) {
                    str = Stringf.format("Render: %s", Config.Debug.render);
                    layout.setText(font, str, Color.WHITE, targetWidth, Align.left, false);
                    font.draw(batch, layout, textX, textY);
                    textY += lineHeight;
                }

                if (Config.Debug.ui) {
                    str = Stringf.format("UI: %s", Config.Debug.ui);
                    layout.setText(font, str, Color.WHITE, targetWidth, Align.left, false);
                    font.draw(batch, layout, textX, textY);
                    textY += lineHeight;
                }

                if (Config.Debug.logging) {
                    str = Stringf.format("Logging: %s", Config.Debug.logging);
                    layout.setText(font, str, Color.WHITE, targetWidth, Align.left, false);
                    font.draw(batch, layout, textX, textY);
                    textY += lineHeight;
                }

                if (Config.Debug.frame_by_frame) {
                    str = Stringf.format("FrameStep: %s", Config.Debug.frame_by_frame);
                    layout.setText(font, str, Color.WHITE, targetWidth, Align.left, false);
                    font.draw(batch, layout, textX, textY);
                    textY += lineHeight;
                }
            }
        }
        batch.end();
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch ) {
        scene.renderFrameBuffers(batch);
    }


    private boolean leftMouseDown = false;
    private boolean rightMouseDown = false;
    private boolean middleMouseDown = false;

    private final InputAdapter input = new InputAdapter() {

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            switch (button) {
                case Input.Buttons.LEFT: leftMouseDown = true; break;
                case Input.Buttons.RIGHT: rightMouseDown = true; break;
                case Input.Buttons.MIDDLE: middleMouseDown = true; break;
            }

            if (leftMouseDown) {
                scene.paintGridAt(mouseGridPos.x, mouseGridPos.y);
//                audioManager.playSound(AudioManager.Sounds.coin, .02f);
                return true;
            }
            if (rightMouseDown) {
                scene.eraseGridAt(mouseGridPos.x, mouseGridPos.y);
                return true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            switch (button) {
                case Input.Buttons.LEFT: leftMouseDown = false; break;
                case Input.Buttons.RIGHT: rightMouseDown = false; break;
                case Input.Buttons.MIDDLE: middleMouseDown = false; break;
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (middleMouseDown) {
//                worldCamera.translate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
//                worldCamera.update();
                return true;
            } else {
                if (leftMouseDown) {
                    scene.paintGridAt(mouseGridPos.x, mouseGridPos.y);
                    return true;
                }
                if (rightMouseDown) {
                    scene.eraseGridAt(mouseGridPos.x, mouseGridPos.y);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            if (amountY != 0) {
                var zoomSpeed = 0.5f;
                var sign = Calc.sign(amountY);
//                worldCamera.zoom = Calc.eerp(worldCamera.zoom, worldCamera.zoom + sign * 0.1f, zoomSpeed);
//                worldCamera.zoom = Calc.clampf(worldCamera.zoom, 0.1f, 8f);
                return true;
            }
            return false;
        }
    };
}
