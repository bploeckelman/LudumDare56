package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.assets.Patches;
import lando.systems.ld56.assets.Transition;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.scene.Scene;

public class CharacterSelectScreen extends BaseScreen {

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
                    "curious given that it lacks the capacity for complex thought.";
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
                this.background = Anims.get(Anims.Type.BACKGROUND_1);
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
        if (Gdx.input.justTouched()) {
            if (characterAButtonHovered) {
                launchGame(nextSceneType, nextSceneType.creatureTypeA);
            }
            else if (characterBButtonHovered) {
                launchGame(nextSceneType, nextSceneType.creatureTypeB);
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
        batch.end();
    }

    void launchGame(Scene.Type sceneType, Player.CreatureType creatureType) {
        if (!exitingScreen){
            exitingScreen = true;
            game.setScreen(new GameScreen(sceneType, creatureType), Transition.Type.DOOMDRIP, 2f);

        }
    }
}
