package lando.systems.ld56.entities.enemy;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.entities.Follower;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.scene.Scene;

public class Truck extends Enemy {

    private Scene scene;

    public Truck(Scene scene, Anims.Type animType) {
        super(animType);

        this.scene = scene;

        collider.rect.height = 60;

        // truck image is backwards
        animator.facing = MathUtils.randomSign();

        int x = animator.facing == -1 ? -100 : (int)this.scene.backgroundRectangle.width + 100;

        position.set(x, 20);

        mover.friction = mover.gravity = 0;
        mover.collider = null;
        mover.speed.x = MathUtils.random(200, 400) * -animator.facing;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        var playerCollider = collider.check(Collider.Type.player);
        if (playerCollider != null) {
            if (playerCollider.entity instanceof Player) {
                scene.player.hit(mover.speed, 20);
            }
        }

        if (mover.speed.x > 0) {
            if (position.x() > scene.backgroundRectangle.width + 100) {
                remove = true;
            }
        } else if (position.x() < -100) {
            remove = true;
        }
    }
}
