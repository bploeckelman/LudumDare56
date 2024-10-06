package lando.systems.ld56.particles.effects;

import lando.systems.ld56.particles.ParticleManager;

public class AsukaEffect extends ParticleEffect {

    public AsukaEffect(ParticleManager particleManager) {
        super(particleManager);
    }

    @Override
    public void spawn(ParticleEffectParams parameters) {
        var params = (AsukaEffectParams) parameters;
        particleManager.spawn(params.layer, params.textureRegion, params.amount, params.startPos, params.angle, params.speed, params.startColor, params.endColor, params.startSize, params.endSize, 2f);
    }
}
