package lando.systems.ld56.entities.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.entities.Entity;
import lando.systems.ld56.entities.components.Animator;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Mover;
import lando.systems.ld56.entities.components.Position;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Enemy extends Entity {
    public Position position;
    public Collider collider;
    public Animator animator;
    public Mover mover;

    public Enemy(Anims.Type animType) {
        this(animType, -1000, -1000);
    }

    public Enemy(Anims.Type animType, int x, int y) {
        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, Anims.get(animType));
        int width = animator.keyframe.getRegionWidth();
        this.collider = Collider.makeRect(this, Collider.Type.enemy, -width / 2, 0, width, animator.keyframe.getRegionHeight());
        this.mover = new Mover(this, this.position, this.collider);
    }

    public void setPosition(int x, int y) {
        this.position.set(x, y);
    }

    public void update(float dt) {
        animator.update(dt);
        mover.update(dt);
    }

    public void render(SpriteBatch batch) {
        animator.render(batch);
    }

    public void renderDebug(ShapeDrawer shapes) {
        collider.render(shapes);
    }
}
