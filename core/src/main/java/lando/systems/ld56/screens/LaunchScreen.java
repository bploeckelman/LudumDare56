package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.assets.Transition;

public class LaunchScreen extends BaseScreen {

    @Override
    public void update(float dt) {
        if (!exitingScreen && Gdx.input.justTouched()){
            exitingScreen = game.setScreen(new TitleScreen(), Transition.Type.BLINDS, true);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.LIGHT_GRAY);

        var camera = windowCamera;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            assets.font.getData().setScale(1f);
            assets.layout.setText(assets.font, "Click to Begin", Color.WHITE, camera.viewportWidth, Align.center, false);
            assets.font.draw(batch, assets.layout, 0, camera.viewportHeight / 2f + assets.layout.height);
            assets.font.getData().setScale(1f);
        }
        batch.end();
    }
}
