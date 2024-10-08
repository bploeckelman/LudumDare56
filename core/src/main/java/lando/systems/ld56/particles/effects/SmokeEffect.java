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
        public float endSize;
        public float speedScale;
        public int amount;

        public Params(float x, float y, float startSize) {
            this(x, y, startSize, 0f, 1f, 5);
        }

        public Params(float x, float y, float startSize, float endSize, float speedScale, int amount) {
            startX = x;
            startY = y;
            this.startSize = startSize;
            this.endSize = endSize;
            this.speedScale = speedScale;
            this.amount = amount;
        }
    }

    @Override
    public void spawn(ParticleEffectParams parameters) {
        var params = (SmokeEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.BACKGROUND);
        var pool = particleManager.particlePool;

        var amount = params.amount;
        var keyframe = Particles.get(Particles.Type.SMOKE).getKeyFrame(0);

        for (int i = 0; i < amount; i++) {
            var angle = MathUtils.random(30f, 140f);
            var speed = MathUtils.random(50f, 100f) * params.speedScale;
            var startSize = MathUtils.random(60f, 200f);
            var ttl = MathUtils.random(1f, 3f);
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX, params.startY)
                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
                .startColor(Color.WHITE)
                .endColor(Color.CLEAR)
                .startSize(startSize)
                .endSize(params.endSize)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
