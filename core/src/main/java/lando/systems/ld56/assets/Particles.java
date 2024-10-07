package lando.systems.ld56.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld56.utils.Utils;
import text.formic.Stringf;

import java.util.HashMap;
import java.util.Map;

public class Particles {

    public enum Type {
          CIRCLE    (0.1f, "particles/kenney/circle",  Animation.PlayMode.LOOP)
        , DIRT      (0.1f, "particles/kenney/dirt",    Animation.PlayMode.LOOP)
        , FIRE      (0.1f, "particles/kenney/fire",    Animation.PlayMode.LOOP)
        , FLAME     (0.1f, "particles/kenney/flame",   Animation.PlayMode.LOOP)
        , FLARE     (0.1f, "particles/kenney/flare",   Animation.PlayMode.LOOP)
        , LIGHT     (0.1f, "particles/kenney/light",   Animation.PlayMode.LOOP)
        , MAGIC     (0.1f, "particles/kenney/magic",   Animation.PlayMode.LOOP)
        , MUZZLE    (0.1f, "particles/kenney/muzzle",  Animation.PlayMode.LOOP)
        , SCORCH    (0.1f, "particles/kenney/scorch",  Animation.PlayMode.LOOP)
        , SCRATCH   (0.1f, "particles/kenney/scratch", Animation.PlayMode.LOOP)
        , SLASH     (0.1f, "particles/kenney/slash",   Animation.PlayMode.LOOP)
        , SMOKE     (0.1f, "particles/kenney/smoke",   Animation.PlayMode.LOOP)
        , SPARK     (0.1f, "particles/kenney/spark",   Animation.PlayMode.LOOP)
        , STAR      (0.1f, "particles/kenney/star",    Animation.PlayMode.LOOP)
        , SYMBOL    (0.1f, "particles/kenney/symbol",  Animation.PlayMode.LOOP)
        , TRACE     (0.1f, "particles/kenney/trace",   Animation.PlayMode.LOOP)
        , TWIRL     (0.1f, "particles/kenney/twirl",   Animation.PlayMode.LOOP)
        , WINDOW    (0.1f, "particles/kenney/window",  Animation.PlayMode.LOOP)
        , BITE      (0.1f, "particles/bite",           Animation.PlayMode.LOOP)
        , BLOOD     (0.1f, "particles/blood/particle-blood", Animation.PlayMode.LOOP)
        , BLOOD_SPLAT(0.1f, "particles/blood/particle-blood-splat", Animation.PlayMode.LOOP)
        ;
        public final float frameDuration;
        public final String regionsName;
        public final Animation.PlayMode playMode;
        Type(float frameDuration, String regionsName, Animation.PlayMode playMode) {
            this.frameDuration = frameDuration;
            this.regionsName = regionsName;
            this.playMode = playMode;
        }
    }

    private static final Map<Type, Animation<TextureRegion>> animations = new HashMap<>();

    public static void init(Assets assets) {
        var atlas = assets.atlas;
        for (var type : Type.values()) {
            var frames = atlas.findRegions(type.regionsName);
            var animation = new Animation<TextureRegion>(type.frameDuration, frames, type.playMode);
            animations.put(type, animation);
        }
    }

    public static Animation<TextureRegion> get(Particles.Type type) {
        var animation = animations.get(type);
        if (animation == null) {
            Utils.log("Animations", Stringf.format("Animation type '%s', regions '%s' not found", type.name(), type.regionsName));
        }
        return animation;
    }
}
