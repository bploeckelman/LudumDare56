package lando.systems.ld56.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld56.Config;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.assets.Patches;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.utils.Utils;

public class SettingsUI {

    Rectangle settingsBound;
    Rectangle musicSliderBound;
    Rectangle soundSliderBound;
    Rectangle backButtonBound;
    Slider musicSlider;
    Slider soundSlider;
    Button backButton;
    BitmapFont font;
    float x;
    float y;
    float buttonWidth;
    float buttonHeight;
    boolean isShown = false;
    Assets assets = Main.game.assets;

    public SettingsUI(float buttonWidth, float buttonHeight, BitmapFont font) {
        var MARGIN = 30f;
        float width = Config.Screen.window_width / 2f;
        float height = Config.Screen.window_height / 2f;
        this.x = width / 2f;
        this.y = height / 2f;
        this.font = font;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        settingsBound = new Rectangle(x, y, width, height);
        musicSliderBound = new Rectangle(x + 50, y + 200, width - 100, 50);
        soundSliderBound = new Rectangle(x + 50, y + 100, width - 100, 50);
        backButtonBound = new Rectangle(x + 50, y + 25, width - 100, 50);
        float musicVolume = Main.game.audioManager.musicVolume.floatValue();
        float soundVolume = Main.game.audioManager.soundVolume.floatValue();
        Utils.log("SettingsUI", "Music volume: " + musicVolume);
        Utils.log("SettingsUI", "Sound volume: " + soundVolume);
        musicSlider = new Slider(musicSliderBound.x, musicSliderBound.y, musicSliderBound.width, musicSliderBound.height, musicVolume, Anims.Type.NEIGHBORHOOD_SKY, Anims.Type.DOG);
        soundSlider = new Slider(soundSliderBound.x, soundSliderBound.y, soundSliderBound.width, soundSliderBound.height, soundVolume, Anims.Type.NEIGHBORHOOD_OVERLAY, Anims.Type.CAT);
        backButton = new Button(backButtonBound, "Back", Patches.get(Patches.Type.PLAIN), Patches.get(Patches.Type.PLAIN_DIM), assets.fontChrustySm);

        musicSlider.setOnChangeAction(() -> {
            Main.game.audioManager.setMusicVolume(musicSlider.getValue());
            if (Main.game.audioManager.currentMusic != null) Main.game.audioManager.currentMusic.setVolume(musicSlider.getValue());
            Utils.log("SettingsUI", "Music volume: " + musicSlider.getValue());
        });
        soundSlider.setOnChangeAction(() -> {
            Main.game.audioManager.setSoundVolume(soundSlider.getValue());
            Main.game.audioManager.playSound(AudioManager.Sounds.coin);
            Utils.log("SettingsUI", "Sound volume: " + soundSlider.getValue());
        });
        backButton.setOnClickAction(() -> this.isShown = false);
    }

    public void update(float x, float y) {
        musicSlider.update(x, y);
        soundSlider.update(x, y);
        backButton.update(x, y);
        if (Gdx.input.justTouched()) {
            if (backButtonBound.contains(x, y)) {
                backButton.onClick();
            }
        }

    }

    public void draw(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, 0.5f);
        Patches.get(Patches.Type.PLAIN).draw(batch, 0, 0, Config.Screen.window_width, Config.Screen.window_height);
        batch.setColor(1f, 1f, 1f, 1f);
        Patches.get(Patches.Type.PLAIN).draw(batch, settingsBound.x, settingsBound.y, settingsBound.width, settingsBound.height);
        assets.layout.setText(font, "Settings");
        font.draw(batch, assets.layout, Config.Screen.window_width / 2f - assets.layout.width / 2f, settingsBound.y + settingsBound.height - 20);
        assets.layout.setText(assets.fontChrustySm, "Music Volume");
        assets.fontChrustySm.draw(batch, assets.layout, Config.Screen.window_width / 2f - assets.layout.width / 2f, musicSliderBound.y + musicSliderBound.height + 25);
        assets.layout.setText(assets.fontChrustySm, "Sound Volume");
        assets.fontChrustySm.draw(batch, assets.layout, Config.Screen.window_width / 2f - assets.layout.width / 2f, soundSliderBound.y + soundSliderBound.height + 20);
        musicSlider.draw(batch);
        soundSlider.draw(batch);
        backButton.draw(batch);
    }
}
