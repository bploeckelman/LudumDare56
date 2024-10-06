package lando.systems.ld56.entities;

import com.badlogic.gdx.Gdx;
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
import lando.systems.ld56.scene.Scene;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Structure extends Entity implements XRayable {

    public Texture internals;
    public Texture externals;
    public ParticleManager particleManager;
    public StructureDamage structureDamage;
    public Rectangle bounds;
    public XRayRender xRayRender;
    public Scene scene;

    public boolean collapsed = false;
    private boolean isCollapsing = false;
    private float collapseTimer = 0;

    public Structure(Assets assets, float x, float y, float width, float height, Scene scene) {
        this(assets, x, y, width, height, 8, 5, scene);
    }

    public Structure(Assets assets, float x, float y, float width, float height, int rows, int columns, Scene scene) {
        this.internals = assets.buildingXrayTexture;
        this.externals = assets.buildingCoveredTexture;
        this.particleManager = scene.particleManager;
        this.scene = scene;

        this.bounds = new Rectangle(x, y, width, height);
        this.structureDamage = new StructureDamage(this, rows, columns);
        xRayRender = new XRayRender(this, externals, internals, bounds, scene.camera);
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



    public void update(float dt) {
        if (isCollapsing) {
            float collapseDuration = 4f;
            collapseTimer += dt;
            bounds.x += MathUtils.sin(collapseTimer * 120) * 5;
            bounds.y -= Interpolation.exp10In.apply(0, bounds.height, collapseTimer / collapseDuration);
            if (collapseTimer >= collapseDuration) {
                isCollapsing = false;
                collapsed = true;
            }
            randomSmoke();
        } else {
            if (structureDamage.getDamagePercent() > .5f) {
                collapse();
            }
        }
    }

    public void collapse() {
        isCollapsing = !isCollapsing;
        scene.levelMap.removeStructure(this);
        collapseTimer = 0;
        bounds.y = 0;
    }

    private void randomSmoke() {
        var particleEffect = particleManager.effects.get(ParticleEffectType.SMOKE);
        particleEffect.spawn(new SmokeEffect.Params(bounds.x + MathUtils.random(bounds.getWidth()), bounds.y, 30));
    }
}
