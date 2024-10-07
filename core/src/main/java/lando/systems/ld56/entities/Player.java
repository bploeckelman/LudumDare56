package lando.systems.ld56.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
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
import lando.systems.ld56.particles.effects.AsukaEffect;
import lando.systems.ld56.particles.effects.BiteEffect;
import lando.systems.ld56.particles.effects.BloodEffect;
import lando.systems.ld56.particles.effects.BloodFountainEffect;
import lando.systems.ld56.particles.effects.DirtEffect;
import lando.systems.ld56.particles.effects.FlameEffect;
import lando.systems.ld56.particles.effects.HeartEffect;
import lando.systems.ld56.particles.effects.ParticleEffectType;
import lando.systems.ld56.particles.effects.ScratchEffect;
import lando.systems.ld56.scene.Scene;
import lando.systems.ld56.utils.Calc;
import lando.systems.ld56.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;
import text.formic.Stringf;

public class Player extends Entity {

    public enum State { NORMAL, ATTACK }
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

    private final Scene scene;

    public Position position;
    public Animator animator;
    public Collider collider;
    public Mover mover;

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

    private final float jumpHoldDuration = 0.15f;
    private final GridPoint2 offset = new GridPoint2(0, 0);

    private final int maxNumFollowers = 10;
    private final int followerDistance = 40; // TODO: should be related to the segment's size?
    public final Array<Follower> followers = new Array<>();
    public final Queue<GridPoint2> positionHistoryQueue = new Queue<>();

    public Player(Scene scene, CreatureType creatureType, float x, float y, ParticleManager particleManager) {
        this.scene = scene;
        this.creatureType = creatureType;
        this.particleManager = particleManager;

        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, Anims.get(creatureType, Anims.State.IDLE));
        this.collider = Collider.makeRect(this, Collider.Type.player, -10, 0, 24, 20);
        this.mover = new Mover(this, position, collider);

        var scale = 2f;
        animator.scale.set(scale, scale);
        animator.defaultScale.set(scale, scale);
        mover.speed.y = mover.gravity;

        for (int i = 0; i < maxNumFollowers; i++) {
            var pos = Utils.obtainGridPoint2(position);
            var nudgeX = MathUtils.random(-20, 20);
            var nudgeY = MathUtils.random(-20, 20);
            pos.add(nudgeX, nudgeY);
            positionHistoryQueue.addFirst(pos);

            var follower = new Follower(this, pos.x, pos.y, scale, 0, 0);
            follower.attached = true;
            followers.add(follower);
        }
    }

    public void update(float dt, boolean gameEnding) {
        accum += dt;
        // collect input
        var inputMoveDirX = 0;
        if      (Gdx.input.isKeyPressed(Input.Keys.A)) inputMoveDirX = -1;
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) inputMoveDirX =  1;

        var attackJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
        var jumpJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        var jumpHeld = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        var climbHeld = Gdx.input.isKeyPressed(Input.Keys.W)
                     || Gdx.input.isKeyPressed(Input.Keys.UP)
                     || Gdx.input.isKeyPressed(Input.Keys.DPAD_UP);

        if (gameEnding) {
            inputMoveDirX = 0;
            jumpJustPressed = false;
            jumpHeld = false;
            climbHeld = false;
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
                    // apply acceleration based on input
                    if (!climbing) {
                        var accel = (isOnGround && !climbing) ? mover.groundAccel : mover.airAccel;
                        mover.speed.x += inputMoveDirX * accel * dt;
                    } else {
                        mover.stopX();
                    }

                    // cap movement speed at a maximum by lerping hard towards the max if we're over it
                    if (Calc.abs(mover.speed.x) > mover.maxGroundSpeedX) {
                        mover.speed.x = Calc.approach(mover.speed.x, Calc.sign(mover.speed.x) * mover.maxGroundSpeedX, dt * 2000);
                    }

                    // apply friction
                    if (inputMoveDirX == 0 && isOnGround && !climbing) {
                        mover.speed.x  = Calc.approach(mover.speed.x, 0, mover.friction * dt);
                    }

                    // update facing
                    if (inputMoveDirX != 0 && isOnGround) {
                        animator.facing = inputMoveDirX;
                    }
                }

                // vertical movement
                {
                    // stop climbing
                    if (climbing && jumpJustPressed) {
                        climbing = false;
                        mover.gravity = mover.gravityDefault;
                    }

                    // trigger a jump
                    if (jumpJustPressed && isOnGround) {
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
                    var canClimb = collider.check(offset.set(-1, 0), Collider.Type.climbable)
                                || collider.check(offset.set(+1, 0), Collider.Type.climbable);
                    if (climbHeld && canClimb) {
                        climbing = true;
                        mover.gravity = 0;
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
                                case BACTERIA_A: Main.game.audioManager.playSound(AudioManager.Sounds.squelch); break;
                                case HOUSE_A: Main.game.audioManager.playSound(AudioManager.Sounds.impact);break;
                                default: Main.game.audioManager.playSound(AudioManager.Sounds.structureDamage);break;
                            }
//                            Main.playSound(AudioManager.Sounds.structureDamage);
                            attackSuccess = true;
                        }
                     }
                }
            } break;
        }

        // variable jump timing, based on how long the button is held
        if (jumpHoldTimer > 0) {
            jumpHoldTimer -= dt;
            if (jumpHoldTimer < 0) {
                jumpHoldTimer = 0;
            }

            // maintain full jump impulse while button is held and timer not expired
            mover.speed.y = mover.jumpImpulse;
            if (!jumpHeld) {
                jumpHoldTimer = 0;
            }
        }

        // climbing movement
        if (climbing) {
            // TODO(brian): check for reached top
            if (climbHeld) {
                mover.speed.y = mover.climbSpeed;
            } else {
                mover.stopY();
            }
        }

        // TODO(brian): other state-independent updates
        //   - invincibility timer
        //   - 'hurt' check
        //   - ???

        // update components
        mover.update(dt);
        animator.update(dt);

        updateFollowers(dt);

        // update data for next frame
        wasOnGround = mover.isOnGround();
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
        animator.render(batch);
        for (var segment : followers) {
            segment.render(batch);
        }
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

    public void pickup(Follower segment) {
        scene.detachedFollowers.removeValue(segment, true);
        followers.add(segment);
        segment.attached = true;

        var effect = particleManager.effects.get(ParticleEffectType.HEART);
        effect.spawn(new HeartEffect.Params(false, position.x(), position.y()));
        Main.game.audioManager.playSound(AudioManager.Sounds.collectFollower);
    }

    public void launchFollower() {
        if (followers.isEmpty()) return;

        var lastIndex = followers.size - 1;
        var follower = followers.get(lastIndex);

        followers.removeIndex(lastIndex);
        scene.detachedFollowers.add(follower);

        follower.launch();

        var effect = particleManager.effects.get(ParticleEffectType.HEART);
        effect.spawn(new HeartEffect.Params(true, position.x(), position.y()));
        Main.game.audioManager.playSound(AudioManager.Sounds.boing);
    }

    public void explodeFollowers() {
        var detachedFollowers = scene.detachedFollowers;
        for (int i = followers.size - 1; i >= 0; i--) {
            var follower = followers.get(i);
            followers.removeIndex(i);
            detachedFollowers.add(follower);

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
                particleManager.effects.get(ParticleEffectType.FLAME).spawn(new FlameEffect.Params(targetX, targetY));
                break;
            case SNAKE:
                particleManager.effects.get(ParticleEffectType.BITE).spawn(new BiteEffect.Params(targetX, targetY));
                particleManager.effects.get(ParticleEffectType.FLAME).spawn(new FlameEffect.Params(targetX, targetY));
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
            var isFarEnoughX = (pos.x - lastSavedPos.x) >= followerDistance;
            var isFarEnoughY = (pos.y - lastSavedPos.y) >= followerDistance;
            var isFarEnoughAwayDirect = pos.dst(lastSavedPos) >= followerDistance;
            var isFarEnoughAway = isFarEnoughX || isFarEnoughY || isFarEnoughAwayDirect;
            if (isFarEnoughAway) {
                // insert new position
                positionHistoryQueue.addFirst(pos);

                // if too many positions, pop off last one and return it back to the pool
                if (positionHistoryQueue.size > maxNumFollowers) {
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
