package lando.systems.ld56.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld56.utils.Utils;
import text.formic.Stringf;

import java.util.HashMap;
import java.util.Map;

public class Anims {

    public enum Type {
        // pets --------------------------------------------------------------------------------------------------------
          CAT            (0.1f, "pets/cat", Animation.PlayMode.LOOP)
        , DOG            (0.1f, "pets/dog", Animation.PlayMode.LOOP)
        , KITTEN         (0.1f, "pets/kitten", Animation.PlayMode.LOOP)
        , ROSS_DOG       (0.1f, "pets/ross-dog", Animation.PlayMode.LOOP)
        , WHITE_LAB_DOG  (0.1f, "pets/white-lab-dog", Animation.PlayMode.LOOP)
        // rat character -----------------------------------------------------------------------------------------------
        , RAT_IDLE       (0.2f, "creatures/rat/player-rat-idle", Animation.PlayMode.LOOP)
        , RAT_WALK       (0.1f, "creatures/rat/player-rat-walk", Animation.PlayMode.LOOP)
        , RAT_BITE       (0.1f, "creatures/rat/player-rat-bite", Animation.PlayMode.NORMAL)
        // TODO: add these animations - using 'idle' as a placeholder for now
        , RAT_JUMP       (0.1f, "creatures/rat/player-rat-idle", Animation.PlayMode.LOOP)
        , RAT_FALL       (0.1f, "creatures/rat/player-rat-idle", Animation.PlayMode.LOOP)
        // ant character -----------------------------------------------------------------------------------------------
        , ANT_PUNCH      (0.075f, "creatures/ant/player-ant-punch", Animation.PlayMode.LOOP)
        // -------------------------------------------------------------------------------------------------------------
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

    public static Animation<TextureRegion> get(Anims.Type type) {
        var animation = animations.get(type);
        if (animation == null) {
            Utils.log("Animations", Stringf.format("Animation type '%s', regions '%s' not found", type.name(), type.regionsName));
        }
        return animation;
    }
}
