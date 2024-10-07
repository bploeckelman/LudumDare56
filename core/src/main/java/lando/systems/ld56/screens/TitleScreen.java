package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.ui.TitleScreenUI;

public class TitleScreen extends BaseScreen {
    TitleScreenUI titleScreenUI;
    public TitleScreen() {
        super();
        titleScreenUI = new TitleScreenUI(100, 100, 200, 50, assets.font, TitleScreenUI.ButtonOrientation.HORIZONTAL);
    }

    @Override
    public void update(float delta) {
//        if (!exitingScreen && Gdx.input.justTouched()){
//            exitingScreen = game.setScreen(new IntroScreen());
//        }
        vec3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldCamera.unproject(vec3);
        titleScreenUI.update(vec3.x, vec3.y);
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.DARK_GRAY);

        var camera = windowCamera;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            batch.draw(assets.gdx, 0, 0, camera.viewportWidth, camera.viewportHeight);

            assets.font.getData().setScale(1f);
            assets.layout.setText(assets.font, "Title", Color.WHITE, camera.viewportWidth, Align.center, false);
            assets.font.draw(batch, assets.layout, 0, camera.viewportHeight / 2f + assets.layout.height);
            assets.font.getData().setScale(1f);
            titleScreenUI.draw(batch);
        }
        batch.end();
    }
}
