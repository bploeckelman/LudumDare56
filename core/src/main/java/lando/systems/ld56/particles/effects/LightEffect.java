package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.assets.Particles;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.utils.Utils;

public class LightEffect extends ParticleEffect {

    public LightEffect(ParticleManager particleManager) {
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
        var params = (LightEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;
        var amount = 1;
        var startingColor = Color.YELLOW;

        for (int i = 0; i < amount; i++) {
            var keyframe = Particles.get(Particles.Type.LIGHT).getKeyFrame(MathUtils.random());
            var startSize = 15f;
            var endSize = 10f;
            var ttl = 1f;
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX, params.startY)
                .startColor(startingColor)
                .endColor(startingColor.r, startingColor.g, startingColor.b, 0.75f)
                .startSize(startSize)
                .endSize(endSize)
                .interpolation(Interpolation.fastSlow)
                .timeToLive(ttl) // when looping is enabled, this will act as the time between loops
                .looping() // this will live until particleManager is destroyed
                .init()
            );
        }
    }
}
