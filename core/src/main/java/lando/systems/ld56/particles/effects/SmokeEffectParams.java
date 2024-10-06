package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.particles.ParticleManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SmokeEffectParams implements ParticleEffectParams {
    public ParticleManager.Layer layer;
    public Vector2 startPos;
    public TextureRegion textureRegion;
    public float angle;
    public float speed;
    public Color startColor;
    public Color endColor;
    public float startSize;
    public float endSize;
    public float timeToLive;
    public int amount;

    public SmokeEffectParams(Vector2 startPos, float startSize) {
        this(ParticleManager.Layer.FOREGROUND,
            startPos,
            Anims.get(Anims.Type.CAT).getKeyFrame(0),
            MathUtils.random(60, 120),
            MathUtils.random(100, 200),
            Color.WHITE,
            Color.CLEAR,
            startSize,
            startSize * 1.2f,
            1,
            1);
    }
}
