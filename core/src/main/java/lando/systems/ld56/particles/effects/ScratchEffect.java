package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Particles;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.utils.Utils;

public class ScratchEffect extends ParticleEffect {

    public ScratchEffect(ParticleManager particleManager) {
        super(particleManager);
    }

    public static class Params implements ParticleEffectParams {
        public float startX;
        public float startY;
        public Params(float x, float y) {
            startX = x;
            startY = y;
        }
    }

    @Override
    public void spawn(ParticleEffectParams parameters) {
        var params = (ScratchEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;

        var amount = 1;

        for (int i = 0; i < amount; i++) {
            var keyframe = Particles.get(Particles.Type.SCRATCH).getKeyFrame(0);
            var startSize = 50f;
            var ttl = 1f;
            var startingColor = Color.RED;
            var endColor = Color.MAROON;
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX, params.startY)
                .startColor(startingColor)
                .startRotation(MathUtils.random(360f))
                .endColor(Color.CLEAR)
                .startSize(startSize)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
