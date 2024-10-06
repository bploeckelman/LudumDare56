package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Structures;
import lando.systems.ld56.entities.components.Position;
import lando.systems.ld56.entities.components.XRayRender;

public class TestXRay extends Entity{
    XRayRender xRayRender;
    Position pos;

    public TestXRay (Rectangle bounds, OrthographicCamera worldCamera) {
        xRayRender = new XRayRender(this, Structures.get(Structures.Type.BRICK_FRONT), Structures.get(Structures.Type.BRICK_FRONT), bounds, worldCamera);
        pos = new Position(this, bounds.x, bounds.y);
    }

    public void update(float dt) {
        xRayRender.update(dt);
    }

    public void renderFrameBuffers(SpriteBatch batch) {
        xRayRender.renderXrayAlpha(batch);
    }

    public void render(SpriteBatch batch) {
        xRayRender.render(batch);
    }


}
