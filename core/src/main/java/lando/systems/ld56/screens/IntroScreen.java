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
import lando.systems.ld56.particles.Particles;
import lando.systems.ld56.utils.typinglabel.TypingLabel;

public class IntroScreen extends BaseScreen {

    Texture backgroundTexture;
    Texture parchmentTexture;
    BitmapFont font;
    Particles particles;
    String page1 =
        "{COLOR=black}" +
            "Many consider a{GRADIENT=black;gray} King{ENDGRADIENT} the embodiment of{GRADIENT=black;gold} power{ENDGRADIENT}.\n\n" +
            "But in truth, each monarch is a captive. " +
            "Their every need relies on the{GRADIENT=navy;purple} fealty {ENDGRADIENT}of those they purport to rule.\n\n " +
            "And in times of peril, the King is only as safe as the court they are {GRADIENT=yellow;black}summoning{ENDGRADIENT} to their defense...";
    String page2 =
        "{COLOR=black}When a mystical{GRADIENT=red;black} wizard{ENDGRADIENT} unleashes his army of {GRADIENT=red;brown}monsters {ENDGRADIENT}on the castle, " +
            "the King's warriors must confront the menace before they reach the kingdom.\n\n" +
            "(Kind of a dick move by the wizard. Really failed the vibe check.)";
    String page3 =
        "{COLOR=black}For whatever reason, the King's guards prefer a strategy of{GRADIENT=brown;red} turn-based combat {ENDGRADIENT}" +
            "that I guess is reminiscent of {GRADIENT=green;black}chess{ENDGRADIENT}? I don't really know - I'm more of a Go player myself." +
            "\n\nIf our warriors reach the wizard's stronghold, it means victory for the kingdom. But if any of the enemies reach the king, he'll be {GRADIENT=red;brown}Board To Death! {ENDGRADIENT} " ;
    //    String page4 =
//        "{COLOR=black}This is no mere chessboard, but a tapestry of fate woven by your strategic brilliance.\n" +
//        "So, grand observer, will your reign usher in an era of prosperity...\n" +
//        "...or will the kingdom crumble beneath the weight of your decisions?\n" +
//        "{RAINBOW}The time has come. The pieces await your command...{ENDRAINBOW}\n";
    int currentPage = 0;
    float elapsedTime = 0f;
    TypingLabel typingLabel;
    float transitionAlpha = 0f;

    public IntroScreen() {
//        backgroundTexture = assets.introBackground;
//        parchmentTexture = assets.parchment;
        font = Main.game.assets.fontChrustySm;

        Main.game.audioManager.playMusic(AudioManager.Musics.introMusic);

        particles = new Particles(Main.game.assets);

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
        batch.draw(backgroundTexture, 0, 0, windowCamera.viewportWidth, windowCamera.viewportHeight);

        // Center parchment calculation (adjust offsets if needed)
        batch.draw(parchmentTexture, windowCamera.viewportWidth * .1f, windowCamera.viewportHeight * .1f, windowCamera.viewportWidth * .9f, windowCamera.viewportHeight * .9f * transitionAlpha);

        typingLabel.render(batch);

        particles.draw(batch, Particles.Layer.FOREGROUND);
        batch.end();
    }

    void launchGame() {
        if (!exitingScreen){
            exitingScreen = true;
            game.setScreen(new GameScreen(), Transition.Type.DOOMDRIP, 2f);

        }
    }
}
