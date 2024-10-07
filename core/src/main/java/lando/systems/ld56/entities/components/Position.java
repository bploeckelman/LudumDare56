package lando.systems.ld56.entities.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld56.entities.Entity;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Position extends Component {

    public final Vector2 value = new Vector2();

    public Position(Entity entity, float x, float y) {
        super(entity, Position.class);
        this.value.set(x, y);
    }

    public float x() {
        return value.x;
    }

    public float y() {
        return value.y;
    }

    public int ix() {
        return (int) value.x;
    }

    public int iy() {
        return (int) value.y;
    }

    public float dst(Position other) {
        return this.value.dst(other.value);
    }

    public Position x(float x) {
        value.x = x;
        return this;
    }

    public Position y(float y) {
        value.y = y;
        return this;
    }

    public Position add(float x, float y) {
        value.x += x;
        value.y += y;
        return this;
    }

    public Position sub(float x, float y) {
        value.x -= x;
        value.y -= y;
        return this;
    }

    public Position scl(float s) {
        value.x *= s;
        value.y *= s;
        return this;
    }

    public Position set(float x, float y) {
        value.x = x;
        value.y = y;
        return this;
    }

    public Position set(GridPoint2 point) {
        value.x = point.x;
        value.y = point.y;
        return this;
    }

    public void renderDebug(ShapeDrawer shapes) {
        // circle with outline
        shapes.filledCircle(value.x, value.y, 3, Color.MAGENTA);

        shapes.setColor(Color.SKY);
        shapes.circle(value.x, value.y, 3);
        shapes.setColor(Color.WHITE);
    }
}
