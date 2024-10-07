package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Particles;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;

public class BloodEffect extends ParticleEffect {

    public BloodEffect(ParticleManager particleManager) {
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
        var params = (BloodEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;

        var amount = 5;
        var keyframe = Particles.get(Particles.Type.DIRT).getKeyFrame(MathUtils.random(1f));

        for (int i = 0; i < amount; i++) {
            var angle = MathUtils.random(30f, 140f);
            var speed = MathUtils.random(50f, 100f);
            var startSize = MathUtils.random(5f, 10f);
            var ttl = MathUtils.random(.25f, .5f);
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX, params.startY)
                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
                .startColor(Color.BROWN)
                .endColor(Color.CLEAR)
                .startSize(startSize)
                .endSize(startSize * 2f)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
