package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.entities.components.Animator;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Mover;
import lando.systems.ld56.entities.components.Position;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class PlayerSegment extends Entity {

    public final Player player;

    public Position position;
    public Animator animator;
    public Collider collider;
    public Mover mover;

    private float pickupDelay = 1f;

    public PlayerSegment(Player player, int x, int y, float scale, int speedX, int speedY) {
        // TEMP - for testing
        var animation = Anims.get(Anims.Type.KITTEN_IDLE);

        this.player = player;
        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, animation);
        this.collider = Collider.makeRect(this, Collider.Type.player_segment, (int) (scale * -8), (int) (scale * 1), (int) (scale * 16), (int) (scale * 12));
        this.mover = new Mover(this, position, collider);

        animator.defaultScale.set(scale, scale);
        animator.scale.set(2 * scale, 2 * scale);
        mover.speed.set(speedX, speedY);
    }

    public void update(float dt) {
        pickupDelay -= dt;
        if (pickupDelay <= 0) {
            pickupDelay = 0f;
        }

        animator.update(dt);
        mover.update(dt);
    }

    public void render(SpriteBatch batch) {
        animator.render(batch);
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        collider.render(shapes);
    }

    public boolean canPickup() {
        return pickupDelay == 0f;
    }
}
