package lando.systems.ld56.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.entities.components.Animator;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Mover;
import lando.systems.ld56.entities.components.Position;
import lando.systems.ld56.utils.Calc;
import space.earlygrey.shapedrawer.ShapeDrawer;
import text.formic.Stringf;

public class Player extends Entity {

    public enum State { NORMAL }

    public Position position;
    public Animator animator;
    public Collider collider;
    public Mover mover;

    private State state = State.NORMAL;
    private boolean wasOnGround = false;
    private float jumpHoldTimer = 0;

    private final float jumpHoldDuration = 0.2f;

    public Player(Assets assets, float x, float y) {
        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, Anims.get(Anims.Type.RAT_IDLE));
        this.animator.defaultScale.scl(2);
        this.collider = new Collider(this, Collider.Type.player, -4, 0, 12, 24);
        this.collider.origin.set(0, 0);
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

        var inputSign = Calc.sign(inputMoveDirX);
        var speedSign = Calc.sign(mover.speed.x);
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
                    var accel = isOnGround ? mover.groundAccel : mover.airAccel;
                    mover.speed.x += inputMoveDirX * accel * dt;

                    // cap movement speed at a maximum by lerping hard towards the max if we're over it
                    if (Calc.abs(mover.speed.x) > mover.maxGroundSpeedX) {
                        mover.speed.x = Calc.approach(mover.speed.x, Calc.sign(mover.speed.x) * mover.maxGroundSpeedX, dt * 2000);
                    }

                    // apply friction
                    if (inputMoveDirX == 0 && isOnGround) {
                        mover.speed.x  = Calc.approach(mover.speed.x, 0, mover.friction * dt);
                    }

                    // update facing
                    if (inputMoveDirX != 0 && isOnGround) {
                        animator.facing = inputMoveDirX;
                    }
                }

                // vertical movement
                {
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

                    // TODO(brian): trigger a climb
                }

                // TODO(brian): handle attack input... other input handling?
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
