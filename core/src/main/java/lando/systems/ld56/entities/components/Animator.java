package lando.systems.ld56.entities.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld56.entities.Entity;
import lando.systems.ld56.utils.Calc;

public class Animator extends Component {

    public Position position;
    public Animation<TextureRegion> animation;
    public TextureRegion keyframe;
    public Vector2 origin;
    public Vector2 scale;
    public Color tint;
    public float rotation;
    public float stateTime;
    public int facing = 1;
    public Vector2 defaultScale = new Vector2(1, 1);

    public Animator(Entity entity, Position position, Animation<TextureRegion> animation) {
        this(entity, position, animation.getKeyFrame(0));
        this.animation = animation;
    }

    public Animator(Entity entity, Position position, TextureRegion keyframe) {
        super(entity, Animator.class);
        this.position = position;
        this.animation = null;
        this.keyframe = keyframe;
        this.origin = new Vector2(keyframe.getRegionWidth() / 2f, 0);
        this.scale = new Vector2(1, 1);
        this.tint = Color.WHITE.cpy();
        this.rotation = 0;
        this.stateTime = 0;
    }

    public void update(float dt) {
        if (animation == null) {
            return;
        }

        stateTime += dt;
        keyframe = animation.getKeyFrame(stateTime);

        var sx = Calc.approach(Calc.abs(scale.x), defaultScale.x, 4 * dt);
        var sy = Calc.approach(Calc.abs(scale.y), defaultScale.y, 4 * dt);
        scale.set(facing * sx, sy);
    }

    public void render(SpriteBatch batch) {
        batch.setColor(tint);
        batch.draw(keyframe,
            position.x() - origin.x,
            position.y() - origin.y,
            origin.x, origin.y,
            keyframe.getRegionWidth(),
            keyframe.getRegionHeight(),
            scale.x, scale.y,
            rotation
        );
        batch.setColor(Color.WHITE);
    }
}
