package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Particles;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;

public class CampfireFlameEffect extends ParticleEffect {

    public CampfireFlameEffect(ParticleManager particleManager) {
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
        var params = (CampfireFlameEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;

        var amount = 20;

        for (int i = 0; i < amount; i++) {
            var keyframe = Particles.get(Particles.Type.FLAME).getKeyFrame(MathUtils.random());
            var angle = MathUtils.random(60f, 120f);
            var speed = 10;
            var startSize = MathUtils.random(50f, 90f);
            var ttl = 15f;
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX + MathUtils.random(-10f, 10f), params.startY + MathUtils.random(-10f, 10f))
                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
                .startColor(MathUtils.random(.7f, 1f), MathUtils.random(.8f), MathUtils.random(.0f, .2f), 1f)
                .endColor(Color.CLEAR)
                .startSize(startSize)
                .endSize(0f)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
