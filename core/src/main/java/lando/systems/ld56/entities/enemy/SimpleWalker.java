package lando.systems.ld56.entities.enemy;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.entities.Follower;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.scene.Scene;

public class SimpleWalker extends Enemy {

    private float speed = 0;
    private float randomSwitch = 0;

    private final Scene scene;

    public SimpleWalker(Scene scene, Anims.Type animType) {
        super(animType);

        this.scene = scene;
    }

    public void moveX(float speed) {
        this.speed = speed;
        mover.friction = 0;
        mover.speed.x = speed;

        animator.facing = speed > 0 ? -1 : 1;

        randomSwitch = MathUtils.random(1f, 2.5f);
    }

    public void switchDirection() {
        moveX(-speed);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        var playerCollider = collider.check(Collider.Type.player);
        if (playerCollider != null) {
            if (playerCollider.entity instanceof Player) {
                ((Player) playerCollider.entity).mover.speed.set(200, 200);
                switchDirection();
            }
        }

        var followerCollider = collider.check(Collider.Type.follower);
        if (followerCollider != null) {
            if (followerCollider.entity instanceof Follower) {
                var follower = (Follower) followerCollider.entity;
                follower.detach();
                follower.launch(false);
            }
        }

        if (isMovingAwayFromPlayer()) {
            randomSwitch -= dt;
        }

        if (mover.speed.x == 0 || randomSwitch < 0) {
            switchDirection();
        }
    }

    private boolean isMovingAwayFromPlayer() {
        float xDif = position.x() - scene.player.position.x();
        return (mover.speed.x < 0) ? xDif < 0 : xDif > 0;
    }
}
