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

        var amount = 1;

        for (int i = 0; i < amount; i++) {
            var keyframe = Particles.get(Particles.Type.FLARE).getKeyFrame(MathUtils.random());
            var angle = 90f;
            var speed = 300f;
            var acceleration = -100f;
            var startSize = 20f;
            var endSize = 150f;
            var ttl = 3f;
            var startingColor = Utils.randomColor();
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX + MathUtils.random(-10f, 10f), params.startY + MathUtils.random(-10f, 10f))
                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
                .acceleration(MathUtils.random(-50f, 50f), acceleration)
                .accelerationDamping(1f)
                .startColor(startingColor)
                .endColor(startingColor.r, startingColor.g, startingColor.b, 0f)
                .startSize(startSize)
                .endSize(endSize)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
