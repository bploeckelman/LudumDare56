package lando.systems.ld56.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.particles.effects.*;

import java.util.HashMap;
import java.util.Map;

public class ParticleManager implements Disposable {

    public enum Layer { BACKGROUND, FOREGROUND }

    private static final int MAX_PARTICLES = 4000;

    private final Assets assets;
    public final Map<ParticleEffectType, ParticleEffect> effects = new HashMap<>();
    private final ObjectMap<Layer, Array<Particle>> activeParticles;
    private final Pool<Particle> particlePool = Pools.get(Particle.class, MAX_PARTICLES);

    public ParticleManager(Assets assets) {
        this.assets = assets;
        this.activeParticles = new ObjectMap<>();
        int particlesPerLayer = MAX_PARTICLES / Layer.values().length;
        this.activeParticles.put(Layer.BACKGROUND, new Array<>(false, particlesPerLayer));
        this.activeParticles.put(Layer.FOREGROUND,     new Array<>(false, particlesPerLayer));
        init();
    }

    public void init() {
        effects.put(ParticleEffectType.LEVEL_UP, new AsukaEffect(this));
        effects.put(ParticleEffectType.SMOKE, new SmokeEffect(this));
    }

    public void spawn(Layer layer, TextureRegion textureRegion, int amount, Vector2 startPos, Vector2 endPos, Color startColor, Color endColor, float startSize, float endSize, float timeToLive) {
        for (int i = 0; i < amount; i++) {
            activeParticles.get(layer).add(Particle.initializer(particlePool.obtain())
                .keyframe(textureRegion)
                .startPos(startPos)
                .endPos(endPos)
                .startColor(startColor)
                .endColor(endColor)
                .startSize(startSize)
                .endSize(endSize)
                .timeToLive(timeToLive)
                .init()
            );
        }
    }

    public void spawn(Layer layer, TextureRegion textureRegion, int amount, Vector2 startPos, float angle, float speed, Color startColor, Color endColor, float startSize, float endSize, float timeToLive) {
        for (int i = 0; i < amount; i++) {
            activeParticles.get(layer).add(Particle.initializer(particlePool.obtain())
                .keyframe(textureRegion)
                .startPos(startPos)
                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
                .startColor(startColor)
                .endColor(endColor)
                .startSize(startSize)
                .endSize(endSize)
                .timeToLive(timeToLive)
                .init()
            );
        }
    }

    public void clear() {
        for (Layer layer : Layer.values()) {
            particlePool.freeAll(activeParticles.get(layer));
            activeParticles.get(layer).clear();
        }
    }

    public void update(float dt) {
        for (Layer layer : Layer.values()) {
            for (int i = activeParticles.get(layer).size - 1; i >= 0; --i) {
                Particle particle = activeParticles.get(layer).get(i);
                particle.update(dt);
                if (particle.isDead()) {
                    activeParticles.get(layer).removeIndex(i);
                    particlePool.free(particle);
                }
            }
        }
    }


    public void draw(SpriteBatch batch, Layer layer) {
        activeParticles.get(layer).forEach(particle -> particle.render(batch));
    }

    @Override
    public void dispose() {
        clear();
    }
}
