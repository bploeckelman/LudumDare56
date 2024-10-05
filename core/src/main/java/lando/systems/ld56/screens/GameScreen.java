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
import lando.systems.ld56.Main;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.scene.Scene;
import lando.systems.ld56.utils.Calc;

public class GameScreen extends BaseScreen {

    Scene scene;
    GridPoint2 mouseGridPos;

    public GameScreen() {
        int tileSize = 16;
        int initialWidth  = (int) Calc.ceiling(worldCamera.viewportWidth  / tileSize);
        int initialHeight = (int) Calc.ceiling(worldCamera.viewportHeight / tileSize);
        this.scene = new Scene(assets, tileSize, initialWidth, initialHeight);
        this.mouseGridPos = new GridPoint2();

        var inputMux = new InputMultiplexer(input);
        Gdx.input.setInputProcessor(inputMux);

//        audioManager.playMusic(AudioManager.Musics.introMusic);

    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        // debug toggles
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) { Config.Debug.general = !Config.Debug.general; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) { Config.Debug.render = !Config.Debug.render; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) { Config.Debug.ui = !Config.Debug.ui; }

        var goToEndScreen = false; // TODO: set true based on 'completing' the game, whatever that will mean
        if (!exitingScreen && goToEndScreen) {
            exitingScreen = game.setScreen(new EndingScreen());
        }

        vec3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldCamera.unproject(vec3);
        var worldGridX = (int) Calc.floor(vec3.x) / scene.grid.tileSize();
        var worldGridY = (int) Calc.floor(vec3.y) / scene.grid.tileSize();
        mouseGridPos.set(worldGridX, worldGridY);

        scene.update(delta);

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

            if (Config.Debug.render) {
                scene.renderDebug(batch, assets.shapes);
            }

            assets.font.getData().setScale(1f);
            assets.layout.setText(assets.font, "Game", Color.WHITE, camera.viewportWidth, Align.center, false);
            assets.font.draw(batch, assets.layout, 0, camera.viewportHeight - assets.layout.height);
            assets.font.getData().setScale(1f);
        }
        batch.end();
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
                worldCamera.translate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
                worldCamera.update();
                return true;
            } else {
                scene.highlightGridAt(mouseGridPos.x, mouseGridPos.y);

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
            scene.highlightGridAt(mouseGridPos.x, mouseGridPos.y);
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            if (amountY != 0) {
                var zoomSpeed = 0.5f;
                var sign = Calc.sign(amountY);
                worldCamera.zoom = Calc.eerp(worldCamera.zoom, worldCamera.zoom + sign * 0.1f, zoomSpeed);
                worldCamera.zoom = Calc.clampf(worldCamera.zoom, 0.1f, 8f);
                return true;
            }
            return false;
        }
    };
}
