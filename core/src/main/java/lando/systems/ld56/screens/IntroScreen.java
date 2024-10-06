package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.Config;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Transition;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.scene.Scene;
import lando.systems.ld56.utils.typinglabel.TypingLabel;

public class IntroScreen extends BaseScreen {

    Texture backgroundTexture;
    Texture parchmentTexture;
    BitmapFont font;
    ParticleManager particles;
    String page1 =
        "{COLOR=white}" +
            "Food additives, micro plastics, and pesticides have mutated the once innocuous creatures of the land\n\n"+
            "They now seek vengeance against their polluting oppressors \n\n";

    String page2 =
        "{COLOR=white}" +
            "Exact revenge! Raze their buildings, exter{GRADIENT=black;gray}mini{ENDGRADIENT}ate them!";
    String page3 =
        "{COLOR=white}" +
            "Food: roast chicken and fruit\n\n"+
            "Photographer - stuns, bombs, electrical appliances";

    int currentPage = 0;
    float elapsedTime = 0f;
    TypingLabel typingLabel;
    float transitionAlpha = 0f;

    public IntroScreen() {
//        backgroundTexture = assets.introBackground;
//        parchmentTexture = assets.parchment;
        font = Main.game.assets.fontChrustyMd;

        Main.game.audioManager.playMusic(AudioManager.Musics.introMusic);

        particles = new ParticleManager(Main.game.assets);

        typingLabel = new TypingLabel(font, page1, worldCamera.viewportWidth * .2f, worldCamera.viewportHeight * .8f);
        typingLabel.setWidth(Config.Screen.window_width * .7f);
        typingLabel.setFontScale(1.2f);
    }

    @Override
    public void alwaysUpdate(float delta) {

    }

    @Override
    public void update(float dt) {
        elapsedTime += dt;
        if (Gdx.input.justTouched() && elapsedTime > .5f) {
            elapsedTime = 0;
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldCamera.unproject(touchPos);
//            particles.levelUpEffect(touchPos.x, touchPos.y);

            if (transitionAlpha < 1f) {
                transitionAlpha = 1f;
            } else if (!typingLabel.hasEnded()) {
                typingLabel.skipToTheEnd();
            } else {
                currentPage++;
                if (currentPage == 1) {
                    typingLabel.restart(page2);
                } else if (currentPage == 2) {
                    typingLabel.restart(page3);
                }
//                else if (currentPage == 3) {
//                    typingLabel.restart(page4);
//                }
                else {
                    launchGame();
                }
            }
        }
        particles.update(dt);
        if (transitionAlpha < 1f) {
            transitionAlpha = MathUtils.clamp(transitionAlpha + dt, 0f, 1f);
        } else {
            typingLabel.update(dt);
        }
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {

    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(.0f, .0f, .1f, 1f);

        batch.enableBlending();
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, transitionAlpha);
        //batch.draw(backgroundTexture, 0, 0, windowCamera.viewportWidth, windowCamera.viewportHeight);

        // Center parchment calculation (adjust offsets if needed)
        //batch.draw(parchmentTexture, windowCamera.viewportWidth * .1f, windowCamera.viewportHeight * .1f, windowCamera.viewportWidth * .9f, windowCamera.viewportHeight * .9f * transitionAlpha);

        typingLabel.render(batch);

        particles.draw(batch, ParticleManager.Layer.FOREGROUND);
        batch.end();
    }

    void launchGame() {
        if (!exitingScreen){
            exitingScreen = true;
            game.setScreen(new GameScreen(Scene.Type.MICROBIOME), Transition.Type.DOOMDRIP, 2f);

        }
    }
}
