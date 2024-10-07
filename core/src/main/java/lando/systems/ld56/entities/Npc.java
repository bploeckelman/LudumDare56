package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.entities.components.Animator;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.entities.components.Position;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Npc extends Entity {

    public Position position;
    public Collider collider;
    public Animator animator;

    public Npc(int x, int y, Anims.Type animType) {
        this.position = new Position(this, x, y);
        this.animator = new Animator(this, position, Anims.get(animType));
        int width = animator.keyframe.getRegionWidth();
        this.collider = Collider.makeRect(this, Collider.Type.enemy, -width / 2, 0, width, animator.keyframe.getRegionHeight());
    }

    public void update(float dt) {
        animator.update(dt);
    }

    public void render(SpriteBatch batch) {
        animator.render(batch);
    }

    public void renderDebug(ShapeDrawer shapes) {
        collider.render(shapes);
    }
}
