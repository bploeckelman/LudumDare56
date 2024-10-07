package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.assets.InputPrompts;
import lando.systems.ld56.assets.Patches;
import lando.systems.ld56.assets.Transition;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.scene.Scene;

public class CharacterSelectScreen extends BaseScreen {
    public static boolean showTutorial = true;

    BitmapFont font;
    ParticleManager particles;
    Scene.Type nextSceneType;
    private final float SPRITE_SIZE = 250f;
    Rectangle characterAButtonBound = new Rectangle(windowCamera.viewportWidth / 3f - SPRITE_SIZE / 2f, 100f, SPRITE_SIZE, 100);
    Rectangle characterBButtonBound = new Rectangle(windowCamera.viewportWidth * 2f / 3f - SPRITE_SIZE / 2f, 100, SPRITE_SIZE, 100);
    boolean characterAButtonHovered = false;
    boolean characterBButtonHovered = false;
    Color characterAColor = Color.RED;
    Color characterBColor = Color.BLUE;
    float accum = 0f;
    Animation<TextureRegion> background;
    Animation<TextureRegion> creatureAAnim;
    Animation<TextureRegion> creatureBAnim;
    String characterDescriptionA;
    String characterDescriptionB;

    public Rectangle modalRect = new Rectangle(50, 50, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 100);

    public CharacterSelectScreen(Scene.Type nextSceneType) {
        font = Main.game.assets.fontChrustyMd;
        Main.game.audioManager.playMusic(AudioManager.Musics.introMusic);
        particles = new ParticleManager(Main.game.assets);
        this.nextSceneType = nextSceneType;
        switch (nextSceneType) {
            case MICROBIOME:
                this.background = Anims.get(Anims.Type.MICROBIOME_BACKGROUND);
                this.creatureAAnim = Anims.get(Anims.Type.PHAGE_IDLE);
                this.characterDescriptionA = "This phage is built to destroy bacteria. \n\nIt " +
                    "punctures cellular structures with its razor-sharp butt-spike, which is " +
                    "unsettling but also " +
                    "slightly intriguing.";
                this.creatureBAnim = Anims.get(Anims.Type.PARASITE_IDLE);
                this.characterDescriptionB = "This parasite bludgeons offending bacteria with its" +
                    " oversized head-probe." +

                    "\n\nIt appears to have a fair bit of aggression to work through, which is " +
                    "surprising given its lack of capacity for complex thought.";
                break;
            case NEIGHBORHOOD:
                this.background = Anims.get(Anims.Type.NEIGHBORHOOD_OVERLAY);
                this.creatureAAnim = Anims.get(Anims.Type.WORM_IDLE);
                this.characterDescriptionA = "This worm's long, extended body makes it an " +
                    "excellent choice for people who enjoy string cheese, red rope licorice, and " +
                    "other long, tube-like treats.";
                this.creatureBAnim = Anims.get(Anims.Type.ANT_WALK);
                this.characterDescriptionB = "This humble insect is small, but intense.\n\n" +
                    "Do you want ants? Because this is how you get ants.";
                break;
            default:
                this.background = Anims.get(Anims.Type.CITY_BACKGROUND);
                this.creatureAAnim = Anims.get(Anims.Type.RAT_WALK);
                this.characterDescriptionA = "This rodent known for its long, hairless tail is " +
                    " the undisputed world champion when it comes to spreading plague via " +
                    "infected fleas.";
                this.creatureBAnim = Anims.get(Anims.Type.SNAKE_IDLE);
                this.characterDescriptionB = "Don't let the character image's resemblance to a " +
                    "velociraptor" +
                    " fool you:\n\n" +
                    "This is absolutely a snake.";
                break;
        }
    }

    @Override
    public void alwaysUpdate(float delta) {

    }

    @Override
    public void update(float dt) {
        accum += dt;
        if (accum < 1f) {
            return;
        }
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        windowCamera.unproject(touchPos);
        if (characterAButtonBound.contains(touchPos.x, touchPos.y)) {
            characterAButtonHovered = true;
        } else if (characterBButtonBound.contains(touchPos.x, touchPos.y)) {
            characterBButtonHovered = true;
        } else {
            characterAButtonHovered = false;
            characterBButtonHovered = false;
        }
        if (showTutorial){
            if (Gdx.input.justTouched()) {
                showTutorial = false;
            }
            } else {
                if (Gdx.input.justTouched()) {
                    if (characterAButtonHovered) {
                    launchGame(nextSceneType, nextSceneType.creatureTypeA);
                } else if (characterBButtonHovered) {
                    launchGame(nextSceneType, nextSceneType.creatureTypeB);
                }
            }
        }
        particles.update(dt);
    }

    public void renderCharacterSelection(SpriteBatch batch) {
        // draw rectangle with ninepatch
        batch.setColor(1f, 1f, 1f, 0.75f);
        Patches.get(Patches.Type.PLAIN).draw(batch, 0, 0, windowCamera.viewportWidth, windowCamera.viewportHeight);
        batch.setColor(Color.WHITE);
        assets.fontChrustyLg.draw(batch, "Select your tiny creature",
            windowCamera.viewportWidth / 2, windowCamera.viewportHeight - 50, 0, 1, false);
        // character title
        assets.layout.setText(assets.fontChrustyMd, nextSceneType.creatureTypeA.name());
        assets.fontChrustyMd.draw(batch, assets.layout, windowCamera.viewportWidth / 3f - assets.layout.width / 2f, windowCamera.viewportHeight - 150);
        assets.layout.setText(assets.fontChrustyMd, nextSceneType.creatureTypeB.name());
        assets.fontChrustyMd.draw(batch, assets.layout, windowCamera.viewportWidth * 2f / 3f - assets.layout.width / 2f, windowCamera.viewportHeight - 150);
        // character name
        assets.layout.setText(assets.fontChrustySm, "Swarm");
        assets.fontChrustySm.draw(batch, assets.layout, windowCamera.viewportWidth / 3f - assets.layout.width / 2f, windowCamera.viewportHeight - 200);
        assets.layout.setText(assets.fontChrustySm, "Chase");
        assets.fontChrustySm.draw(batch, assets.layout, windowCamera.viewportWidth * 2f / 3f - assets.layout.width / 2f, windowCamera.viewportHeight - 200);
        // character sprites TODO: get the correct animation for nextSceneType.creatureTypeA
        batch.draw(creatureAAnim.getKeyFrame(accum), windowCamera.viewportWidth / 3f - SPRITE_SIZE / 2f, windowCamera.viewportHeight - 500f, SPRITE_SIZE, SPRITE_SIZE);
        batch.draw(creatureBAnim.getKeyFrame(accum), windowCamera.viewportWidth * 2f / 3f - SPRITE_SIZE / 2f, windowCamera.viewportHeight - 500f, SPRITE_SIZE, SPRITE_SIZE);
        // buttons to select character
        if (characterAButtonHovered) batch.setColor(characterAColor);
        Patches.get(Patches.Type.PLAIN).draw(batch, characterAButtonBound.x, characterAButtonBound.y, characterAButtonBound.width, characterAButtonBound.height);
        if (characterBButtonHovered) {
            batch.setColor(characterBColor);
        } else {
            batch.setColor(Color.WHITE);
        }
        Patches.get(Patches.Type.PLAIN).draw(batch, characterBButtonBound.x, characterBButtonBound.y, characterBButtonBound.width, characterBButtonBound.height);
        batch.setColor(Color.WHITE);

        // button text
        assets.layout.setText(assets.fontChrustyMd, "Select");
        assets.fontChrustyMd.draw(batch, "Select", windowCamera.viewportWidth / 3f - assets.layout.width / 2f, 110 + assets.layout.height * 2f);
        assets.layout.setText(assets.fontChrustyMd, "Select");
        assets.fontChrustyMd.draw(batch, assets.layout, windowCamera.viewportWidth * 2f / 3f - assets.layout.width / 2f, 110 + assets.layout.height * 2f);

        // description off to the side
        Patches.get(Patches.Type.PLAIN).draw(batch, 30, 100, 250, 400);
//        assets.fontChrustyMd.draw(batch,"text",
//            windowCamera.viewportWidth / 3f - assets.layout.width / 2f, 110 + assets.layout.height * 2f);
        Patches.get(Patches.Type.PLAIN).draw(batch, windowCamera.viewportWidth - 280, 100, 250, 400);
        assets.fontChrustySm.draw(batch,characterDescriptionA,
            45, windowCamera.viewportHeight - 235, 200, -1, true);
        assets.fontChrustySm.draw(batch,characterDescriptionB,
            1040, windowCamera.viewportHeight - 235, 200, 2, true);



//        assets.layout.setText(assets.fontChrustyMd, "Test",);

        //TODO: add character descriptions

        batch.setColor(Color.WHITE);
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
        batch.setColor(Color.WHITE);
        batch.draw(background.getKeyFrame(accum), 0, 0, windowCamera.viewportWidth, windowCamera.viewportHeight);
        renderCharacterSelection(batch);
        particles.draw(batch, ParticleManager.Layer.FOREGROUND);
        if (showTutorial){
            renderTutorial(batch);
        }
        batch.end();
    }

    void launchGame(Scene.Type sceneType, Player.CreatureType creatureType) {
        if (!exitingScreen){
            exitingScreen = true;
            game.setScreen(new GameScreen(sceneType, creatureType), Transition.Type.DOOMDRIP, 2f);

        }
    }

    Color modalColor = new Color(1f, 1f, 1f, .95f);
    Color fontColor = new Color(1f, 1f, 1f, .95f);
    private void renderTutorial(SpriteBatch batch) {
        batch.setColor(.5f, .5f, 1f, .95f );
        Patches.get(Patches.Type.PLAIN).draw(batch, modalRect.x, modalRect.y, modalRect.width, modalRect.height);
        batch.setColor(modalColor);
        GlyphLayout layout = assets.layout;
        BitmapFont font = assets.font;
        BitmapFont lgFont = assets.fontChrustyLg;
        BitmapFont smFont = assets.fontChrustySm;
        layout.setText(lgFont, "How to Play:", fontColor, modalRect.width, Align.center, true);
        lgFont.draw(batch, layout, modalRect.x, modalRect.y + modalRect.height - 20);
        batch.draw(assets.inputPrompts.get(InputPrompts.Type.key_light_key_w), modalRect.x + 150, modalRect.y + modalRect.height - 180, 75, 75);
        batch.draw(assets.inputPrompts.get(InputPrompts.Type.key_light_key_a), modalRect.x + 70, modalRect.y + modalRect.height - 260, 75, 75);
        batch.draw(assets.inputPrompts.get(InputPrompts.Type.key_light_key_s), modalRect.x + 150, modalRect.y + modalRect.height - 260, 75, 75);
        batch.draw(assets.inputPrompts.get(InputPrompts.Type.key_light_key_d), modalRect.x + 230, modalRect.y + modalRect.height - 260, 75, 75);
        font.getData().setScale(.8f);layout.setText(font, "W to climb up\nS to climb down\nA/D move left and right", fontColor, modalRect.width/2f, Align.center, true);
        font.draw(batch, layout, modalRect.x + modalRect.width/3f,modalRect.y + modalRect.height - 120 );


        batch.draw(assets.inputPrompts.get(InputPrompts.Type.key_light_key_space), modalRect.x + 120, modalRect.y + 250, 150, 75);
        layout.setText(font, "Space to jump", fontColor, modalRect.width/2f, Align.center, true);
        font.draw(batch, layout, modalRect.x + modalRect.width/3f,modalRect.y + 270 + layout.height);

        float enterTileSize = 50f;
        batch.draw(assets.inputPrompts.get(InputPrompts.Type.enter_1), modalRect.x + 120, modalRect.y + 150, enterTileSize, enterTileSize);
        batch.draw(assets.inputPrompts.get(InputPrompts.Type.enter_2), modalRect.x + 120 + enterTileSize, modalRect.y + 150, enterTileSize, enterTileSize);
        batch.draw(assets.inputPrompts.get(InputPrompts.Type.enter_3), modalRect.x + 120, modalRect.y + 150 - enterTileSize, enterTileSize, enterTileSize);
        batch.draw(assets.inputPrompts.get(InputPrompts.Type.enter_4), modalRect.x + 120 + enterTileSize, modalRect.y + 150 -enterTileSize, enterTileSize, enterTileSize);

        layout.setText(font, "Enter to Attack", fontColor, modalRect.width/2f, Align.center, true);
        font.draw(batch, layout, modalRect.x + modalRect.width/3f, modalRect.y + 120 + layout.height);

        font.getData().setScale(1f);
        layout.setText(smFont, "CLick to continue", fontColor, modalRect.width, Align.center, true);
        smFont.draw(batch, layout, modalRect.x, modalRect.y + layout.height + 10);
    }
}
