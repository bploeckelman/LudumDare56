package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld56.particles.ParticleManager;

public class LevelUpEffect extends ParticleEffect {
    ParticleManager.Layer layer = ParticleManager.Layer.FOREGROUND;
    float angle = MathUtils.random(0f, 360f);
    float speed = 0f;
    float startSize = MathUtils.random(50f, 80f);
    float endSize = 0f;

    public LevelUpEffect(ParticleManager particleManager) {
        super(particleManager);
    }

    @Override
    public void spawn(ParticleEffectParams params) {
        particleManager.spawn(layer, params.textureRegion, 30, params.startPos, params.endPos, angle, speed, Color.BLUE, Color.RED, startSize, endSize, 1f);
    }
}
