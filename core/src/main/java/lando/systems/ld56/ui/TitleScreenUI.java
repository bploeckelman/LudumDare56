package lando.systems.ld56.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Patches;
import lando.systems.ld56.screens.CreditsScreen;
import lando.systems.ld56.screens.IntroScreen;

public class TitleScreenUI {
    float x;
    float y;
    float buttonWidth;
    float buttonHeight;
    Rectangle startGameBound;
    Rectangle settingsBound;
    Rectangle creditBound;
    ButtonOrientation buttonOrientation;
    Button startGameButton;
    Button settingsButton;
    Button creditButton;
    BitmapFont font;
    SettingsUI settingsUI;

    public enum ButtonOrientation {
        VERTICAL,
        HORIZONTAL
    }
    public TitleScreenUI(float x, float y, float buttonWidth, float buttonHeight, BitmapFont font) {
        this(x, y, buttonWidth, buttonHeight, font, ButtonOrientation.VERTICAL);
    }
    public TitleScreenUI(float x, float y, float buttonWidth, float buttonHeight, BitmapFont font, ButtonOrientation buttonOrientation) {
        var MARGIN = 10f;
        this.settingsUI = new SettingsUI(buttonWidth, buttonHeight, font);
        this.x = x;
        this.y = y;
        this.font = font;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        startGameBound = new Rectangle(x, y, buttonWidth, buttonHeight);
        if (buttonOrientation == ButtonOrientation.VERTICAL) {
            settingsBound = new Rectangle(x, y - (buttonHeight + MARGIN), buttonWidth, buttonHeight);
            creditBound = new Rectangle(x, y - (buttonHeight + MARGIN) * 2, buttonWidth, buttonHeight);
        } else {
            settingsBound = new Rectangle(x + (buttonWidth + MARGIN), y, buttonWidth, buttonHeight);
            creditBound = new Rectangle(x + (buttonWidth + MARGIN) * 2, y, buttonWidth, buttonHeight);
        }
        startGameButton = new Button(startGameBound, "Play!", Patches.get(Patches.Type.PLAIN), Patches.get(Patches.Type.PLAIN_DIM), font);
        settingsButton = new Button(settingsBound, "Settings", Patches.get(Patches.Type.PLAIN), Patches.get(Patches.Type.PLAIN_DIM), font);
        creditButton = new Button(creditBound, "Credits", Patches.get(Patches.Type.PLAIN), Patches.get(Patches.Type.PLAIN_DIM), font);

        startGameButton.setOnClickAction(() -> Main.game.setScreen(new IntroScreen()));
        settingsButton.setOnClickAction(() -> settingsUI.isShown = true);
        creditButton.setOnClickAction(() -> Main.game.setScreen(new CreditsScreen()));
    }

    public void update(float x, float y) {
        if (settingsUI.isShown) {
            settingsUI.update(x, y);
            return;
        }

        if (Gdx.input.justTouched()) {
            if (startGameBound.contains(x, y)) {
                startGameButton.onClick();
            } else if (settingsBound.contains(x, y)) {
                settingsButton.onClick();
            } else if (creditBound.contains(x, y)) {
                creditButton.onClick();
            } else {
                //Events.get().dispatch(EventType.MEANINGLESS_CLICK, x, y);
            }
        }

        startGameButton.update(x, y);
        settingsButton.update(x, y);
        creditButton.update(x, y);
    }

    public void draw(SpriteBatch batch) {
        startGameButton.draw(batch);
        settingsButton.draw(batch);
        creditButton.draw(batch);
        if (settingsUI.isShown) settingsUI.draw(batch);
    }
}
