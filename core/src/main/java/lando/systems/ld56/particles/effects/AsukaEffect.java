package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;

public class AsukaEffect extends ParticleEffect {

    public AsukaEffect(ParticleManager particleManager) {
        super(particleManager);
    }

    public static class Params implements ParticleEffectParams {
        public Vector2 startPos;
        public Params(float x, float y) {
            startPos = new Vector2(x, y);
        }
    }

    @Override
    public void spawn(ParticleEffectParams parameters) {
        var params = (AsukaEffect.Params) parameters;
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;

        var amount = 10;
        var keyframe = Anims.get(Anims.Type.DOG).getKeyFrame(0);

        for (int i = 0; i < amount; i++) {
            var angle = MathUtils.random(30f, 160f);
            var speed = MathUtils.random(50f, 200f);
            var startSize = MathUtils.random(60f, 200f);
            var ttl = MathUtils.random(1f, 3f);
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startPos)
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
