package lando.systems.ld56.utils;

import com.badlogic.gdx.Gdx;
import lando.systems.ld56.Config;

public class Util {
    public static void log(String tag, String msg) {
        if (!Config.Debug.logging) return;
        Gdx.app.log(tag, msg);
    }
}
