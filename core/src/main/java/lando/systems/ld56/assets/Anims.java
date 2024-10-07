package lando.systems.ld56.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.utils.RectangleI;
import lando.systems.ld56.utils.Utils;
import text.formic.Stringf;

import java.util.HashMap;
import java.util.Map;

public class Anims {

    public enum State { IDLE, WALK, JUMP, FALL, STICK, HURT, ATTACK, SPECIAL_ATTACK }

    public enum Type {
        // pets --------------------------------------------------------------------------------------------------------
          CAT            (0.1f, "pets/cat", Animation.PlayMode.LOOP)
        , DOG            (0.1f, "pets/dog", Animation.PlayMode.LOOP)
        , KITTEN         (0.1f, "pets/kitten", Animation.PlayMode.LOOP)
        , ROSS_DOG       (0.1f, "pets/ross-dog", Animation.PlayMode.LOOP)
        , WHITE_LAB_DOG  (0.1f, "pets/white-lab-dog", Animation.PlayMode.LOOP)
        // kitten character --------------------------------------------------------------------------------------------
        // NOTE: test for snake/swarm 'segment'
        , KITTEN_IDLE    (0.1f, "creatures/kittens/kitten", Animation.PlayMode.LOOP)
        // Backgrounds -------------------------------------------------------------------------------------------------
        , MICROBIOME_BACKGROUND(0.1f, "backgrounds/background-biome", Animation.PlayMode.LOOP_PINGPONG)
        , BACKGROUND_1   (0.1f, "backgrounds/background-level-1", Animation.PlayMode.LOOP)
        , NEIGHBORHOOD_OVERLAY(0.1f, "backgrounds/background-neighborhood-overlay", Animation.PlayMode.LOOP)
        , NEIGHBORHOOD_SKY(0.1f, "backgrounds/background-neighborhood-sky", Animation.PlayMode.LOOP)
        // phage character ---------------------------------------------------------------------------------------------
        , PHAGE_IDLE     (0.1f, "creatures/phage/phage-idle/player-phage-idle", Animation.PlayMode.LOOP, new RectangleI(-8, 6, 16, 50))
        , PHAGE_WALK     (0.1f, "creatures/phage/phage-walk/player-phage-walk", Animation.PlayMode.LOOP, new RectangleI(-8, 6, 16, 50))
        , PHAGE_JUMP     (0.1f, "creatures/phage/phage-jump/player-phage-jump", Animation.PlayMode.NORMAL, new RectangleI(-8, 14, 16, 50))
        , PHAGE_FALL     (0.1f, "creatures/phage/phage-idle/player-phage-idle", Animation.PlayMode.LOOP, new RectangleI(-8, 10, 16, 50)) // TODO: setup a custom anim with frames from jump/idle
        , PHAGE_STICK    (0.1f, "creatures/phage/phage-stick/player-phage-stick", Animation.PlayMode.NORMAL, new RectangleI(-8, 0, 16, 45))
        , PHAGE_HURT     (0.1f, "creatures/phage/phage-hurt/player-phage-hurt", Animation.PlayMode.NORMAL, new RectangleI(-8, 4, 16, 45))
        , PHAGE_ATTACK   (0.1f, "creatures/phage/phage-stick/player-phage-stick", Animation.PlayMode.NORMAL, new RectangleI(-10, 6, 20, 40))
        // parasite character ------------------------------------------------------------------------------------------
        // TODO: placeholder anims
        , PARASITE_IDLE  (0.1f, "creatures/phage/phage-idle/player-phage-idle", Animation.PlayMode.LOOP)
        , PARASITE_WALK  (0.1f, "creatures/phage/phage-walk/player-phage-walk", Animation.PlayMode.LOOP)
        , PARASITE_JUMP  (0.1f, "creatures/phage/phage-jump/player-phage-jump", Animation.PlayMode.NORMAL)
        , PARASITE_FALL  (0.1f, "creatures/phage/phage-idle/player-phage-idle", Animation.PlayMode.LOOP) // TODO: setup a custom anim with frames from jump/idle
        , PARASITE_STICK (0.1f, "creatures/phage/phage-stick/player-phage-stick", Animation.PlayMode.NORMAL)
        , PARASITE_HURT  (0.1f, "creatures/phage/phage-hurt/player-phage-hurt", Animation.PlayMode.NORMAL)
        , PARASITE_ATTACK(0.1f, "creatures/phage/phage-headbutt/player-phage-headbutt", Animation.PlayMode.NORMAL)
        // worm character ----------------------------------------------------------------------------------------------
        // TODO: placeholder anims
        , WORM_IDLE      (0.1f, "creatures/phage/phage-idle/player-phage-idle", Animation.PlayMode.LOOP)
        , WORM_WALK      (0.1f, "creatures/phage/phage-walk/player-phage-walk", Animation.PlayMode.LOOP)
        , WORM_JUMP      (0.1f, "creatures/phage/phage-jump/player-phage-jump", Animation.PlayMode.NORMAL)
        , WORM_FALL      (0.1f, "creatures/phage/phage-idle/player-phage-idle", Animation.PlayMode.LOOP) // TODO: setup a custom anim with frames from jump/idle
        , WORM_STICK     (0.1f, "creatures/phage/phage-stick/player-phage-stick", Animation.PlayMode.NORMAL)
        , WORM_HURT      (0.1f, "creatures/phage/phage-hurt/player-phage-hurt", Animation.PlayMode.NORMAL)
        , WORM_ATTACK    (0.1f, "creatures/phage/phage-headbutt/player-phage-headbutt", Animation.PlayMode.NORMAL)
        // snake character ----------------------------------------------------------------------------------------------
        // TODO: placeholder anims
        , SNAKE_IDLE      (0.1f, "creatures/snake/player-snake-head", Animation.PlayMode.LOOP)
        , SNAKE_WALK      (0.1f, "creatures/snake/player-snake-head", Animation.PlayMode.LOOP)
        , SNAKE_JUMP      (0.1f, "creatures/snake/player-snake-head", Animation.PlayMode.LOOP)
        , SNAKE_FALL      (0.1f, "creatures/snake/player-snake-head", Animation.PlayMode.LOOP)
        , SNAKE_STICK     (0.1f, "creatures/snake/player-snake-head", Animation.PlayMode.LOOP)
        , SNAKE_HURT      (0.1f, "creatures/snake/player-snake-head", Animation.PlayMode.LOOP)
        , SNAKE_ATTACK    (0.1f, "creatures/snake/player-snake-head-bite", Animation.PlayMode.LOOP)
        , BALL_IDLE      (0.1f, "creatures/snake/player-snake-ball", Animation.PlayMode.LOOP)
        , BALL_WALK      (0.1f, "creatures/snake/player-snake-ball", Animation.PlayMode.LOOP)
        , BALL_JUMP      (0.1f, "creatures/snake/player-snake-ball", Animation.PlayMode.LOOP)
        , BALL_FALL      (0.1f, "creatures/snake/player-snake-ball", Animation.PlayMode.LOOP)
        , BALL_STICK     (0.1f, "creatures/snake/player-snake-ball", Animation.PlayMode.LOOP)
        , BALL_HURT      (0.1f, "creatures/snake/player-snake-ball", Animation.PlayMode.LOOP)
        , BALL_ATTACK    (0.1f, "creatures/snake/player-snake-ball", Animation.PlayMode.LOOP)
        // ant character -----------------------------------------------------------------------------------------------
        , ANT_PUNCH      (0.075f, "creatures/ant/player-ant-punch", Animation.PlayMode.LOOP)
        , ANT_CLIMB_PUNCH(0.075f, "creatures/ant/player-ant-up-punch", Animation.PlayMode.LOOP)
        // rat character -----------------------------------------------------------------------------------------------
        , RAT_IDLE       (0.2f, "creatures/rat/player-rat-idle", Animation.PlayMode.LOOP)
        , RAT_WALK       (0.1f, "creatures/rat/player-rat-walk", Animation.PlayMode.LOOP)
        , RAT_ATTACK       (0.1f, "creatures/rat/player-rat-bite", Animation.PlayMode.NORMAL)
        // TODO: add these animations - using 'idle' as a placeholder for now
        , RAT_JUMP       (0.1f, "creatures/rat/player-rat-idle", Animation.PlayMode.LOOP)
        , RAT_FALL       (0.1f, "creatures/rat/player-rat-idle", Animation.PlayMode.LOOP)

        // enemies
        , TARDIGRADE     (0.1f, "pets/cat", Animation.PlayMode.LOOP)
        , BACTERIA       (0.1f, "pets/dog", Animation.PlayMode.LOOP)
        , BIRD           (0.1f, "pets/cat", Animation.PlayMode.LOOP)
        , ANIMAL         (0.1f, "pets/dog", Animation.PlayMode.LOOP)
        , TRUCK          (0.1f, "pets/cat", Animation.PlayMode.LOOP)
        , PERSON         (0.1f, "pets/dog", Animation.PlayMode.LOOP)
        , GOOMBA         (0.1f, "pets/cat", Animation.PlayMode.LOOP)
        , BOWSER         (0.1f, "pets/dog", Animation.PlayMode.LOOP)

        // snake character ---------------------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------------------------------------
        ;

        public final float frameDuration;
        public final String regionsName;
        public final Animation.PlayMode playMode;
        public final RectangleI colliderRect;
        Type(float frameDuration, String regionsName, Animation.PlayMode playMode) {
            this(frameDuration, regionsName, playMode, Anims.defaultColliderRect);
        }
        Type(float frameDuration, String regionsName, Animation.PlayMode playMode, RectangleI colliderRect) {
            this.frameDuration = frameDuration;
            this.regionsName = regionsName;
            this.playMode = playMode;
            this.colliderRect = colliderRect;
        }
    }

    public static final RectangleI defaultColliderRect = new RectangleI(-16, 0, 32, 32);
    private static final Map<Type, Animation<TextureRegion>> animations = new HashMap<>();
    private static final Map<Player.CreatureType, Map<State, Type>> creatureAnims = new HashMap<>();

    public static void init(Assets assets) {
        var atlas = assets.atlas;
        for (var type : Type.values()) {
            var frames = atlas.findRegions(type.regionsName);
            if (frames.isEmpty()) {
                Utils.log("Anims", Stringf.format("No atlas regions found for type '%s' regionsName '%s'", type, type.regionsName));
                continue;
            }

            var animation = new Animation<TextureRegion>(type.frameDuration, frames, type.playMode);
            animations.put(type, animation);
        }

        for (var creatureType : Player.CreatureType.values()) {
            var creatureName = creatureType.name();
            creatureAnims.putIfAbsent(creatureType, new HashMap<>());

            for (var animType : Type.values()) {
                if (!animType.name().startsWith(creatureName)) continue;

                for (var state : State.values()) {
                    if (!animType.name().endsWith(state.name())) continue;

                    creatureAnims.get(creatureType).put(state, animType);
                }
            }
        }
    }

    public static Animation<TextureRegion> get(Anims.Type type) {
        var animation = animations.get(type);
        if (animation == null) {
            Utils.log("Animations", Stringf.format("Animation type '%s', regions '%s' not found", type.name(), type.regionsName));
        }
        return animation;
    }

    public static Animation<TextureRegion> get(Player.CreatureType creatureType, Anims.State state) {
        Animation<TextureRegion> animation = null;
        var animStates = creatureAnims.get(creatureType);
        if (animStates == null) {
            Utils.log("Animations", Stringf.format("Animations for creature type '%s' not found", creatureType));
        } else {
            var animType = animStates.get(state);
            if (animType == null) {
                Utils.log("Animations", Stringf.format("No anim types found for creature type '%s' and anim state '%s'", creatureType, state));
            } else {
                animation = get(animType);
                if (animation == null) {
                    Utils.log("Animations", Stringf.format("No animation found for creature type '%s' and anim state '%s', anim type '%s'", creatureType, state, animType));
                }
            }
        }
        return animation;
    }

    public static Anims.Type getType(Player.CreatureType creatureType, Anims.State state) {
        Anims.Type animType = null;
        var animStates = creatureAnims.get(creatureType);
        if (animStates == null) {
            Utils.log("Animations", Stringf.format("Animations for creature type '%s' not found", creatureType));
        } else {
            animType = animStates.get(state);
            if (animType == null) {
                Utils.log("Animations", Stringf.format("No anim types found for creature type '%s' and anim state '%s'", creatureType, state));
            }
        }
        return animType;
    }
}
