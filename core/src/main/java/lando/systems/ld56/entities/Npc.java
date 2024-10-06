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

    public Npc(int x, int y) {
        var scale = 2;
        this.position = new Position(this, x, y);
        this.collider = Collider.makeRect(this, Collider.Type.solid, scale * -36, 0, scale * 72, scale * 50);
        this.animator = new Animator(this, position, Anims.get(Anims.Type.ANT_PUNCH));
        this.animator.defaultScale.set(scale, scale);
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
