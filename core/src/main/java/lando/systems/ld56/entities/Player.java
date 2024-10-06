package lando.systems.ld56.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.entities.components.Animator;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Mover;
import lando.systems.ld56.entities.components.Position;
import lando.systems.ld56.utils.Calc;
import space.earlygrey.shapedrawer.ShapeDrawer;
import text.formic.Stringf;

public class Player extends Entity {

    public enum State { NORMAL, ATTACK }

    public Position position;
    public Animator animator;
    public Collider collider;
    public Mover mover;

    private State state = State.NORMAL;
    private boolean wasOnGround = false;
    private boolean climbing = false;
    private float jumpHoldTimer = 0;

    private final float jumpHoldDuration = 0.15f;
    private final GridPoint2 offset = new GridPoint2(0, 0);

    private float attackTimer = 0;

    // the amount of damage this player does
    public float damage = 0.4f;

    public Player(Assets assets, float x, float y) {
        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, Anims.get(Anims.Type.RAT_IDLE));
        this.animator.defaultScale.scl(2);
        this.collider = Collider.makeRect(this, Collider.Type.player, -10, 0, 24, 20);
        this.mover = new Mover(this, position, collider);
        this.mover.speed.y = this.mover.gravity;
    }

    public void update(float dt) {
        // collect input
        var inputMoveDirX = 0;
        if      (Gdx.input.isKeyPressed(Input.Keys.A)) inputMoveDirX = -1;
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) inputMoveDirX =  1;

        var jumpJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        var jumpHeld = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        var climbHeld = Gdx.input.isKeyPressed(Input.Keys.W)
                     || Gdx.input.isKeyPressed(Input.Keys.UP)
                     || Gdx.input.isKeyPressed(Input.Keys.DPAD_UP);

        var attackJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);

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
                        animator.play(Anims.Type.RAT_WALK);
                    } else {
                        animator.play(Anims.Type.RAT_IDLE);
                    }
                } else {
                    if (mover.speed.y < 0) {
                        animator.play(Anims.Type.RAT_FALL);
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
                        animator.play(Anims.Type.RAT_JUMP);
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
                }
                break;
            }
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
            case ATTACK:
                if (this.state == State.NORMAL) {
                    this.state = newState;
                    attackTimer = animator.play(Anims.Type.RAT_BITE);
                    Main.playSound(AudioManager.Sounds.ratAttack);
                }
                break;
            case NORMAL:
                this.state = newState;
                break;
        }
    }

    public void render(SpriteBatch batch) {
        animator.render(batch);
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        collider.render(shapes);
    }

    public String debugString() {
        return Stringf.format("Player: pos(%.1f, %.1f) spd(%.1f, %.1f)", position.x(), position.y(), mover.speed.x, mover.speed.y);
    }
}
