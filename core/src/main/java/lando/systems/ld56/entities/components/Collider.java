package lando.systems.ld56.entities.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import lando.systems.ld56.Main;
import lando.systems.ld56.entities.Entity;
import lando.systems.ld56.utils.RectangleI;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Collider extends Component {

    public enum Type {solid, player}

    public Type type;
    public RectangleI rect;
    public GridPoint2 origin;

    private final RectangleI rect1 = new RectangleI();
    private final RectangleI rect2 = new RectangleI();

    public Collider(Entity entity, Type type, int x, int y, int width, int height) {
        super(entity);
        this.type = type;
        this.rect = new RectangleI(x, y, width, height);
        this.origin = new GridPoint2(0, 0);
        Main.game.entityData.add(this, Collider.class);
    }

    public void render(ShapeDrawer shapes) {
        var pos1 = Main.game.entityData.get(entity, Position.class);
        int x1 = (pos1 != null) ? (int) pos1.value.x : 0;
        int y1 = (pos1 != null) ? (int) pos1.value.y : 0;
        rect1.set(
            x1 + this.origin.x + this.rect.x,
            y1 + this.origin.y + this.rect.y,
            this.rect.width, this.rect.height);
        shapes.rectangle(rect1.x, rect1.y, rect1.width, rect1.height, Color.YELLOW, 1);
    }

    public boolean check(Type type, GridPoint2 offset) {
        var colliders = Main.game.entityData.getComponents(Collider.class);
        for (var collider : colliders) {
            if (collider == this) continue;
            if (collider.type != type) continue;
            var isOverlapping = overlaps(collider, offset);
            if (isOverlapping) {
                return true;
            }
        }
        return false;
    }

    public boolean overlaps(Collider other, GridPoint2 offset) {
        var pos1 = Main.game.entityData.get(entity, Position.class);
        var pos2 = Main.game.entityData.get(other.entity, Position.class);

        int x1 = (pos1 != null) ? (int) pos1.value.x : 0;
        int y1 = (pos1 != null) ? (int) pos1.value.y : 0;
        int x2 = (pos2 != null) ? (int) pos2.value.x : 0;
        int y2 = (pos2 != null) ? (int) pos2.value.y : 0;

        rect1.set(
            x1 + this.origin.x + this.rect.x + offset.x,
            y1 + this.origin.y + this.rect.y + offset.y,
            this.rect.width, this.rect.height);

        rect2.set(
            x2 + other.origin.x + other.rect.x,
            y2 + other.origin.y + other.rect.y,
            other.rect.width, other.rect.height);

        return rect1.overlaps(rect2);
    }
}
