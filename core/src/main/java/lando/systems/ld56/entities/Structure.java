package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.entities.components.StructureDamage;
import lando.systems.ld56.entities.components.XRayRender;
import lando.systems.ld56.utils.RectangleI;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Structure extends Entity implements XRayable {

    public Texture internals;
    public Texture externals;
    public StructureDamage structureDamage;
    public Rectangle bounds;
    public XRayRender xRayRender;

    public Structure(Assets assets, int centerX, int bottomY, OrthographicCamera worldCamera) {
        this(assets, centerX, bottomY, 8, 3, worldCamera);
    }

    public Structure(Assets assets, int centerX, int bottomY, int rows, int columns, OrthographicCamera worldCamera) {
        this.internals = assets.buildingXrayTexture;
        this.externals = assets.buildingCoveredTexture;

        var scale = 2;
        var width = scale * internals.getWidth();
        var height = scale * internals.getHeight();
        this.bounds = new Rectangle(centerX - width / 2, bottomY, width, height);
        this.structureDamage = new StructureDamage(this, rows, columns);
        xRayRender = new XRayRender(this, externals, internals, bounds, worldCamera);
    }

    public void damage(Player player, int x, int y) {
        structureDamage.applyDamage(player, x, y);
    }

    public void render(SpriteBatch batch) {
        xRayRender.render(batch);
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
//        this.structureDamage.renderDebug(batch, shapes);
    }

    @Override
    public void renderMask(SpriteBatch batch) {
        structureDamage.renderMask(batch);
    }

    public void renderFrameBuffers(SpriteBatch batch) {
        xRayRender.renderXrayAlpha(batch);
    }
}
