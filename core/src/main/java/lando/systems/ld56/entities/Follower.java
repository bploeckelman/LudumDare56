package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
    public float pickupDelay = 1f;

    // swarm behavior constants
    private static float MAX_SWARM_SPEED = 200f;
    private static float AVOID_FOLLOWERS_WEIGHT = 0.4f;
    private static float CLOSE_TO_PLAYER_WEIGHT = 0.6f;

    public Follower(Player player, int x, int y, float scale, int speedX, int speedY) {
        this.player = player;

        Animation<TextureRegion> animation;
        var creatureType = player.creatureType;
        switch (creatureType.mode) {
            case SWARM: animation = Anims.get(creatureType, Anims.State.IDLE); break;
            // TODO: fetch appropriate anim based on creature type
            case CHASE: {
                if (creatureType == Player.CreatureType.SNAKE) {
                    animation = Anims.get(Anims.Type.BALL_IDLE);
                } else if (creatureType == Player.CreatureType.ANT) {
                    animation = Anims.get(Anims.Type.ANT_IDLE); }
                else if (creatureType == Player.CreatureType.PARASITE) {
                    animation = Anims.get(Anims.Type.SEGMENT_IDLE);
                } else {
                    animation = Anims.get(Anims.Type.KITTEN_IDLE);
                }
            } break;
            default: {
                animation = Anims.get(Anims.Type.KITTEN_IDLE);
            } break;
        }

        var frame = animation.getKeyFrame(0);
        var width = scale * frame.getRegionWidth();
        var height = scale * frame.getRegionHeight();
        var margin = 0.1f * Math.max(width, height);
        var rect = new Rectangle(
            -width / 2f + margin,
            scale + margin,
            width - 2 * margin,
            height - 2 * margin);

        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, animation);
        this.collider = Collider.makeRect(this, Collider.Type.follower, rect.x, rect.y, rect.width, rect.height);
        this.mover = new Mover(this, position, collider);
        this.followTarget = new GridPoint2(x, y);

        mover.speed.set(speedX, speedY);
        animator.defaultScale.set(scale, scale);
        animator.scale.set(scale, scale);
    }

    public void update(float dt) {
        if (!attached) {
            animator.tint.set(Color.WHITE);

            pickupDelay -= dt;
            if (pickupDelay <= 0) {
                pickupDelay = 0f;
            }
        }

        if (attached) {
            if (player.creatureType.mode == Player.Mode.SWARM) {
                var gray = 128 / 255f;
                animator.tint.set(gray, gray, gray, 1);
                // apply constraints to speed
                var outputVector = Utils.vector2Pool.obtain().setZero();
                mover.speed.add(avoidFollowers(outputVector, AVOID_FOLLOWERS_WEIGHT));
                mover.speed.add(closeToPlayer(outputVector, CLOSE_TO_PLAYER_WEIGHT));
                mover.speed.limit(MAX_SWARM_SPEED);
                animator.facing = player.position.x() > position.x() ? 1 : -1;
                // directly apply movement speed to position (since mover.update() is only for detached movement)
                position.add(mover.speed.x * dt, mover.speed.y * dt);
            } else if (player.creatureType.mode == Player.Mode.CHASE) {
                animator.tint.set(Color.WHITE);
                // when chasing, always lerp towards the followTarget
                var x = Interpolation.linear.apply(position.x(), followTarget.x, dt * 10);
                var y = Interpolation.linear.apply(position.y(), followTarget.y, dt * 10);
                animator.facing = player.position.x() > position.x() ? 1 : -1;
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
        batch.setColor(Color.WHITE);
    }

    private final Color debugColor = new Color(1, 1, 0, 0.1f);
    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        collider.render(shapes, debugColor);
    }

    public boolean canPickup() {
        return !attached && pickupDelay == 0 && Collider.rectanglesOverlap(player.collider, collider);
    }

    public void detach() {
        if (!attached) return;
        player.detach(this);
    }

    public void launch() {
        launch(true);
    }

    public void launch(boolean fromPlayer) {
        var x = fromPlayer ? (int) player.position.x() : (int)position.x();
        var y = fromPlayer ? (int) player.position.y() : (int)Math.max(position.y(), 64);
        var angle = MathUtils.random(50, 130);
        var speed = MathUtils.random(400, 1000);
        var speedX = (int) (MathUtils.cosDeg(angle) * speed);
        var speedY = (int) (MathUtils.sinDeg(angle) * speed);

        animator.scale.set(2 * animator.defaultScale.x, 2 * animator.defaultScale.y);
        position.set(x, y);
        mover.position.set(x, y);
        mover.speed.set(speedX, speedY);
    }

    private Vector2 avoidFollowers(Vector2 output, float weight) {
        // rename out param for clarity
        var avoid = output;
        avoid.setZero();

        // add up vectors towards other followers who are too close
        var tooCloseRadius = 20f;
        for (var other : player.followers) {
            if (this == other) continue;
            var dist = this.position.dst(other.position);
            if (dist <= tooCloseRadius) {
                avoid.x += other.position.x() - this.position.x();
                avoid.y += other.position.y() - this.position.y();
            }
        }

        // 'avoid' is now the average direction towards the other followers
        // inverting it turns it into the average direction away from others
        avoid.scl(-1f);

        // apply specified weight
        avoid.scl(weight);
        return avoid;
    }

    private Vector2 closeToPlayer(Vector2 output, float weight) {
        // rename out param for clarity
        var close = output;
        close.setZero();
        var tooFarRadius = 40f;
        var dist = this.position.dst(player.position);
        if (dist >= tooFarRadius) {
            close.x += player.position.x() - this.position.x();
            close.y += player.position.y() - this.position.y();
        }
        close.scl(weight);
        return close;
    }
}
