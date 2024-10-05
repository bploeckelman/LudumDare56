package lando.systems.ld56.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld56.utils.Utils;
import text.formic.Stringf;

import java.util.HashMap;
import java.util.Map;

public class Icons {

    public enum Type {
          TOKEN_BLANK         ("icons/kenney-board-game/flip_empty")
        , TOKEN_FILLED        ("icons/kenney-board-game/flip_full")
        , TOKEN_HALF_FILLED   ("icons/kenney-board-game/flip_half")
        , TOKEN_CHECKMARK     ("icons/kenney-board-game/flip_head")
        , TOKEN_X             ("icons/kenney-board-game/flip_tails")
        ;
        public final String regionName;
        Type(String regionName) {
            this.regionName = regionName;
        }
    }

    private static final Map<Type, TextureRegion> icons = new HashMap<>();

    public static void init(Assets assets) {
        var atlas = assets.atlas;

        for (var type : Type.values()) {
            var textureRegion = atlas.findRegion(type.regionName);
            icons.put(type, textureRegion);
        }
    }

    public static TextureRegion get(Type type) {
        var region = icons.get(type);
        if (region == null) {
            Utils.log("Icons", Stringf.format("Icon type '%s', texture region '%s' not found", type.name(), type.regionName));
        }
        return region;
    }
}
