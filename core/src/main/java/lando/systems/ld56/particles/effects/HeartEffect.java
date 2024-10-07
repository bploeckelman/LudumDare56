package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.assets.Icons;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;

public class HeartEffect extends ParticleEffect {

    public static class Params implements ParticleEffectParams {
        public boolean broken;
        public float x;
        public float y;
        public Params(boolean broken, float x, float y) {
            this.broken = broken;
            this.x = x;
            this.y = y;
        }
    }

    public HeartEffect(ParticleManager particleManager) {
        super(particleManager);
    }

    @Override
    public void spawn(ParticleEffectParams parameters) {
        var params = (HeartEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;

        var amount = 10;
        var iconType = params.broken ? Icons.Type.HEART_BROKEN : Icons.Type.HEART;
        var keyframe = Icons.get(iconType);

        for (int i = 0; i < amount; i++) {
            var angle = MathUtils.random(70f, 110f);
            var speed = MathUtils.random(50f, 300f);
            var startSize = MathUtils.random(10f, 30f);
            var ttl = MathUtils.random(1f, 3f);
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.x, params.y)
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
