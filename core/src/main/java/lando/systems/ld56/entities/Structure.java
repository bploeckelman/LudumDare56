package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.entities.components.StructureDamage;
import lando.systems.ld56.entities.components.XRayRender;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.particles.effects.ParticleEffectType;
import lando.systems.ld56.particles.effects.SmokeEffect;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Structure extends Entity implements XRayable {

    public Texture internals;
    public Texture externals;
    public ParticleManager particleManager;
    public StructureDamage structureDamage;
    public Rectangle bounds;
    public XRayRender xRayRender;

    public Structure(Assets assets, ParticleManager particleManager, float x, float y, float width, float height, OrthographicCamera worldCamera) {
        this(assets, particleManager, x, y, width, height, 8, 3, worldCamera);
    }

    public Structure(Assets assets, ParticleManager particleManager, float x, float y, float width, float height, int rows, int columns, OrthographicCamera worldCamera) {
        this.internals = assets.buildingXrayTexture;
        this.externals = assets.buildingCoveredTexture;
        this.particleManager = particleManager;

        this.bounds = new Rectangle(x, y, width, height);
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

    private boolean isCollapsing = false;
    private float collapseTimer = 0;

    public void update(float dt) {
        if (isCollapsing) {
            float collapseDuration = 4f;
            collapseTimer += dt;
            bounds.x += MathUtils.sin(collapseTimer * 120) * 5;
            bounds.y -= Interpolation.exp10In.apply(0, bounds.height, collapseTimer / collapseDuration);
            if (collapseTimer >= collapseDuration) {
                isCollapsing = false;
            }
            randomSmoke();
        }
    }

    public void collapse() {
        isCollapsing = !isCollapsing;

        collapseTimer = 0;
        bounds.y = 0;
    }

    private void randomSmoke() {
        var particleEffect = particleManager.effects.get(ParticleEffectType.SMOKE);
        particleEffect.spawn(new SmokeEffect.Params(bounds.x + MathUtils.random(bounds.getWidth()), bounds.y, 30));
    }
}
