package lando.systems.ld56.particles.effects;

import lando.systems.ld56.particles.ParticleManager;

public abstract class ParticleEffect {
    ParticleManager particleManager;
    public ParticleEffect(ParticleManager particleManager) {
        this.particleManager = particleManager;
    }
    public abstract void spawn(ParticleEffectParams params);
}
