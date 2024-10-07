package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.entities.components.Animator;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Mover;
import lando.systems.ld56.entities.components.Position;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Follower extends Entity {

    public final Player player;

    public Position position;
    public Animator animator;
    public Collider collider;
    public Mover mover;
    public GridPoint2 followTarget;

    public boolean attached;
    private float pickupDelay = 1f;

    public Follower(Player player, int x, int y, float scale, int speedX, int speedY) {
        // TEMP - for testing
        var animation = Anims.get(Anims.Type.KITTEN_IDLE);

        this.player = player;
        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, animation);
        this.collider = Collider.makeRect(this, Collider.Type.follower, (int) (scale * -8), (int) (scale * 1), (int) (scale * 16), (int) (scale * 12));
        this.mover = new Mover(this, position, collider);
        this.followTarget = new GridPoint2(x, y);

        // for testing, start detached
        attached = false;

        animator.defaultScale.set(scale, scale);
        animator.scale.set(2 * scale, 2 * scale);
        mover.speed.set(speedX, speedY);
    }

    public void update(float dt) {
        if (!attached) {
            pickupDelay -= dt;
            if (pickupDelay <= 0) {
                pickupDelay = 0f;
            }
        }

        // positions are set directly by player when attached
        if (attached) {
            var x = Interpolation.linear.apply(position.x(), followTarget.x, dt * 4);
            var y = Interpolation.linear.apply(position.y(), followTarget.y, dt * 4);
            position.set(x, y);
        } else {
            mover.update(dt);
        }

        animator.update(dt);
    }

    public void render(SpriteBatch batch) {
        animator.render(batch);
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        collider.render(shapes);
    }

    public boolean canPickup() {
        return !attached && pickupDelay == 0 && Collider.rectanglesOverlap(player.collider, collider);
    }

    public void detach() {
        attached = false;
        pickupDelay = 1f;
    }

    public void launch() {
        detach();

        var x = (int) player.position.x();
        var y = (int) player.position.y();
        var angle = MathUtils.random(50, 130);
        var speed = MathUtils.random(400, 1000);
        var speedX = (int) (MathUtils.cosDeg(angle) * speed);
        var speedY = (int) (MathUtils.sinDeg(angle) * speed);

        animator.scale.set(2 * animator.defaultScale.x, 2 * animator.defaultScale.y);
        position.set(x, y);
        mover.position.set(x, y);
        mover.speed.set(speedX, speedY);
    }
}
