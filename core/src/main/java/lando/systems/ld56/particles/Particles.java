package lando.systems.ld56.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import lando.systems.ld56.Config;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.utils.Time;
import lando.systems.ld56.utils.Util;

public class Particles implements Disposable {

    public enum Layer { BACKGROUND, FOREGROUND }

    private static final int MAX_PARTICLES = 4000;

    private final Assets assets;
    private final ObjectMap<Layer, Array<Particle>> activeParticles;
    private final Pool<Particle> particlePool = Pools.get(Particle.class, MAX_PARTICLES);

    public Particles(Assets assets) {
        this.assets = assets;
        this.activeParticles = new ObjectMap<>();
        int particlesPerLayer = MAX_PARTICLES / Layer.values().length;
        this.activeParticles.put(Layer.BACKGROUND, new Array<>(false, particlesPerLayer));
        this.activeParticles.put(Layer.FOREGROUND,     new Array<>(false, particlesPerLayer));
    }

    public void clear() {
        for (Layer layer : Layer.values()) {
            particlePool.freeAll(activeParticles.get(layer));
            activeParticles.get(layer).clear();
        }
    }

    public void update(float dt) {
        for (Layer layer : Layer.values()) {
            for (int i = activeParticles.get(layer).size - 1; i >= 0; --i) {
                Particle particle = activeParticles.get(layer).get(i);
                particle.update(dt);
                if (particle.isDead()) {
                    activeParticles.get(layer).removeIndex(i);
                    particlePool.free(particle);
                }
            }
        }
    }


    public void draw(SpriteBatch batch, Layer layer) {
        activeParticles.get(layer).forEach(particle -> particle.render(batch));
    }

    @Override
    public void dispose() {
        clear();
    }
//
//    // ------------------------------------------------------------------------
//    // Helper fields for particle spawner methods
//    // ------------------------------------------------------------------------
//    private final Color tempColor = new Color();
//    private final Vector2 tempVec2 = new Vector2();
//
//    // ------------------------------------------------------------------------
//    // Spawners for different particle effects
//    // ------------------------------------------------------------------------
//
//    public void smoke(float inX, float inY) {
//        for (int i = 0; i < 30; i++) {
//            float angle = MathUtils.random(0f, 360f);
//            float speed = MathUtils.random(0f, 100f);
//            float x = inX + MathUtils.random(-100f, 100f);
//            float y = inY + MathUtils.random(-100f, 100f);
//            float size = MathUtils.random(60f, 200f);
//            float color = MathUtils.random(.3f, 1f);
//            activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//                .keyframe(assets.particles.smoke)
//                .startPos(x, y)
//                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
//                .startColor(color, color, color, 1f)
//                .endColor(0, 0, 0, 0)
//                .startSize(size)
//                .endSize(5f)
//                .timeToLive(MathUtils.random(2f, 4f))
//                .init()
//            );
//        }
//    }
//
//    public void tinySmoke(float inX, float inY) {
//        for (int i = 0; i < 5; i++) {
//            float angle = MathUtils.random(0f, 360f);
//            float speed = MathUtils.random(0f, 10f);
//            float x = inX + MathUtils.random(-10f, 10f);
//            float y = inY + MathUtils.random(-10f, 10f);
//            float size = MathUtils.random(5f, 20f);
//            float color = MathUtils.random(.3f, 1f);
//            activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//                .keyframe(assets.particles.smoke)
//                .startPos(x, y)
//                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
//                .startColor(color, color, color, 1f)
//                .endColor(0, 0, 0, 0)
//                .startSize(size)
//                .endSize(5f)
//                .timeToLive(MathUtils.random(2f, 4f))
//                .init()
//            );
//        }
//    }
//
//    public void levelUpEffect(float x, float y) {
//        // Stars
//        for (int i = 0; i < 30; i++) {
//            float angle = MathUtils.random(0f, 360f);
//            float speed = MathUtils.random(50f, 150f);
//
//            activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//                .keyframe(assets.particles.stars.getKeyFrame(MathUtils.random(0f, 1f)))
//                .startPos(x, y)
//                .velocityDirection(angle, speed)
//                .startColor(1f, 1f, .8f, 1f)
//                .endColor(1f, 1f, .8f, 0f)
//                .startSize(MathUtils.random(50f, 80f))
//                .endSize(0f)
//                .timeToLive(MathUtils.random(1f, 2f))
//                .init()
//            );
//        }
//    }
//
//    public void portal(float x, float y, float radius) {
//        for (int i = 0; i < 50; i++) {
//            float angle = MathUtils.random(0f, 360f);
//            float distance = MathUtils.random(50f, 100f);
//            float offsetX = MathUtils.cosDeg(angle) * distance;
//            float offsetY = MathUtils.sinDeg(angle) * distance;
//            float targetX = x;
//            float targetY = y;
//
//            activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//                .keyframe(assets.particles.stars.getKeyFrame(MathUtils.random(0f, 1f)))
//                .startPos(x + offsetX, y + offsetY)
//                .targetPos(targetX, targetY)  // Target the portal's center
//                .startColor(.5f, .5f, 1f, 1f)
//                .endColor(.9f, .9f, 1f, 0f)
//                .startSize(MathUtils.random(25f, 50f))
//                .endSize(0f)
//                .timeToLive(MathUtils.random(2f, 4f))
//                .init()
//            );
//        }
//
//        for (int i = 0; i < 10; i++) {
//            float angle = MathUtils.random(0f, 360f);
//            float distance = radius + MathUtils.random(-10f, 10f);
//            float offsetX = MathUtils.cosDeg(angle) * distance;
//            float offsetY = MathUtils.sinDeg(angle) * distance;
//
//            activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//                .keyframe(assets.particles.twirls.getKeyFrame(MathUtils.random(0f, 1f)))
//                .startPos(x + offsetX, y + offsetY)
//                .velocity(MathUtils.cosDeg(angle) * 10f, MathUtils.sinDeg(angle) * 10f)
//                .startColor(0.5f, 0.5f, 1f, 0.5f)
//                .endColor(0.8f, 0.8f, 1f, 0f)
//                .startSize(MathUtils.random(25f, 50f))
//                .endSize(0f)
//                .startRotation(0f)
//                .endRotation(MathUtils.random(360, 1440))
//                .timeToLive(MathUtils.random(1f, 3f)) // Short lifespan
//                .init()
//            );
//        }
//    }
//
//    public void gameOver(boolean win) {
//        for (int i = 0; i < 5000; i++) {
//            float angle = MathUtils.random(0f, 360f);
//            float speed = MathUtils.random(200f, 500f);
//            float x = (Config.Screen.window_width / 2f) + MathUtils.random(-100f, 100f);
//            float y = (Config.Screen.window_height / 2f) + MathUtils.random(-100f, 100f);
//            float size = MathUtils.random(60f, 120f);
//            float color = MathUtils.random(.3f, 1f);
//            activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//                .animation(win ? assets.particles.twirls : assets.particles.blood)
//                .startPos(x, y)
//                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
//                .startColor(color, color, color, 1f)
//                .endColor(0, 0, 0, 0)
//                .startSize(size)
//                .endSize(5f)
//                .timeToLive(MathUtils.random(2f, 6f))
//                .init()
//            );
//        }
//    }
//
//    public void bloodBurst(float x, float y) {
//        for (int i = 0; i < 10; i++) {
//            float angle = MathUtils.random(0f, 360f);
//            float speed = MathUtils.random(10f, 30f);
//            float size = MathUtils.random(10f, 20f);
//            float color = MathUtils.random(.3f, 1f);
//            activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//                .keyframe(assets.particles.blood.getKeyFrame(MathUtils.random(0f, 1f)))
//                .startPos(x, y)
//                .velocityDirection(angle, speed)
//                .acceleration(0, -100f)
//                .startColor(color, 0, 0, 1f)
//                .endColor(0, 0, 0, 0)
//                .startSize(size)
//                .endSize(0f)
//                .timeToLive(MathUtils.random(1f, 2f))
//                .init()
//            );
//        }
//    }
//
//    public void bloodFountain(float x, float y) {
//        for (int i = 0; i < 10; i++) {
//            float angle = MathUtils.random(0f, 60f) + 60;
//            float speed = MathUtils.random(10f, 100f) + 200;
//            float size = MathUtils.random(10f, 20f);
//            float color = MathUtils.random(.3f, 1f);
//            activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//                .keyframe(assets.particles.blood.getKeyFrame(MathUtils.random(0f, 1f)))
//                .startPos(x, y)
//                .velocityDirection(angle, speed)
//                .acceleration(0, -700)
//                .accelerationDamping(0.95f)
//                .startColor(color, 0, 0, 1f)
//                .endColor(0, 0, 0, 0)
//                .startSize(size)
//                .endSize(0f)
//                .timeToLive(MathUtils.random(1f, 2f))
//                .init()
//            );
//        }
//    }
//
//    public void spawnBloodPuddle(float x, float y) {
//        activeParticles.get(Layer.BACKGROUND).add(Particle.initializer(particlePool.obtain())
//            .keyframe(assets.particles.splats.getKeyFrame(MathUtils.random(0f, 1f)))
//            .startPos(MathUtils.random(x - 10, x + 10), MathUtils.random(y - 10, y + 10))
//            .startColor(0.6f, 0.1f, 0.1f, 1f)
//            .startSize(MathUtils.random(40f, 50f))
//            .endSize(MathUtils.random(60f, 80f))
//            .init()
//        );
//    }
//
//    public void spawnFireball(float x, float y, float targetX, float targetY) {
//        activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//            .keyframe(assets.particles.fires.getKeyFrame(0f))
//            .startPos(x, y)
//            .targetPos(targetX, targetY)
//            .startColor(1f, 0.6f, 0.2f, 1f)
//            .startSize(MathUtils.random(50f, 75f))
//            .startRotation(MathUtils.random(0, 180))
//            .endRotation(MathUtils.random(720, 1440))
//            .acceleration(100f,100f)
//            .accelerationDamping(0.95f)
//            .timeToLive(.6f)
//            .init()
//        );
//        Time.do_after_delay(0.6f, param -> smoke(targetX, targetY));
//    }
//
//    public void spawnMagic(float x, float y, float targetX, float targetY) {
//        for (int i = 0; i < 10; i++) {
//            activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//                .keyframe(assets.particles.magics.getKeyFrame(MathUtils.random(1f)))
//                .startPos(x, y)
//                .targetPos(MathUtils.random(targetX - 10f, targetX + 10f), MathUtils.random(targetY - 10f, targetY + 10f))
//                .startColor(0.3f, 0.3f, 1f, 1f)
//                .startSize(MathUtils.random(50f, 75f))
//                .startRotation(MathUtils.random(0, 180))
//                .endRotation(MathUtils.random(720, 1440))
//                .acceleration(100f,100f)
//                .accelerationDamping(0.95f)
//                .timeToLive(.4f)
//                .init()
//            );
//        }
//        Time.do_after_delay(0.4f, param -> smoke(targetX, targetY));
//    }
//
//    public void spawnArrow(float x, float y, float targetX, float targetY) {
//        float angle = MathUtils.atan2(targetY - y, targetX - x);
//        activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//            .keyframe(assets.particles.traces.getKeyFrame(MathUtils.random(0f, 1f)))
//            .startPos(x, y)
//            .targetPos(targetX, targetY)
//            .startRotation(angle * MathUtils.radiansToDegrees + 90)
//            .startColor(1f, 1f, 1f, 1f)
//            .startSize(50f)
//            .timeToLive(0.5f)
//            .init()
//        );
//    }
//
//    public void spawnSwordSlash(float x, float y, float targetX, float targetY) {
//        activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//            .keyframe(assets.particles.twirls.getKeyFrame(MathUtils.random(0f, 1f)))
//            .startPos(x, y)
//            .targetPos(targetX, targetY)
//            .startColor(0.8f, 0.8f, 1f, 1f)
//            .endColor(0.8f, 0.8f, 1f, 0f)
//            .startSize(30f)
//            .endSize(50f)
//            .timeToLive(0.4f)
//            .init()
//        );
//    }
//
//    public void fanfareConfetti(float x, float y) {
//        for (int i = 0; i < 70; i++) {
//            activeParticles.get(Layer.FOREGROUND).add(Particle.initializer(particlePool.obtain())
//                .keyframe(assets.particles.stars.getKeyFrame(MathUtils.random(0f, 1f)))
//                .startPos(x, y)
//                .velocityDirection(MathUtils.random(90, 270), MathUtils.random(200))
//                .acceleration(50f, -1000f)
//                .accelerationDamping(.95f)
//                .startRotation(MathUtils.random(360f))
//                .endRotation(360f * 1000)
//                .startColor(Util.randomColor()) // Get a random color
//                .startSize(MathUtils.random(10f, 30f))
//                .endSize(MathUtils.random(35f, 60f))
//                .timeToLive(10f)
//                .init()
//            );
//        }
//    }
}
