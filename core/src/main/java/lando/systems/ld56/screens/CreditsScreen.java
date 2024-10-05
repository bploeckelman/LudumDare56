package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

public class CreditsScreen extends BaseScreen {

    @Override
    public void update(float delta) {
        if (!exitingScreen && Gdx.input.justTouched()){
            exitingScreen = game.setScreen(new LaunchScreen());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.PURPLE);

        var camera = windowCamera;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            batch.draw(assets.gdx, 0, 0, camera.viewportWidth, camera.viewportHeight);

            assets.font.getData().setScale(1f);
            assets.layout.setText(assets.font, "Credits", Color.WHITE, camera.viewportWidth, Align.center, false);
            assets.font.draw(batch, assets.layout, 0, camera.viewportHeight / 2f + assets.layout.height);
            assets.font.getData().setScale(1f);
        }
        batch.end();
    }
}
