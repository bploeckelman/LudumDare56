package lando.systems.ld56.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.entities.components.*;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.particles.effects.DirtEffect;
import lando.systems.ld56.particles.effects.ParticleEffectType;
import lando.systems.ld56.utils.Calc;
import space.earlygrey.shapedrawer.ShapeDrawer;
import text.formic.Stringf;

public class Player extends Entity {

    public enum State { NORMAL, ATTACK }
    public enum Mode { SWARM, SNAKE }
    public enum CreatureType {
        // Microbiome
          PHAGE(Mode.SWARM)
        , PARASITE(Mode.SNAKE)
        // Neighborhood
        , WORM(Mode.SWARM)
        , ANT(Mode.SNAKE)
        // City
        , RAT(Mode.SWARM)
        , SNAKE(Mode.SNAKE)
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

    public Position position;
    public Animator animator;
    public Collider collider;
    public Mover mover;

    private State state = State.NORMAL;
    private final CreatureType creatureType;
    private final ParticleManager particleManager;

    private boolean wasOnGround = false;
    private boolean climbing = false;
    private float jumpHoldTimer = 0;
    private float attackTimer = 0;
    public float attackStrength = 0.4f;
    private boolean attackSuccess = false;
    private Collider attackCollider;

    private final float jumpHoldDuration = 0.15f;
    private final GridPoint2 offset = new GridPoint2(0, 0);
    private final Array<PlayerSegment> segments = new Array<>();

    public Player(CreatureType creatureType, float x, float y, ParticleManager particleManager) {
        this.creatureType = creatureType;
        this.particleManager = particleManager;

        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, Anims.get(creatureType, Anims.State.IDLE));
        this.collider = Collider.makeRect(this, Collider.Type.player, -10, 0, 24, 20);
        this.mover = new Mover(this, position, collider);

        var scale = 2f;
        animator.scale.set(scale, scale);
        animator.defaultScale.set(scale, scale);
        mover.speed.y = this.mover.gravity;
    }

    public void update(float dt, boolean gameEnding) {
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
                        int x = collider.rectA.x + collider.rectA.width / 2;
                        int y = collider.rectA.y + collider.rectA.height / 2;

                        if (collider.entity instanceof StructureDamage) {
                            ((StructureDamage)collider.entity).applyDamage(this, x, y);
                            Main.playSound(AudioManager.Sounds.structureDamage);
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

        // update flags for next frame
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
                    int attackX = animator.facing == -1 ? -30 : 10;
                    attackCollider = Collider.makeRect(this, Collider.Type.player, attackX, 25, 20, 20);
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

    public void render(SpriteBatch batch) {
        animator.render(batch);
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        collider.render(shapes);

        if (attackCollider != null) {
            attackCollider.render(shapes);
        }

        position.renderDebug(shapes);
    }

    public String debugString() {
        return Stringf.format("Player: pos(%.1f, %.1f) spd(%.1f, %.1f)", position.x(), position.y(), mover.speed.x, mover.speed.y);
    }
}
