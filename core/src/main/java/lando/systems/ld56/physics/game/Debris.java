package lando.systems.ld56.physics.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld56.Main;
import lando.systems.ld56.physics.base.Collidable;
import lando.systems.ld56.physics.base.CollisionShape;
import lando.systems.ld56.physics.base.CollisionShapeCircle;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Debris implements Collidable {
    public static float TOTAL_TIME_TO_LIVE = 10;

    TextureRegion region;
    float radius = 10f;
    float COLLISION_MARGIN = .5f;
    Vector2 position;
    Vector2 velocity;
    float friction;
    float mass;
    CollisionShapeCircle collisionShape;
    Rectangle collisionBounds;
    public float ttl = 0;

    public Debris(Vector2 pos, float width, float height, TextureRegion region) {
        ttl = TOTAL_TIME_TO_LIVE;
        this.region = region;
        this.position = new Vector2(pos);
        this.velocity = new Vector2(MathUtils.random(-5f, 5f), 0);
        this.mass = MathUtils.random(9f, 10f);
        this.friction = MathUtils.random(.4f, .5f);
        radius = Math.min(width/2f, height/2f) - 2f;
        this.collisionShape = new CollisionShapeCircle(radius, position.x, position.y);
        this.collisionBounds = new Rectangle(position.x - width/2f - COLLISION_MARGIN, position.y - height/2f - COLLISION_MARGIN, (width+COLLISION_MARGIN *2f) , height+COLLISION_MARGIN*2f);
    }

    @Override
    public void renderDebug(SpriteBatch batch) {
        batch.draw(Main.game.assets.fuzzyCircle, collisionBounds.x, collisionBounds.y, collisionBounds.width, collisionBounds.height);
    }

    @Override
    public float getFriction() {
        return friction;
    }

    @Override
    public float getMass() {
        return mass;
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector2 newVel) {
        setVelocity(newVel.x, newVel.y);
    }

    @Override
    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void setPosition(float x, float y) {
        position.set(x, y);
        collisionShape.center.set(position);
        this.collisionBounds.set(position.x - collisionBounds.width/2f, position.y - collisionBounds.height/2f, collisionBounds.width, collisionBounds.height);

    }

    @Override
    public void setPosition(Vector2 newPos) {
        setPosition(newPos.x, newPos.y);
    }

    @Override
    public Rectangle getCollisionBounds() {
        return collisionBounds;
    }

    @Override
    public CollisionShape getCollisionShape() {
        return collisionShape;
    }

    @Override
    public void collidedWith(Collidable object) {
        if (object instanceof Debris) {
            if (MathUtils.randomBoolean(.01f)){
                ttl = 0;
            }
            collisionBounds.height *= .99f;
            collisionBounds.width *= .99f;
            radius = Math.min(collisionBounds.width/2f, collisionBounds.height/2f) - 2f;

        }
    }

    @Override
    public boolean shouldCollideWith(Collidable object) {
        return true;
    }

    public void render(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        batch.draw(region, collisionBounds.x, collisionBounds.y, collisionBounds.width, collisionBounds.height);
    }

    public void update(float dt) {
        ttl -= dt;
    }

    public boolean shouldRemove() {
        return (ttl < 0 ||
            (velocity.epsilonEquals(0,0) && (ttl + 3 < TOTAL_TIME_TO_LIVE) ));
    }
}
