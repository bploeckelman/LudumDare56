package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.Config;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.assets.Patches;
import lando.systems.ld56.ui.Button;
import lando.systems.ld56.utils.typinglabel.TypingLabel;

import javax.sound.midi.Patch;

public class CreditsScreen extends BaseScreen {

    private final TypingLabel titleLabel;
    private final TypingLabel themeLabel;
    private final TypingLabel leftCreditLabel;
    private final TypingLabel rightCreditLabel;
    private final TypingLabel thanksLabel;
    private final TypingLabel disclaimerLabel;

//    private final Animation<TextureRegion> catAnimation;
//    private final Animation<TextureRegion> dogAnimation;
//    private final Animation<TextureRegion> kittenAnimation;
    private final TextureRegion background;

    private final String title = "{GRADIENT=purple;cyan}Game Name{ENDGRADIENT}";
    private final String theme = "{GRADIENT=purple;cyan}Made for Ludum Dare 56: Tiny Creatures{ENDGRADIENT}";

    private final String thanks = "{GRADIENT=purple;cyan}Thank you for playing our game!{ENDGRADIENT}";
    private final String developers = "{COLOR=gray}Developed by:{COLOR=white}\n {GRADIENT=white;gray}Brian Ploeckelman{ENDGRADIENT}\n {GRADIENT=white;gray}Doug Graham{ENDGRADIENT}\n {GRADIENT=white;gray}Brian Rossman{ENDGRADIENT}\n {GRADIENT=white;gray}Jeffrey Hwang{ENDGRADIENT}\n {GRADIENT=white;gray}Luke Bain{ENDGRADIENT}";
    private final String artists = "{COLOR=gray}Art by:{COLOR=white}\n {GRADIENT=white;gray}Matt Neumann{ENDGRADIENT}\n {GRADIENT=white;gray}Luke Bain{ENDGRADIENT}\n";
    private final String emotionalSupport = "{COLOR=cyan}Emotional Support:{COLOR=white}\n Asuka, Osha, Cherry, \n Obi, Yoda, Nova, and Roxie";
    private final String music = "{COLOR=gray}Music and Writing:{COLOR=white}\n {GRADIENT=white;gray}Pete Valeo{ENDGRADIENT}\n";
    private final String libgdx = "Made with {COLOR=red}<3{COLOR=white}\nand {RAINBOW}LibGDX{ENDRAINBOW}";
    private final String disclaimer = "{GRADIENT=black;gray}Disclaimer:{ENDGRADIENT}  {GRADIENT=gold;yellow}{JUMP=.27} No babies were made in the harming of this game{ENDJUMP}{ENDGRADIENT}";

    private float accum = 0f;
    private boolean showPets = false;

    private Button afterCreditsButton;

    public CreditsScreen() {
        super();

        assets.fontChrustySm.setColor(Color.WHITE);
        assets.fontChrustySm.getData().setScale(1f);

        titleLabel = new TypingLabel(assets.fontChrustyMd, title, 0f, Config.Screen.window_height - 20f);
        titleLabel.setWidth(Config.Screen.window_width);
        titleLabel.setFontScale(1f);

        themeLabel = new TypingLabel(assets.fontChrustyMd, theme, 0f, Config.Screen.window_height - 80f);
        themeLabel.setWidth(Config.Screen.window_width);
        themeLabel.setFontScale(1f);

        leftCreditLabel = new TypingLabel(assets.fontChrustyMd, developers.toLowerCase() + "\n\n" + emotionalSupport.toLowerCase() + "\n\n", 75f, Config.Screen.window_height / 2f + 135f);
        leftCreditLabel.setWidth(Config.Screen.window_width / 2f - 150f);
        leftCreditLabel.setLineAlign(Align.left);
        leftCreditLabel.setFontScale(.75f);

        background = assets.pixelRegion;

        rightCreditLabel = new TypingLabel(assets.fontChrustyMd, artists.toLowerCase() + "\n" + music.toLowerCase() + "\n" + libgdx.toLowerCase(), Config.Screen.window_width / 2 + 75f, Config.Screen.window_height / 2f + 135f);
        rightCreditLabel.setWidth(Config.Screen.window_width / 2f - 150f);
        rightCreditLabel.setLineAlign(Align.left);
        rightCreditLabel.setFontScale(.75f);

        thanksLabel = new TypingLabel(assets.fontChrustySm, thanks, 0f, 105f);
        thanksLabel.setWidth(Config.Screen.window_width);
        thanksLabel.setLineAlign(Align.center);
        thanksLabel.setFontScale(.85f);

        disclaimerLabel = new TypingLabel(assets.fontChrustySm, disclaimer, 0f, 75f);
        disclaimerLabel.setWidth(Config.Screen.window_width);
        thanksLabel.setLineAlign(Align.center);
        disclaimerLabel.setFontScale(.5f);

//        catAnimation = assets.dog;
//        dogAnimation = assets.asuka;
//        kittenAnimation = assets.osha;
//ToDo ninePatch        afterCreditsButton = new Button(new Rectangle(worldCamera.viewportWidth - 300f, 0f, 300, 50), "Scrapped Ideas", Assets.NinePatches.glass_yellow, Assets.NinePatches.glass, assets.fontChrustySm);

//ToDo PV       Main.game.audioManager.playMusic(AudioManager.Musics.outroMusic);
        var bounds = new Rectangle((windowCamera.viewportWidth /3), 0, (windowCamera.viewportWidth /3), 50);
        afterCreditsButton = new Button(bounds, "Done", Patches.get(Patches.Type.PLAIN), Patches.get(Patches.Type.PLAIN_GRADIENT), assets.font);
    }

//    @Override
//    public void alwaysUpdate(float delta) {

//    }

    @Override
    public void update(float dt) {
        if (exitingScreen) { return; }
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            windowCamera.unproject(mousePos);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isTouched()) {
            if (afterCreditsButton.getBounds().contains(mousePos.x, mousePos.y)) {
               game.setScreen(new TitleScreen());
               exitingScreen = true;
                return;
            }
            var allDone = titleLabel.hasEnded() && themeLabel.hasEnded() && leftCreditLabel.hasEnded() && rightCreditLabel.hasEnded() && thanksLabel.hasEnded() && disclaimerLabel.hasEnded();
            Gdx.app.log("CreditScreen", "allDone: " + allDone);
            if (allDone) {
                game.setScreen(new TitleScreen());
                exitingScreen = true;
                return;
            } else {
                titleLabel.skipToTheEnd();
                themeLabel.skipToTheEnd();
                leftCreditLabel.skipToTheEnd();
                rightCreditLabel.skipToTheEnd();
                thanksLabel.skipToTheEnd();
                disclaimerLabel.skipToTheEnd();
                showPets = true;
                return;
            }
        }
        accum += dt;
        titleLabel.update(dt);
        themeLabel.update(dt);
        leftCreditLabel.update(dt);
        rightCreditLabel.update(dt);
        thanksLabel.update(dt);
        disclaimerLabel.update(dt);
        afterCreditsButton.update(mousePos.x, mousePos.y);
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
        {
            batch.draw(background, 0, 0, Config.Screen.window_width, Config.Screen.window_height);

            batch.setColor(0f, 0f, 0f, .6f);
            batch.draw(assets.pixelRegion, 25f, 130f, Config.Screen.window_width / 2f - 50f, 400f);
            batch.draw(assets.pixelRegion, Config.Screen.window_width / 2f + 25f, 130f, Config.Screen.window_width / 2f - 50f, 400f);

            batch.setColor(Color.WHITE);
            titleLabel.render(batch);
            themeLabel.render(batch);
            leftCreditLabel.render(batch);
            rightCreditLabel.render(batch);
            thanksLabel.render(batch);
            disclaimerLabel.render(batch);
            if (accum > 7.5 || showPets) {
//                TextureRegion cherryTexture = assets.cherry.getKeyFrame(accum);
//                TextureRegion asukaTexture = assets.asuka.getKeyFrame(accum);
//                TextureRegion oshaTexture = assets.osha.getKeyFrame(accum);
//                batch.draw(oshaTexture, 450f, 175f);
//                batch.draw(asukaTexture, 500f, 175f);
//                batch.draw(cherryTexture, 550f, 175f);
            }
            if (accum > 8.5 || showPets) {
//                TextureRegion obiTexture = assets.obi.getKeyFrame(accum);
//                TextureRegion yodaTexture = assets.yoda.getKeyFrame(accum);
//                batch.draw(obiTexture, 475f, 125f);
//                batch.draw(yodaTexture, 525f, 125f);
            }
            batch.setColor(Color.WHITE);
            afterCreditsButton.draw(batch);
        }
        batch.end();
    }

}
