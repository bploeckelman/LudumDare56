package lando.systems.ld56.assets;

import com.badlogic.gdx.graphics.Texture;
import lando.systems.ld56.utils.Utils;
import text.formic.Stringf;

import java.util.HashMap;
import java.util.Map;

public class Structures {

    public enum Type {
          BACTERIA  ("images/structures/building-bacteria-back_00.png",
              "images/structures/building-bacteria-front_00.png",
              6, 3, .5f)
        , HOUSE_A  ("images/structures/building-house-a-back_00.png",
            "images/structures/building-house-a-front_00.png",
            3, 3, .5f)
        , HOUSE_B  ("images/structures/building-house-b-back_00.png",
            "images/structures/building-house-b-front_00.png",
            5, 3, .5f)
        , HOUSE_C  ("images/structures/building-house-c-back_00.png",
            "images/structures/building-house-c-front_00.png",
            5, 6, .5f)
        , HOUSE_D  ("images/structures/building-house-d-back_00.png",
            "images/structures/building-house-d-front_00.png",
            3, 5, .5f)
        , HOUSE_E  ("images/structures/building-house-e-back_00.png",
            "images/structures/building-house-e-front_00.png",
            3, 7, .5f)
        ;
        public final String internalTextureName;
        public final String externalTextureName;
        public final int rows;
        public final int cols;
        public final float destructionPercent;

        Type(String internalTextureName,
             String externalTextureName,
             int rows,
             int cols,
             float destructionPercent) {
            this.internalTextureName = internalTextureName;
            this.externalTextureName = externalTextureName;
            this.rows = rows;
            this.cols = cols;
            this.destructionPercent = destructionPercent;
        }
    }

    private static final Map<Type, StructureDef> structures = new HashMap<>();

    public static void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : Type.values()) {
            StructureDef def = new StructureDef();
            def.externalTexture = mgr.get(type.externalTextureName, Texture.class);
            def.internalTexture = mgr.get(type.internalTextureName, Texture.class);
            def.cols = type.cols;
            def.rows = type.rows;
            def.collapsePercent = type.destructionPercent;
            structures.put(type, def);
        }
    }

    public static StructureDef get(Type type) {
        var structure = structures.get(type);
        return structure;
    }
}
