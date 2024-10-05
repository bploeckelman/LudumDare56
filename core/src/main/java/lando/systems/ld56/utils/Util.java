package lando.systems.ld56.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.Config;

import java.util.List;

public class Util {
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
}
