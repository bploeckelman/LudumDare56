package lando.systems.ld56.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
    public final ObjectMap<Layer, Array<Particle>> activeParticles;
    public final Pool<Particle> particlePool = Pools.get(Particle.class, MAX_PARTICLES);

    public ParticleManager(Assets assets) {
        this.assets = assets;
        this.activeParticles = new ObjectMap<>();
        int particlesPerLayer = MAX_PARTICLES / Layer.values().length;
        this.activeParticles.put(Layer.BACKGROUND, new Array<>(false, particlesPerLayer));
        this.activeParticles.put(Layer.FOREGROUND,     new Array<>(false, particlesPerLayer));
        init();
    }

    public void init() {
        effects.put(ParticleEffectType.ASUKA, new AsukaEffect(this));
        effects.put(ParticleEffectType.SMOKE, new SmokeEffect(this));
        effects.put(ParticleEffectType.FIRE,  new FireEffect(this));
    }

    public ParticleEffect randomEffect() {
        return (ParticleEffect) effects.values().toArray()[MathUtils.random(effects.size() - 1)];
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
