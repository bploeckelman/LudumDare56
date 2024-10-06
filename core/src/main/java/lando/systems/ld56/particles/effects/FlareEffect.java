package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Particles;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.utils.Utils;

public class FlareEffect extends ParticleEffect {

    public FlareEffect(ParticleManager particleManager) {
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
        var params = (FlareEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;

        var amount = 10;

        for (int i = 0; i < amount; i++) {
            var keyframe = Particles.get(Particles.Type.FLARE).getKeyFrame(MathUtils.random());
            var angle = MathUtils.random(60f, 120f);
            var speed = 10;
            var startSize = MathUtils.random(20f, 50f);
            var ttl = MathUtils.random(1f, 3f);
            var startingColor = Utils.randomColor();
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX + MathUtils.random(-10f, 10f), params.startY + MathUtils.random(-10f, 10f))
                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
                .startColor(startingColor)
                .startSize(startSize)
                .endSize(0)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
