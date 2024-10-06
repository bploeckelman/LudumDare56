package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Structures;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.entities.components.StructureDamage;
import lando.systems.ld56.entities.components.XRayRender;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.particles.effects.ParticleEffectType;
import lando.systems.ld56.particles.effects.SmokeEffect;
import lando.systems.ld56.scene.Scene;
import lando.systems.ld56.utils.RectangleI;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Structure extends Entity implements XRayable {

    public final Scene scene;
    public static float collapseDuration = 2f;

    public Texture internals;
    public Texture externals;
    public ParticleManager particleManager;
    public StructureDamage structureDamage;
    public Rectangle bounds;
    public XRayRender xRayRender;

    private AudioManager.Sounds damageSound = AudioManager.Sounds.structureDamage;

    public float collapsePercent;
    public boolean collapsed = false;
    private boolean isCollapsing = false;
    private float collapseTimer = 0;

    public Structure(Scene scene, RectangleI gridRect) {
        this(scene, gridRect, 0.5f, 8, 3);
    }

    public Structure(Scene scene, RectangleI gridRect, int destructionRows, int destructionCols) {
        this(scene, gridRect, 0.5f, destructionRows, destructionCols);
    }

    public Structure(Scene scene, RectangleI gridRect, float collapsePercent) {
        this(scene, gridRect, collapsePercent, 8, 5);
    }

    public Structure(Scene scene, RectangleI gridRect, float collapsePercent, int destructionRows, int destructionCols) {
        this.scene = scene;
        this.particleManager = scene.particleManager;
        switch (scene.type) {
            case MICROBIOME: {
                internals = Structures.get(Structures.Type.BACTERIA_BACK);
                externals = Structures.get(Structures.Type.BACTERIA_FRONT);
            } break;
            case NEIGHBORHOOD: {
                // TODO: create 'stick frame' house assets for this level
                internals = Structures.get(Structures.Type.BRICK_BACK);
                externals = Structures.get(Structures.Type.BRICK_FRONT);
            } break;
            case CITY: {
                internals = Structures.get(Structures.Type.BRICK_BACK);
                externals = Structures.get(Structures.Type.BRICK_FRONT);
            } break;
            case MUSHROOM_KINGDOM: {
                // TODO: create structure images for this level
                internals = Structures.get(Structures.Type.BRICK_BACK);
                externals = Structures.get(Structures.Type.BRICK_FRONT);
            } break;
        }
        this.collapsePercent = collapsePercent;
        this.bounds = new Rectangle(gridRect.x, gridRect.y, gridRect.width, gridRect.height);
        this.structureDamage = new StructureDamage(this, destructionRows, destructionCols);
        this.xRayRender = new XRayRender(this, externals, internals, this.bounds, scene.camera);
    }

    public void damage(Player player, int x, int y) {
        if (!isCollapsing && structureDamage.applyDamage(player, x, y)) {
            Main.game.audioManager.playSound(this.damageSound);
        }
    }

    public void render(SpriteBatch batch) {
        xRayRender.render(batch);
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        this.structureDamage.renderDebug(batch, shapes);
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
            collapseTimer += dt;
            float collapsePercent = collapseTimer / collapseDuration;
            structureDamage.setMinDamageForAllTiles(collapsePercent);
            bounds.x += MathUtils.sin(collapseTimer * 120) * 5;
//            bounds.y -= Interpolation.exp10In.apply(0, bounds.height, collapsePercent);
            if (collapseTimer >= collapseDuration) {
                isCollapsing = false;
                collapsed = true;
                //  TODO: create physics particles here

            }
            randomSmoke();
        } else {
            if (structureDamage.getDamagePercent() > collapsePercent) {
                collapse();
            }
        }
    }

    public void collapse() {
        isCollapsing = true;
        scene.levelMap.removeStructure(this);
        collapseTimer = 0;
    }

    private void randomSmoke() {
        var particleEffect = particleManager.effects.get(ParticleEffectType.SMOKE);
        particleEffect.spawn(new SmokeEffect.Params(bounds.x + MathUtils.random(bounds.getWidth()), bounds.y, MathUtils.random(5f,10f)));
    }
}
