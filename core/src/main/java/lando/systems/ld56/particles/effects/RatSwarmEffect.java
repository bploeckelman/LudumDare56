package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.Config;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.particles.Particle;
import lando.systems.ld56.particles.ParticleManager;

import java.util.ArrayList;

public class RatSwarmEffect extends ParticleEffect {

    public RatSwarmEffect(ParticleManager particleManager) {
        super(particleManager);
    }



    @Override
    public void spawn(ParticleEffectParams parameters) {
        var layer = particleManager.activeParticles.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;
        var amount = 50;
        var list = new ArrayList<Animation<TextureRegion>>();
        list.add(Anims.get(Anims.Type.RAT_WALK));
        list.add(Anims.get(Anims.Type.ANT_WALK));
        list.add(Anims.get(Anims.Type.WORM_WALK));
        list.add(Anims.get(Anims.Type.PHAGE_WALK));

        for (int i = 0; i < amount; i++) {
            var startSize = MathUtils.random(30f, 50f);
            var ttl = MathUtils.random(5f, 10f);
            var animation = list.get(MathUtils.random(list.size() - 1));
            var velocity = 90f;
            layer.add(Particle.initializer(pool.obtain())
                .animation(animation)
                .startPos(- 10f, MathUtils.random(0f, 200f))
                .targetPos(Config.Screen.window_width + 10f, MathUtils.random(0f, 200f))
                .startColor(Color.WHITE)
                .startSize(animation.getKeyFrame(0).getRegionWidth(), animation.getKeyFrame(0).getRegionHeight())
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
