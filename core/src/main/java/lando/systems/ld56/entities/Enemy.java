package lando.systems.ld56.entities;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Mover;

public class Enemy extends Npc {

    public static Enemy createTardigrade(LevelMap levelMap) {
        int start = levelMap.tileSize * 4;
        return new Enemy(start, start, Anims.Type.TARDIGRADE).moveX(200);
    }

    private final Mover mover;
    private float speed;

    public Enemy(int x, int y, Anims.Type animType) {
        super(x, y, animType);
        mover = new Mover(this, this.position, this.collider);
    }

    public Enemy moveX(float speed) {
        mover.friction = 0;

        this.speed = speed;
        mover.speed.x = speed;

        animator.facing = speed > 0 ? -1 : 1;

        return this;
    }

    public void switchDirection() {
        moveX(-speed);
    }

     @Override
    public void update(float dt) {
        super.update(dt);
        mover.update(dt);

        var playerCollider = collider.check(Collider.Type.player);
        if (playerCollider != null) {
            if (playerCollider.entity instanceof Player) {
                ((Player)playerCollider.entity).mover.speed.set(200, 200);
                switchDirection();
            }
        }

        var follower = collider.check(Collider.Type.follower);
        if (follower != null) {
            if (follower.entity instanceof Follower) {
                Follower f = (Follower)follower.entity;
                f.launch(false);
                moveX(-speed);
            }
        }

        if (mover.speed.x == 0) {
            switchDirection();
        }
    }
}
