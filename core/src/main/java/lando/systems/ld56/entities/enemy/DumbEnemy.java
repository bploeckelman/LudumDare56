package lando.systems.ld56.entities.enemy;

import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.entities.Follower;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.components.Collider;

public class DumbEnemy extends Enemy {

    private float speed = 0;

    public DumbEnemy(Anims.Type animType) {
        super(animType);
    }

    public void moveX(float speed) {
        this.speed = speed;
        mover.friction = 0;
        mover.speed.x = speed;

        animator.facing = speed > 0 ? -1 : 1;
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

        if (mover.speed.x == 0) {
            switchDirection();
        }
    }
}
