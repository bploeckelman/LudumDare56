package lando.systems.ld56.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.entities.components.Animator;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Mover;
import lando.systems.ld56.entities.components.Position;
import lando.systems.ld56.utils.Calc;
import space.earlygrey.shapedrawer.ShapeDrawer;
import text.formic.Stringf;

public class Player extends Entity {

    public Position position;
    public Animator animator;
    public Collider collider;
    public Mover mover;

    public Player(Assets assets, float x, float y) {
        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, assets.animations.guyIdle);
        this.animator.defaultScale.scl(2);
        this.collider = new Collider(this, Collider.Type.player, -4, 0, 12, 24);
        this.collider.origin.set(0, 0);
        this.mover = new Mover(this, position, collider);
        this.mover.speed.y = this.mover.gravity;
    }

    public void update(float dt) {
        var input = 0;
        if      (Gdx.input.isKeyPressed(Input.Keys.A)) input = -1;
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) input =  1;

        if (input != 0) {
            animator.facing = input;
        }

        if (input != 0) {
            var inputSign = Calc.sign(input);
            var speedSign = Calc.sign(mover.speed.x);
            if (speedSign != inputSign) {
                // clear momentum when switching directions
                mover.stopX();
            }
        }

        var accel = mover.isOnGround() ? mover.groundAccel : mover.airAccel;
        mover.speed.x += input * accel * dt;

        mover.update(dt);
        animator.update(dt);
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
