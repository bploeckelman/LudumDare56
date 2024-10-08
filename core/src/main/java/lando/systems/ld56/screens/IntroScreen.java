package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
    Rectangle skipButton = new Rectangle(Gdx.graphics.getWidth() - 200, 20, 180, 80);
    String page1 =
        "{COLOR=white}" +
//            "Life comes in all shapes and sizes.\n\n" +
            "From the smallest of creatures, to those slightly larger (though " +
            "still quite small by any reasonable measure), " +
            "life is a constant struggle to survive.\n\n" +
            "For many of these diminutive animals, the best defense is a good offense. "+
            "And destruction is the main tool in their toolbox. \n\n" +
            "Let us see what kind of tricks these {GRADIENT=black;gray}tiny " +
            "creatures{ENDGRADIENT} have " +
            "up" +
            " their sleeves as they set out on their {GRADIENT=red;yellow}tiny " +
            "rampage{ENDGRADIENT}.";


//    String page2 =
//        "{COLOR=white}" +
//            "For many of these diminutive animals, the best defense "+
//            "is a good offense." +
//            "And the destruction is the main tool in their toolbox. \n\n" +
//            "Let us see what kind of tricks these GRADIENT=black;gray}tiny creatures{ENDGRADIENT}" +
//            " have up their sleeves as they set out on their tiny rampage!";
//
//    String page3 =
//        "{COLOR=white}" +
//            "Exact revenge! Raze their buildings, exter{GRADIENT=black;gray}mini{ENDGRADIENT}ate them!";

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

        typingLabel = new TypingLabel(font, page1, worldCamera.viewportWidth * .1f,
            worldCamera.viewportHeight * .8f);
        typingLabel.setWidth(Config.Screen.window_width * .8f);
        typingLabel.setFontScale(.9f);
        audioManager.playSound(AudioManager.Sounds.introNarration);
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
                launchGame();
//                currentPage++;
//                if (currentPage == 1) {
//                    typingLabel.restart(page2);
//                } else if (currentPage == 2) {
//                    typingLabel.restart(page3);
                }
//                else if (currentPage == 3) {
//                    typingLabel.restart(page4);
//                }
//                else {
//                    launchGame();
//                }
//            }
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

//        Patches.get(Patches.Type.PLAIN).draw(batch, skipButton.x, skipButton.y, skipButton.width, skipButton.height);
        batch.end();
    }

    void launchGame() {
        if (!exitingScreen){
            audioManager.stopSound(AudioManager.Sounds.introNarration);
            exitingScreen = true;
            game.setScreen(new CharacterSelectScreen(Scene.Type.MICROBIOME), Transition.Type.DOOMDRIP);
//            game.setScreen(new EndingScreen(), Transition.Type.DOOMDRIP, 2f);
        }
    }
}
