package lando.systems.ld56.entities;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Structures;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.entities.components.StructureDamage;
import lando.systems.ld56.entities.components.XRayRender;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.particles.effects.ParticleEffectType;
import lando.systems.ld56.particles.effects.SmokeEffect;
import lando.systems.ld56.physics.game.Debris;
import lando.systems.ld56.scene.Scene;
import lando.systems.ld56.utils.RectangleI;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Structure extends Entity implements XRayable {

    public final Scene scene;
    public static float collapseDuration = 3f;

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
    Color tintColor = new Color(Color.WHITE);

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
        this.internals.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.externals.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.collapsePercent = collapsePercent;
        this.bounds = new Rectangle(gridRect.x, gridRect.y, gridRect.width, gridRect.height);
        this.structureDamage = new StructureDamage(this, destructionRows, destructionCols);
        this.xRayRender = new XRayRender(this, externals, internals, this.bounds, scene.camera);
    }

    public void damage(Player player, int x, int y) {
        if (!isCollapsing && structureDamage.applyDamage(player, x, y)) {
            Main.game.audioManager.playSound(AudioManager.Sounds.impact);
        }
    }

    public void render(SpriteBatch batch) {
        if (isCollapsing) {
            tintColor.a = 1f - collapseTimer / collapseDuration;;
        }
        batch.setColor(tintColor);
        batch.draw(internals, bounds.x, bounds.y, bounds.width, bounds.height);
        xRayRender.render(batch, tintColor);
        batch.setColor(Color.WHITE);
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
//            structureDamage.setMinDamageForAllTiles(collapsePercent);
            bounds.x += MathUtils.sin(collapseTimer * 120) * 5;
//            bounds.y -= Interpolation.exp10In.apply(0, bounds.height, collapsePercent);
            if (collapseTimer >= collapseDuration) {
                isCollapsing = false;
                collapsed = true;

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
        createDebris();
        structureDamage.setMinDamageForAllTiles(1f);
        Main.playSound(AudioManager.Sounds.collapse);

    }

    private void randomSmoke() {
        var particleEffect = particleManager.effects.get(ParticleEffectType.SMOKE);
        particleEffect.spawn(new SmokeEffect.Params(bounds.x + MathUtils.random(bounds.getWidth()), bounds.y, MathUtils.random(5f,10f)));
    }

    int subdivisions = 2;
    private void createDebris() {
        float width = bounds.width/ structureDamage.columns;
        float height = bounds.height/ structureDamage.rows;
        for (int y = 0; y < structureDamage.rows; y++) {
            for (int x = 0; x < structureDamage.columns; x++) {
                if (structureDamage.damage[x][y] < 1f) {
                    for (int ix = 0; ix < subdivisions; ix++) {
                        for (int iy = 0; iy < subdivisions; iy++) {
                            float du = 1f/ (structureDamage.columns * subdivisions);
                            float dv = 1f/ (structureDamage.rows * subdivisions);
                            Debris d = new Debris(new Vector2(bounds.x + width * x + ix * width / (subdivisions) + width / (subdivisions *2f), bounds.y + height * y + iy * height / (subdivisions *2f) + height / (subdivisions * 2f)),
                                width/subdivisions, height/subdivisions,
                                new TextureRegion(externals, (ix + x * subdivisions) * (du), 1f - (iy + y * subdivisions) * (dv), (1 + ix + x * subdivisions) * (du), 1f - (1 + iy + y * subdivisions) * (dv)));
                            scene.collidables.add(d);
                        }
                    }
                }
            }
        }
    }
}
