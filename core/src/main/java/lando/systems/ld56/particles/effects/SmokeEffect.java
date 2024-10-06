package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Particles;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;

public class SmokeEffect extends ParticleEffect {

    public SmokeEffect(ParticleManager particleManager) {
        super(particleManager);
    }

    public static class Params implements ParticleEffectParams {
        public float startX;
        public float startY;
        public float startSize;

        public Params(float x, float y, float startSize) {
            startX = x;
            startY = y;
            this.startSize = startSize;
        }
    }

    @Override
    public void spawn(ParticleEffectParams parameters) {
        var params = (SmokeEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;

        var amount = 5;
        var keyframe = Particles.get(Particles.Type.SMOKE).getKeyFrame(0);

        for (int i = 0; i < amount; i++) {
            var angle = MathUtils.random(30f, 140f);
            var speed = MathUtils.random(50f, 100f);
            var startSize = MathUtils.random(60f, 200f);
            var ttl = MathUtils.random(1f, 3f);
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX, params.startY)
                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
                .startColor(Color.WHITE)
                .endColor(Color.CLEAR)
                .startSize(startSize)
                .endSize(0)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
