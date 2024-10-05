package lando.systems.ld56.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld56.particles.ParticleManager;

public class ParticleEffectParams {
    public ParticleManager.Layer layer;
    public Vector2 startPos;
    public Vector2 endPos;
    public TextureRegion textureRegion;
    public float angle;
    public float speed;
    public Color startColor;
    public Color endColor;
    public float startSize;
    public float endSize;
    public float timeToLive;
    public int amount;

    public ParticleEffectParams(ParticleManager.Layer layer, Vector2 startPos, Vector2 endPos, TextureRegion textureRegion, float angle, float speed, Color startColor, Color endColor, float startSize, float endSize, float timeToLive, int amount) {
        this.layer = layer;
        this.startPos = startPos;
        this.endPos = endPos;
        this.textureRegion = textureRegion;
        this.angle = angle;
        this.speed = speed;
        this.startColor = startColor;
        this.endColor = endColor;
        this.startSize = startSize;
        this.endSize = endSize;
        this.timeToLive = timeToLive;
        this.amount = amount;
    }

    public ParticleEffectParams(Vector2 startPos, TextureRegion textureRegion) {
        this(ParticleManager.Layer.FOREGROUND, startPos, startPos, textureRegion, 0, 0, Color.WHITE, Color.WHITE, 1, 1, 1, 1);
    }
}
