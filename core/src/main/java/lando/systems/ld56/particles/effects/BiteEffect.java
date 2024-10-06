package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Particles;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;

public class BiteEffect extends ParticleEffect {

    public BiteEffect(ParticleManager particleManager) {
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
        var params = (BiteEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;

        var amount = 1;

        for (int i = 0; i < amount; i++) {
            var keyframe = Particles.get(Particles.Type.BITE).getKeyFrame(0f);
            var startSize = 50f;
            var ttl = .2f;
            var startingColor = Color.WHITE;
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX, params.startY)
                .startSize(62f, 36f)
                .endSize(62f, 0f)
                .startColor(startingColor)
                .startRotation(MathUtils.random(360f))
                .startSize(startSize)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
