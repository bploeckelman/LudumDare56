package lando.systems.ld56.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.entities.components.Animator;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Mover;
import lando.systems.ld56.entities.components.Position;
import lando.systems.ld56.entities.components.StructureDamage;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.particles.effects.*;
import lando.systems.ld56.scene.Scene;
import lando.systems.ld56.utils.Calc;
import lando.systems.ld56.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;
import text.formic.Stringf;

public class Player extends Entity {

    public enum State { NORMAL, ATTACK, KNOCK_OUT }
    public enum Mode { SWARM, CHASE}
    public enum CreatureType {
        // Microbiome
          PHAGE(Mode.SWARM)
        , PARASITE(Mode.CHASE)
        // Neighborhood
        , WORM(Mode.SWARM)
        , ANT(Mode.CHASE)
        // City
        , RAT(Mode.SWARM)
        , SNAKE(Mode.CHASE)
        // Mushroom Kingdom
        , MARIO(Mode.SWARM)
        , LUIGI(Mode.SWARM)
        ;
        public final Mode mode;
        public final Anims.Type[] animTypes;
        CreatureType(Mode mode, Anims.Type... animTypes) {
            this.mode = mode;
            this.animTypes = animTypes;
        }
    }

    public final Scene scene;

    public Position position;
    public Animator animator;
    public Collider collider;
    public Mover mover;
    public Color defaultColor = Color.WHITE.cpy();

    private State state = State.NORMAL;
    public final CreatureType creatureType;
    private final ParticleManager particleManager;

    private boolean wasOnGround = false;
    private boolean climbing = false;
    private float jumpHoldTimer = 0;
    private float attackTimer = 0;
    public float attackStrength = 0.4f;
    private boolean attackSuccess = false;
    private Collider attackCollider;
    private float accum = 0f;
    public float detachSoundCountdownTimer = 0f;

    private final float jumpHoldDuration = 0.1f;
    private final GridPoint2 offset = new GridPoint2(0, 0);

    private final int maxNumFollowersChase = 10;
    private final int maxNumFollowersSwarm = 30;
    private final int chaseFollowerDistance = 20; // TODO: should be related to the followers's size?
    public final Array<Follower> followers = new Array<>();
    public final Queue<GridPoint2> positionHistoryQueue = new Queue<>();

    public Player(Scene scene, CreatureType creatureType, float x, float y, ParticleManager particleManager) {
        this.scene = scene;
        this.creatureType = creatureType;
        this.particleManager = particleManager;
        var animType = Anims.getType(creatureType, Anims.State.IDLE);

        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, Anims.get(animType));
        this.collider = Collider.makeRect(this, Collider.Type.player, animType.colliderRect);
        this.mover = new Mover(this, position, collider);

        var scale = 1.5f;
        if (creatureType == CreatureType.PARASITE) {
            scale = 0.5f;
        } else if (creatureType == CreatureType.ANT) {
            var grey = 23 / 255f;
            defaultColor.set(grey, grey, grey, 1f);
            scale = 2f;
        }

        animator.scale.set(scale, scale);
        animator.defaultScale.set(scale, scale);
        mover.speed.y = mover.gravity;

        var numFollowers = creatureType.mode == Mode.SWARM ? maxNumFollowersSwarm : maxNumFollowersChase;
        for (int i = 0; i < numFollowers; i++) {
            var pos = Utils.obtainGridPoint2(position);
            if (i != 0) {
                var nudgeX = MathUtils.random(-20, 20);
                var nudgeY = MathUtils.random(-20, 20);
                pos.add(nudgeX, nudgeY);
            }
            positionHistoryQueue.addFirst(pos);

            var speedX = 0;
            var speedY = 0;
            if (creatureType.mode == Mode.SWARM) {
                scale = 0.5f;
                var angle = MathUtils.random(0, 365);
                var speed = MathUtils.random(25, 150);
                speedX = (int) (MathUtils.cosDeg(angle) * speed);
                speedY = (int) (MathUtils.sinDeg(angle) * speed);
            }
            if (creatureType == Player.CreatureType.SNAKE) {
                scale = 1;
            } else if (creatureType == CreatureType.ANT) {
                scale = 1f;
            }
            if (creatureType == CreatureType.PARASITE) {
                scale = 0.5f;
            }
            var follower = new Follower(this, pos.x, pos.y, scale, speedX, speedY);
            follower.attached = true;
            followers.add(follower);
        }
    }

    public void update(float dt, boolean gameEnding) {
        accum += dt;
        detachSoundCountdownTimer -= dt;
        if (hitInvincibility > 0) {
            hitInvincibility -= dt;
        }
//        Gdx.app.log("Detach timer", String.valueOf(detachSoundCountdownTimer));
        // collect input
        var inputMoveDirX = 0;
        if      (Gdx.input.isKeyPressed(Input.Keys.A)) inputMoveDirX = -1;
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) inputMoveDirX =  1;

        var attackJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
        var jumpJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        var jumpHeld = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        var climbUpHeld = Gdx.input.isKeyPressed(Input.Keys.W)
                       || Gdx.input.isKeyPressed(Input.Keys.UP)
                       || Gdx.input.isKeyPressed(Input.Keys.DPAD_UP);
        var climbDownHeld = Gdx.input.isKeyPressed(Input.Keys.S)
                         || Gdx.input.isKeyPressed(Input.Keys.DOWN)
                         || Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN);

        if (gameEnding) {
            inputMoveDirX = 0;
            jumpJustPressed = false;
            jumpHeld = false;
            climbUpHeld = false;
            climbDownHeld = false;
            attackJustPressed = false;
        }

        var isOnGround = mover.isOnGround();
        // animation stuff that should always happen regardless of state
        {
            // just landed - squash
            if (!wasOnGround && isOnGround) {
                animator.scale.set(animator.facing * 1.5f, 0.7f);
            }

            // always be lerping squash/stretch back towards default
            animator.scale.x = Calc.approach(animator.scale.x, animator.defaultScale.x * animator.facing, dt * 4f);
            animator.scale.y = Calc.approach(animator.scale.y, animator.defaultScale.y, dt * 4f);
        }

        switch (state) {
            case NORMAL: {
                // set current animations
                if (isOnGround) {
                    if (inputMoveDirX != 0) {
                        animator.play(creatureType, Anims.State.WALK);
                        particleManager.effects.get(ParticleEffectType.DIRT).spawn(new DirtEffect.Params(position.x(), position.y()));
                    } else {
                        animator.play(creatureType, Anims.State.IDLE);
                    }
                } else {
                    if (mover.speed.y < 0) {
                        animator.play(creatureType, Anims.State.FALL);
                    }
                }

                // horizontal movement
                {
                    // apply acceleration based on input, and adjust the speed if we're climbing so its 'grippier'
                    var climbAdjust = 1f;
                    if (climbing) {
                        climbAdjust = 0.25f;
                    }
                    var accel = climbAdjust * ((isOnGround) ? mover.groundAccel : mover.airAccel);
                    mover.speed.x += inputMoveDirX * accel * dt;

                    // cap movement speed at a maximum by lerping hard towards the max if we're over it
                    if (Calc.abs(mover.speed.x) > mover.maxGroundSpeedX) {
                        mover.speed.x = Calc.approach(mover.speed.x, Calc.sign(mover.speed.x) * mover.maxGroundSpeedX, dt * 2000);
                    }

                    // apply friction
                    if (inputMoveDirX == 0 && (isOnGround || climbing)) {
                        var friction = climbing ? mover.frictionClimb : mover.friction;
                        mover.speed.x  = Calc.approach(mover.speed.x, 0, friction * dt);
                    }
                }

                // vertical movement
                {
                    // trigger a jump
                    if (jumpJustPressed && (isOnGround || climbing)) {
                        // stop climbing
                        if (climbing) {
                            climbing = false;
                            mover.gravity = mover.gravityDefault;
                            animator.rotation = 0;
                        }

                        // start jumping
                        jumpHoldTimer = jumpHoldDuration;
                        Main.game.audioManager.playSound(AudioManager.Sounds.jump);

                        // update animation - stretch on jump
                        animator.play(creatureType, Anims.State.JUMP);
                        animator.scale.set(animator.facing * 0.7f, 1.5f);

                        // adjust horizontal movement when jumping
                        mover.speed.x = inputMoveDirX * mover.maxAirSpeedX;
                    }

                    // trigger a climb
                    var canClimb = collider.check(offset.set(0, 1), Collider.Type.climbable);
                    if (climbUpHeld && canClimb) {
                        climbing = true;
                        // gravity doesn't apply while climbing
                        mover.gravity = 0;
                        // cancel horiz movement on climb start
                        mover.stopX();

                        // figure out which side of a climbable structure we're on
                        int dir = 0;
                        float edge = 0f;
                        var structure = collider.check(Collider.Type.structure);
                        if (structure != null) {
                            if (structure.entity instanceof StructureDamage) {
                                var structureDamage = (StructureDamage) structure.entity;
                                var bounds = structureDamage.structure.bounds;

                                // are we closer to left or right side?
                                var distToLeftEdge = Calc.abs(position.x() - bounds.x);
                                var distToRightEdge = Calc.abs(position.x() - (bounds.x + bounds.width));
                                if (distToLeftEdge < distToRightEdge) {
                                    dir = 1;
                                    edge = bounds.x;
                                } else {
                                    dir = -1;
                                    edge = bounds.x + bounds.width;
                                }
                            }
                        }

                        // rotate to correct orientation, and move to the horizontal edge
                        if (dir != 0) {
                            position.x(edge);

                            animator.facing = dir;
                            if (dir == 1) {
                                animator.rotation = 90;
                            } else {
                                animator.rotation = -90;
                            }
                        }
                    }
                }

                if (attackJustPressed) {
                    setState(State.ATTACK);
                }
            } break;
            case ATTACK: {
                attackTimer -= dt;
                if (attackTimer <= 0) {
                    setState(State.NORMAL);
                } else if (!attackSuccess) {
                    var collider = attackCollider.check(Collider.Type.structure);
                    if (collider != null) {
                        // safe(ish) to use rectA now since it was just populated inside check()
                        int x = collider.rectA.x + collider.rectA.width / 2;
                        int y = collider.rectA.y + collider.rectA.height / 2;

                        if (collider.entity instanceof StructureDamage) {
                            var structureDamage = (StructureDamage) collider.entity;
                            structureDamage.applyDamage(this, x, y);
                            switch (structureDamage.structure.structureType) {
                                case BACTERIA_A: Main.game.audioManager.playSound(AudioManager.Sounds.squelch, .5f); break;
                                case HOUSE_A: Main.game.audioManager.playSound(AudioManager.Sounds.impact, .5f);break;
                                default: Main.game.audioManager.playSound(AudioManager.Sounds.structureDamage, .5f);break;
                            }
//                            Main.playSound(AudioManager.Sounds.structureDamage);
                            attackSuccess = true;
                        }
                     }
                }
            } break;
            case KNOCK_OUT: {
                if (hitInvincibility <= 0) {
                    setState(State.NORMAL);
                }
            }
        }

        // variable jump timing, based on how long the button is held
        if (jumpHoldTimer > 0) {
            // maintain full jump impulse while button is held and timer not expired
            if (!jumpHeld) {
                jumpHoldTimer = 0;
                mover.speed.y = 0;
            } else {
                mover.speed.y = mover.jumpImpulse;
            }

            jumpHoldTimer -= dt;
            if (jumpHoldTimer < 0) {
                jumpHoldTimer = 0;
            }
        }

        // climbing movement
        if (climbing) {
            var canClimb = collider.check(offset.set(0, 0), Collider.Type.climbable);
            if (!canClimb) {
                climbing = false;
                mover.gravity = mover.gravityDefault;
                animator.rotation = 0;
            }

            if (canClimb && (climbUpHeld || climbDownHeld)) {
                if (climbUpHeld) {
                    mover.speed.y = mover.climbSpeed;
                }
                if (climbDownHeld) {
                    mover.speed.y = -2 * mover.climbSpeed;
                }
            } else {
                mover.stopY();
            }
        }

        // TODO(brian): other state-independent updates
        //   - invincibility timer
        //   - 'hurt' check
        //   - ???

        var facing = (int) Calc.sign(mover.speed.x);
        if (facing != 0) {
            animator.facing = facing;
        }

        // update components
        mover.update(dt);

        setInvincibilityColor();

        animator.update(dt);

        updateFollowers(dt);

        // update data for next frame
        wasOnGround = mover.isOnGround();
    }

    private void setInvincibilityColor() {
        animator.tint = hitInvincibility > 0 ? Color.RED : defaultColor;
    }

    private void setState(State newState) {
        switch (newState) {
            case ATTACK: {
                if (this.state == State.NORMAL) {
                    this.state = newState;
                    attackTimer = animator.play(creatureType, Anims.State.ATTACK) / 3;
//                    Main.playSound(AudioManager.Sounds.ratAttack);
                    Main.playSound(AudioManager.Sounds.swipe);
                    attackCollider = getAttackCollider();
                    attackSuccess = false;
                }
            } break;
            case NORMAL: {
                this.state = newState;
                animator.play(creatureType, Anims.State.IDLE);
                if (attackCollider != null) {
                    Main.game.entityData.remove(attackCollider, Collider.class);
                    attackCollider = null;
                }
            } break;
            case KNOCK_OUT: {
                this.state = newState;
            }
        }
    }

    private Collider getAttackCollider() {
        int x, y, width, height;

        switch (creatureType) {
            case PHAGE:
                y = -20;
                x = -10;
                width = height = 20;
                break;
            case PARASITE:
                y = 48;
                x = animator.facing == -1 ? -60 :25;
                width = height = 40;
                break;
            default:
                x = animator.facing == -1 ? -30 : 10;
                y = 0;
                width = height = 20;
                break;
        }
        return Collider.makeRect(this, Collider.Type.player, x, y, width, height);
    }

    public void render(SpriteBatch batch) {
        for (var follower : followers) {
            follower.render(batch);
        }
        animator.render(batch);
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        collider.render(shapes);

        if (attackCollider != null) {
            attackCollider.render(shapes);
        }

        position.renderDebug(shapes);

        for (var segment : followers) {
            segment.renderDebug(batch, shapes);
        }
    }

    public void pickup(Follower follower) {
        scene.detachedFollowers.removeValue(follower, true);
        followers.add(follower);
        follower.attached = true;

        var effect = particleManager.effects.get(ParticleEffectType.HEART);
        effect.spawn(new HeartEffect.Params(false, position.x(), position.y()));
        Main.game.audioManager.playSound(AudioManager.Sounds.collectFollower, .3f);
    }

    public void detach(Follower follower) {
        follower.attached = false;
        follower.pickupDelay = 1f;
        followers.removeValue(follower, true);
        scene.detachedFollowers.add(follower);

        var effect = particleManager.effects.get(ParticleEffectType.HEART);
        effect.spawn(new HeartEffect.Params(true, position.x(), position.y()));
        if(detachSoundCountdownTimer < 0) {
            Main.game.audioManager.playSound(AudioManager.Sounds.boing);
            detachSoundCountdownTimer = 1f;
        }

    }

    private float hitInvincibility = 0;
    public void hit(Vector2 speed, int power) {
        mover.speed.set(200 * Calc.sign(speed.x), 200);
        if (!launchFollowers(power)) {
            setState(State.KNOCK_OUT);
        }
        hitInvincibility = 5f;
    }

    public boolean isInvicible() {
        return hitInvincibility > 0 || state == State.KNOCK_OUT;
    }

    public boolean launchFollowers(int count) {
        for (int i = followers.size - 1; i >= 0; i--) {
            if (count-- == 0) { return true; }
            var follower = followers.get(i);
            detach(follower);
            follower.launch();
        }
        return false;
    }

    public void explodeFollowers() {
        for (int i = followers.size - 1; i >= 0; i--) {
            var follower = followers.get(i);
            detach(follower);
            follower.launch();
        }

        var effect = particleManager.effects.get(ParticleEffectType.HEART);
        effect.spawn(new HeartEffect.Params(true, position.x(), position.y()));
        Main.game.audioManager.playSound(AudioManager.Sounds.boing);
    }

    public void successfulHitEffect(float targetX, float targetY) {
        switch(creatureType) {
            case PHAGE:
                particleManager.effects.get(ParticleEffectType.SCRATCH).spawn(new ScratchEffect.Params(targetX, targetY));
                particleManager.effects.get(ParticleEffectType.BLOOD).spawn(new BloodEffect.Params(targetX, targetY));
                break;
            case PARASITE:
                particleManager.effects.get(ParticleEffectType.BITE).spawn(new BiteEffect.Params(targetX, targetY));
                particleManager.effects.get(ParticleEffectType.BLOOD).spawn(new BloodEffect.Params(targetX, targetY));
                break;
            case WORM:
                particleManager.effects.get(ParticleEffectType.SCRATCH).spawn(new ScratchEffect.Params(targetX, targetY));
                particleManager.effects.get(ParticleEffectType.DIRT).spawn(new DirtEffect.Params(targetX, targetY));
                break;
            case ANT:
                particleManager.effects.get(ParticleEffectType.BITE).spawn(new BiteEffect.Params(targetX, targetY));
                particleManager.effects.get(ParticleEffectType.DIRT).spawn(new DirtEffect.Params(targetX, targetY));
                break;
            case RAT:
                particleManager.effects.get(ParticleEffectType.SCRATCH).spawn(new ScratchEffect.Params(targetX, targetY));
                particleManager.effects.get(ParticleEffectType.FLAME).spawn(new DirtEffect.Params(targetX, targetY));
                break;
            case SNAKE:
                particleManager.effects.get(ParticleEffectType.BITE).spawn(new BiteEffect.Params(targetX, targetY));
                particleManager.effects.get(ParticleEffectType.FLAME).spawn(new DirtEffect.Params(targetX, targetY));
                break;
            case MARIO:
                particleManager.effects.get(ParticleEffectType.ASUKA).spawn(new AsukaEffect.Params(targetX, targetY));
                break;
            case LUIGI:
                particleManager.effects.get(ParticleEffectType.ASUKA).spawn(new AsukaEffect.Params(targetX, targetY));
                break;
            default:
                break;
        }
    }

    public void successfulDestroyEffect(float targetX, float targetY) {
        // TODO: differentiate effect per character (scene)
        switch (scene.type) {
            case MICROBIOME:
                particleManager.effects.get(ParticleEffectType.BLOOD_SPLAT).spawn(new BloodSplatEffect.Params(targetX, targetY));
                break;
            case NEIGHBORHOOD:
            case CITY:
                particleManager.effects.get(ParticleEffectType.FLAME).spawn(new FlameEffect.Params(targetX, targetY));
                break;
            default:
                break;
        }
        particleManager.effects.get(ParticleEffectType.BLOOD_FOUNTAIN).spawn(new BloodFountainEffect.Params(targetX, targetY));
    }
    public String debugString() {
        return Stringf.format("Player: pos(%.1f, %.1f) spd(%.1f, %.1f)", position.x(), position.y(), mover.speed.x, mover.speed.y);
    }

    private void updateFollowers(float dt) {
        // update position queue only when moving...
        if (mover.speed.x != 0 || mover.speed.y != 0) {
            // ... and when the current position is far enough away from the last one on either axis
            var pos = Utils.obtainGridPoint2(position);
            var lastSavedPos = positionHistoryQueue.first();
            var isFarEnoughX = (pos.x - lastSavedPos.x) >= chaseFollowerDistance;
            var isFarEnoughY = (pos.y - lastSavedPos.y) >= chaseFollowerDistance;
            var isFarEnoughAwayDirect = pos.dst(lastSavedPos) >= chaseFollowerDistance;
            var isFarEnoughAway = isFarEnoughX || isFarEnoughY || isFarEnoughAwayDirect;
            if (isFarEnoughAway) {
                // insert new position
                positionHistoryQueue.addFirst(pos);

                // if too many positions, pop off last one and return it back to the pool
                if (positionHistoryQueue.size > maxNumFollowersSwarm) {
                    var gridPoint = positionHistoryQueue.removeLast();
                    Utils.gridPoint2Pool.free(gridPoint);
                }
            } else {
                Utils.gridPoint2Pool.free(pos);
            }
        }

        // update attached follower positions
        for (int i = 0; i < followers.size; i++) {
            var follower = followers.get(i);
            follower.update(dt);

            // update follower position from history
            if (follower.attached) {
                var pos = positionHistoryQueue.get(i);
                follower.followTarget.set(pos);
            }
        }
    }
}
