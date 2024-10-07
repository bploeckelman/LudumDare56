package lando.systems.ld56.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.utils.Utils;

public class Slider {
    private TextureRegion backgroundTexture;
    private TextureRegion knobTexture;
    private Rectangle backgroundBounds;
    private Rectangle knobBounds;
    private float sliderValue; // Value between 0 and 1
    private Runnable onChangeAction;
    private float previousValue;
    private final float knobSize = 50f;

    public Slider(float x, float y, float width, float height, float initialValue, Anims.Type backgroudType, Anims.Type knobType) {
        backgroundTexture = Anims.get(backgroudType).getKeyFrame(0f); // Replace with your image
        knobTexture = Anims.get(knobType).getKeyFrame(0f); // Replace with your image
        backgroundBounds = new Rectangle(x, y, width, height);
        knobBounds = new Rectangle(x + (backgroundBounds.width - knobSize) * initialValue, y, knobSize, knobSize);
        sliderValue = initialValue;
        previousValue = initialValue;
    }

    public void update(float x, float y) {
        if (Gdx.input.isTouched()) {
            float touchX = x;
            float touchY = y;

            if (backgroundBounds.contains(touchX, touchY)) {
                float knobX = touchX - knobBounds.width / 2;
                knobX = MathUtils.clamp(knobX, backgroundBounds.x, backgroundBounds.x + backgroundBounds.width - knobBounds.width);
                knobBounds.x = knobX;

                sliderValue = (knobBounds.x - backgroundBounds.x) / (backgroundBounds.width - knobBounds.width);
                sliderValue = MathUtils.floor(sliderValue * 10) / 10f;
                knobBounds.x = backgroundBounds.x + (backgroundBounds.width - knobBounds.width) * sliderValue;
            }
            if (sliderValue != previousValue) {
                if (onChangeAction != null) {
                    onChangeAction.run();
                }
                previousValue = sliderValue;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(backgroundTexture, backgroundBounds.x, backgroundBounds.y, backgroundBounds.width, backgroundBounds.height);
        batch.draw(knobTexture, knobBounds.x, knobBounds.y, knobBounds.width, knobBounds.height);
    }

    public float getValue() {
        Utils.log("Slider", String.valueOf(sliderValue));
        return sliderValue;
    }

    public void setOnChangeAction(Runnable onChangeAction) {
        this.onChangeAction = onChangeAction;
    }
}
