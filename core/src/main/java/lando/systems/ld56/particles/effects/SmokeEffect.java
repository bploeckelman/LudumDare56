package lando.systems.ld56.particles.effects;

import lando.systems.ld56.particles.ParticleManager;

public class SmokeEffect extends ParticleEffect {

    ParticleEffectParams params = new ParticleEffectParams(
            ParticleManager.Layer.FOREGROUND,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            1,
            1,
            1,
            1
    );
    public SmokeEffect(ParticleManager particleManager) {
        super(particleManager);
    }

    @Override
    public void spawn(ParticleEffectParams params) {
        particleManager.spawn(params);
    }
}
