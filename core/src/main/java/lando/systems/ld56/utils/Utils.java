package lando.systems.ld56.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.Config;

import java.util.List;

public class Utils {
    public static void log(String tag, String msg) {
        if (!Config.Debug.logging) return;
        Gdx.app.log(tag, msg);
    }

    private static List<Color> colors;
    public static Color randomColor() {
        if (colors == null) {
            colors = List.of(Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLACK,
                Color.BLUE, Color.NAVY, Color.ROYAL, Color.SLATE, Color.SKY, Color.CYAN, Color.TEAL,
                Color.GREEN, Color.CHARTREUSE, Color.LIME, Color.FOREST, Color.OLIVE,
                Color.YELLOW, Color.GOLD, Color.GOLDENROD, Color.ORANGE, Color.BROWN, Color.TAN,
                Color.FIREBRICK, Color.RED, Color.SCARLET, Color.CORAL, Color.SALMON,
                Color.PINK, Color.MAGENTA, Color.PURPLE, Color.VIOLET, Color.MAROON);
        }
        var index = MathUtils.random(colors.size() - 1);
        return colors.get(index);
    }

    public static Color hsvToRgb(float hue, float saturation, float value, Color outColor) {
        if (outColor == null) outColor = new Color();
        while (hue < 0) hue += 10f;
        hue = hue % 1f;
        int h = (int) (hue * 6);
        h = h % 6;
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0: outColor.set(value, t, p, 1f); break;
            case 1: outColor.set(q, value, p, 1f); break;
            case 2: outColor.set(p, value, t, 1f); break;
            case 3: outColor.set(p, q, value, 1f); break;
            case 4: outColor.set(t, p, value, 1f); break;
            case 5: outColor.set(value, p, q, 1f); break;
            default: Utils.log("HSV->RGB", "Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
        return outColor;
    }
}
