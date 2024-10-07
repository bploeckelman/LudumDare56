package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.audio.AudioManager;

public class EndingScreen extends BaseScreen {
    private float startX;
    private float startY;

    public EndingScreen() {
        audioManager.playSound(AudioManager.Sounds.outroNarration);
        this.startX = 0;
        this.startY = windowCamera.viewportHeight / 1.25f + assets.layout.height - 4;
    }
    @Override
    public void update(float delta) {
        if (!exitingScreen && Gdx.input.justTouched()){
            exitingScreen = game.setScreen(new CreditsScreen());
        }
    }


    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.BROWN);

        var camera = windowCamera;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
//            batch.draw(assets.gdx, 0, 0, camera.viewportWidth, camera.viewportHeight);

            String endingString = "Well there you have it! \n\n" +
                "An entire world of tiny creatures " +
                "existing right beneath \n- and in some cases inside - our very noses.\n\n" +
                "Who knows what terrifying new adventures await our miniature heroes as our " +
                "climate continues to change, and the worst people in the world make their grab " +
                "at global power. \n\n" +
                "Truly a cursed timeline. \n\n" +
                "May the universe smile upon us \nand a brighter tomorrow emerge from the chaos" +
                ".\n\n" +
                "#blessed #tinycreatures #likefollowsubscribe";

            assets.font.getData().setScale(.7f);

            assets.layout.setText(assets.font, endingString, Color.BLACK, camera.viewportWidth,
                Align.center, true);
            assets.font.draw(batch, assets.layout, startX+4,
                startY - 4);
            assets.layout.setText(assets.font, endingString, Color.WHITE, camera.viewportWidth, Align.center
                , true);
            assets.font.draw(batch, assets.layout, startX,
                startY);

            assets.font.getData().setScale(1f);
        }
        batch.end();
    }
}
