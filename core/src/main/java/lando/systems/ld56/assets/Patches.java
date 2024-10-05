package lando.systems.ld56.assets;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import lando.systems.ld56.utils.RectangleI;
import lando.systems.ld56.utils.Utils;
import text.formic.Stringf;

import java.util.HashMap;
import java.util.Map;

public class Patches {

    public enum Type {
          PLAIN                ("ninepatch/plain", 2, 2, 2, 2)
        , PLAIN_DIM            ("ninepatch/plain-dim", 2, 2, 2, 2)
        , PLAIN_GRADIENT       ("ninepatch/plain-gradient", 2, 2, 2, 2)
        ;
        public final String regionName;
        public final RectangleI edges;
        Type(String regionName, int left, int right, int top, int bottom) {
            this.regionName = regionName;
            this.edges = new RectangleI(left, bottom, right, top);
        }
    }

    private static final Map<Type, NinePatch> patches = new HashMap<>();

    public static void init(Assets assets) {
        var atlas = assets.atlas;
        for (var type : Type.values()) {
            var left = type.edges.x;
            var bottom = type.edges.y;
            var right = type.edges.width;
            var top = type.edges.height;
            var textureRegion = atlas.findRegion(type.regionName);
            var ninePatch = new NinePatch(textureRegion, left, right, top, bottom);
            patches.put(type, ninePatch);
        }
    }

    public static NinePatch get(Type type) {
        var patch = patches.get(type);
        if (patch == null) {
            Utils.log("Patches", Stringf.format("Patch type '%s', ninepatch '%s' not found", type.name(), type.regionName));
        }
        return patch;
    }
}
