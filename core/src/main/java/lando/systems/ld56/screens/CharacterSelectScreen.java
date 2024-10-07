package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    public CharacterSelectScreen(Scene.Type nextSceneType) {
        font = Main.game.assets.fontChrustyMd;
        Main.game.audioManager.playMusic(AudioManager.Musics.introMusic);
        particles = new ParticleManager(Main.game.assets);
        this.nextSceneType = nextSceneType;
    }

    @Override
    public void alwaysUpdate(float delta) {

    }

    @Override
    public void update(float dt) {
        accum += dt;
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
        batch.setColor(Color.WHITE);
        Patches.get(Patches.Type.PLAIN).draw(batch, 0, 0, windowCamera.viewportWidth, windowCamera.viewportHeight);
        assets.fontChrustyLg.draw(batch, "Select your tiny character", windowCamera.viewportWidth / 2, windowCamera.viewportHeight - 50, 0, 1, false);
        // character title
        assets.layout.setText(assets.fontChrustyMd, "Unit");
        assets.fontChrustyMd.draw(batch, assets.layout, windowCamera.viewportWidth / 3f - assets.layout.width / 2f, windowCamera.viewportHeight - 150);
        assets.layout.setText(assets.fontChrustyMd, "Swarm");
        assets.fontChrustyMd.draw(batch, assets.layout, windowCamera.viewportWidth * 2f / 3f - assets.layout.width / 2f, windowCamera.viewportHeight - 150);
        // character name
        assets.layout.setText(assets.fontChrustySm, nextSceneType.creatureTypeA.name());
        assets.fontChrustySm.draw(batch, assets.layout, windowCamera.viewportWidth / 3f - assets.layout.width / 2f, windowCamera.viewportHeight - 200);
        assets.layout.setText(assets.fontChrustySm, nextSceneType.creatureTypeB.name());
        assets.fontChrustySm.draw(batch, assets.layout, windowCamera.viewportWidth * 2f / 3f - assets.layout.width / 2f, windowCamera.viewportHeight - 200);
        // character sprites TODO: get the correct animation for nextSceneType.creatureTypeA
        batch.draw(Anims.get(Anims.Type.PHAGE_IDLE).getKeyFrame(accum), windowCamera.viewportWidth / 3f - SPRITE_SIZE / 2f, windowCamera.viewportHeight - 500f, SPRITE_SIZE, SPRITE_SIZE);
        batch.draw(Anims.get(Anims.Type.PARASITE_IDLE).getKeyFrame(accum), windowCamera.viewportWidth * 2f / 3f - SPRITE_SIZE / 2f, windowCamera.viewportHeight - 500f, SPRITE_SIZE, SPRITE_SIZE);
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
        Patches.get(Patches.Type.PLAIN).draw(batch, windowCamera.viewportWidth - 280, 100, 250, 400);

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
