package lando.systems.ld56.assets;

import com.badlogic.gdx.graphics.Texture;
import lando.systems.ld56.utils.Utils;
import text.formic.Stringf;

import java.util.HashMap;
import java.util.Map;

public class Structures {

    public enum Type {
          BACTERIA_BACK  ("images/structures/building-bacteria-back_00.png")
        , BACTERIA_FRONT ("images/structures/building-bacteria-front_00.png")
        , BRICK_BACK     ("images/structures/building-brick-back_upscale_00.png")
        , BRICK_FRONT    ("images/structures/building-brick-front_00.png")
        ;
        public final String textureName;
        Type(String textureName) {
            this.textureName = textureName;
        }
    }

    private static final Map<Type, Texture> structures = new HashMap<>();

    public static void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : Type.values()) {
            structures.put(type, mgr.get(type.textureName, Texture.class));
        }
    }

    public static Texture get(Type type) {
        var structure = structures.get(type);
        if (structure == null) {
            Utils.log("Structures", Stringf.format("Structure texture '%s' not found for scene type '%s'", type.textureName, type));
        }
        return structure;
    }
}
