package lando.systems.ld56.entities.components;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld56.entities.Entity;
import lando.systems.ld56.utils.Calc;

public class Mover extends Component {

    public Position position;
    public Collider collider;

    public Vector2 speed = new Vector2();

    // constants
    public float friction = 50f;
    public float gravity = -100f;
    public float airAccel = 500f;
    public float groundAccel = 200f;
    public float jumpImpulse = 500f;
    public float maxSpeedX = 200f;
    public float maxFallSpeed = -300f;

    private final Vector2 remainder = new Vector2();

    public Mover(Entity entity, Position position) {
        this(entity, position, null);
    }

    public Mover(Entity entity, Position position, Collider collider) {
        super(entity, Mover.class);
        this.position = position;
        this.collider = collider;
    }

    public void update(float dt) {
        if (friction > 0 && isOnGround()) {
            speed.x = Calc.approach(speed.x, 0, friction * dt);
        }

        if (gravity != 0 && !isOnGround()) {
            speed.y += gravity * dt;
        }

        if (Calc.abs(speed.x) > maxSpeedX) {
            speed.x = Calc.approach(speed.x, Calc.sign(speed.x) * maxSpeedX, dt * 2000);
        }

        if (speed.y < maxFallSpeed) {
            speed.y = Calc.approach(speed.y, maxFallSpeed, dt * 2000);
        }

        // calculate how many pixels to move this frame,
        // tracking remainder between frames
        var moveX = remainder.x + speed.x * dt;
        var moveY = remainder.y + speed.y * dt;
        int intMoveX = (int) moveX;
        int intMoveY = (int) moveY;
        remainder.x = moveX - intMoveX;
        remainder.y = moveY - intMoveY;

        moveX(intMoveX);
        moveY(intMoveY);
    }

    private boolean moveX(int amount) {
        if (collider == null) {
            position.value.x += amount;
        } else {
            // for each pixel, if moving there wouldn't collide then move, otherwise stop
            var sign = Calc.sign(amount);

            while (amount != 0) {
                offset.set(sign, 0);
                var isSolid = collider.check(Collider.Type.solid, offset);
                if (isSolid) {
                    stopX();
                    return true;
                }

                amount -= sign;
                position.value.x += amount;
            }
        }
        return false;
    }

    private boolean moveY(int amount) {
        if (collider == null) {
            position.value.y += amount;
        } else {
            // for each pixel, if moving there wouldn't collide then move, otherwise stop
            var sign = Calc.sign(amount);

            while (amount != 0) {
                offset.set(0, sign);
                var isSolid = collider.check(Collider.Type.solid, offset);
                if (isSolid) {
                    stopY();
                    return true;
                }

                amount -= sign;
                position.value.y += amount;
            }
        }
        return false;
    }

    public void stopX() {
        speed.x = 0;
        remainder.x = 0;
    }

    public void stopY() {
        speed.y = 0;
        remainder.y = 0;
    }

    private final GridPoint2 offset = new GridPoint2();
    public boolean isOnGround() {
        if (collider == null) {
            return false;
        }

        // look one pixel below, if it's solid then we're on the ground
        return collider.check(Collider.Type.solid, offset.set(0, -1));
    }
}
