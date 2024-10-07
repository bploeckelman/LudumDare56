package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.entities.components.Animator;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Mover;
import lando.systems.ld56.entities.components.Position;
import lando.systems.ld56.utils.Utils;
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

    // swarm behavior constants
    private static final float SEPARATION_RADIUS = 100;
    private static final float ALIGNMENT_RADIUS = 150;
    private static final float COHESION_RADIUS = 200;
    private static final float MAX_FORCE = 50;
    private static final float MAX_SPEED = 100;

    public Follower(Player player, int x, int y, float scale, int speedX, int speedY) {
        // TEMP - for testing
        var animation = Anims.get(Anims.Type.KITTEN_IDLE);

        this.player = player;
        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, animation);
        this.collider = Collider.makeRect(this, Collider.Type.follower, (int) (scale * -8), (int) (scale * 1), (int) (scale * 16), (int) (scale * 12));
        this.mover = new Mover(this, position, collider);
        this.followTarget = new GridPoint2(x, y);

        animator.defaultScale.set(scale, scale);
        animator.scale.set(scale, scale);
        mover.speed.set(speedX, speedY);
    }

    public void update(float dt) {
        if (!attached) {
            pickupDelay -= dt;
            if (pickupDelay <= 0) {
                pickupDelay = 0f;
            }
        }

        if (attached) {
            if (player.creatureType.mode == Player.Mode.SWARM) {
                // when swarming, use 'boids' constraints to simulate flocking behavior: https://en.wikipedia.org/wiki/Boids
                mover.speed.add(separation());
                mover.speed.add(alignment());
                mover.speed.add(cohesion());

                // constrain to max speed
                mover.speed.limit(MAX_SPEED);

                // directly apply movement speed to position (since mover.update() is only for detached movement)
                position.add(mover.speed.x * dt, mover.speed.y * dt);
            } else if (player.creatureType.mode == Player.Mode.CHASE) {
                // when chasing, always lerp towards the followTarget
                var x = Interpolation.linear.apply(position.x(), followTarget.x, dt * 4);
                var y = Interpolation.linear.apply(position.y(), followTarget.y, dt * 4);
                position.set(x, y);
            }
        } else {
            // when detached, use the mover's speed, gravity, friction, collision detection, etc...
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
        launch(true);
    }

    public void launch(boolean fromPlayer) {
        detach();

        var x = fromPlayer ? (int) player.position.x() : (int)position.x();
        var y = fromPlayer ? (int) player.position.y() : (int)position.y();
        var angle = MathUtils.random(50, 130);
        var speed = MathUtils.random(400, 1000);
        var speedX = (int) (MathUtils.cosDeg(angle) * speed);
        var speedY = (int) (MathUtils.sinDeg(angle) * speed);

        animator.scale.set(2 * animator.defaultScale.x, 2 * animator.defaultScale.y);
        position.set(x, y);
        mover.position.set(x, y);
        mover.speed.set(speedX, speedY);
    }

    // avoid crowding other followers
    private final Vector2 separationForce = new Vector2();
    private Vector2 separation() {
        separationForce.setZero();

        int count = 0;
        for (var other : player.followers) {
            if (other == this) continue;

            float dist = position.value.dst(other.position.value);
            if (dist > 0 && dist < SEPARATION_RADIUS) {
                var diff = Utils.vector2Pool.obtain();
                diff.set(position.value).sub(other.position.value)
                    .nor().scl(1f / dist);
                separationForce.add(diff);
                Utils.vector2Pool.free(diff);
                count++;
            }
        }

        if (count > 0) {
            separationForce.scl(1f / count);
        }

        if (separationForce.len() > 0) {
            separationForce.nor().scl(MAX_FORCE);
            separationForce.sub(mover.speed);
        }

        return separationForce;
    }

    // move towards average speed of nearby followers
    private final Vector2 alignmentForce = new Vector2();
    private Vector2 alignment() {
        var sum = Utils.vector2Pool.obtain().setZero();

        int count = 0;
        for (var other : player.followers) {
            if (other == this) continue;

            float dist = position.value.dst(other.position.value);
            if (dist > 0 && dist < ALIGNMENT_RADIUS) {
                sum.add(other.mover.speed);
                count++;
            }
        }

        if (count > 0) {
            sum.scl(1f / count); // avg vel
            sum.nor().scl(MAX_FORCE);
            alignmentForce.set(sum).sub(mover.speed).limit(MAX_FORCE);
        }

        Utils.vector2Pool.free(sum);
        return alignmentForce;
    }

    private final Vector2 cohesionForce = new Vector2();
    private Vector2 cohesion() {
        var sum = Utils.vector2Pool.obtain().setZero();
        // start with player's pos
        sum.set(player.position.value);

        // count the player as one
        int count = 1;
        for (var other : player.followers) {
            if (other == this) continue;

            float dist = position.value.dst(other.position.value);
            if (dist > 0 && dist < COHESION_RADIUS) {
                sum.add(other.position.value);
                count++;
            }
        }

        if (count > 0) {
            sum.scl(1f / count); // avg vel
            cohesionForce.set(seek(sum));
        }

        Utils.vector2Pool.free(sum);
        return cohesionForce;
    }

    private final Vector2 seek = new Vector2();
    private Vector2 seek(Vector2 target) {
        var desired = Utils.vector2Pool.obtain();
        desired.set(target).sub(player.position.value);
        desired.nor().scl(MAX_SPEED);
        seek.set(desired).sub(mover.speed).limit(MAX_SPEED);
        Utils.vector2Pool.free(desired);
        return seek;
    }
}
