package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;

public class RatSwarmEffect extends ParticleEffect {

    public RatSwarmEffect(ParticleManager particleManager) {
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
        var params = (RatSwarmEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;
        var angle = 0f;
        var amount = 30;
        var keyframe = Anims.get(Anims.Type.RAT_WALK).getKeyFrame(0f);

        for (int i = 0; i < amount; i++) {
            var startSize = MathUtils.random(5f, 20f);
            var ttl = MathUtils.random(1f, 3f);
            var startPosY = MathUtils.random(params.startY - 50, params.startY + 50);
            var velocity = MathUtils.random(50f, 100f);
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX - 50f, startPosY)
                .velocity(MathUtils.cosDeg(angle) * velocity, MathUtils.sinDeg(angle) * velocity)
                .startColor(Color.WHITE)
                .startSize(startSize)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
