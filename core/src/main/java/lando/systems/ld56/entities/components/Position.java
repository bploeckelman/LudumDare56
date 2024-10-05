package lando.systems.ld56.entities.components;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld56.entities.Entity;

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
}
