package lando.systems.ld56.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.entities.LevelMap;
import lando.systems.ld56.entities.Npc;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.Structure;
import lando.systems.ld56.entities.TestXRay;
import lando.systems.ld56.particles.ParticleManager;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Scene {

    public Assets assets;
    public Player player;
    public Npc antPunch;
    public Npc andClimbPunch;
    public LevelMap levelMap;
    public Array<Structure> structures;
    public TextureRegion background;
    public OrthographicCamera camera;
    public Array<TestXRay> testXRays;
    public ParticleManager particleManager;

    public Scene(Assets assets, ParticleManager particleManager, OrthographicCamera camera, int tileSize, int cols, int rows) {
        this.assets = assets;
        this.camera = camera;
        this.player = new Player(assets, (cols * tileSize) / 2f, 50);
        this.antPunch = new Npc((cols * tileSize) / 3, tileSize, Anims.Type.ANT_PUNCH);
        this.andClimbPunch = new Npc((int) ((cols * tileSize) * (2 / 3f)), tileSize, Anims.Type.ANT_CLIMB_PUNCH);
        this.levelMap = new LevelMap(tileSize, cols, rows);
        this.particleManager = particleManager;
        structures = new Array<>();

        structures.add(new Structure(assets, camera.viewportWidth / 4  - 100, 0, 200, 300, this, .5f));
        structures.add(new Structure(assets, camera.viewportWidth / 4 * 2 - 150, 0, 300, 400, this, .3f));
        structures.add(new Structure(assets, camera.viewportWidth / 4 * 3 - 100, 0, 200, 340, this, .6f));

        this.background = assets.atlas.findRegions("backgrounds/background-level-1").first();

        levelMap.setBorderSolid();
        for (Structure structure : structures) {
            levelMap.setClimbable(structure);
        }

        testXRays = new Array<>();

//        testXRays.add(new TestXRay(new Rectangle(200, 30, 300, 300), camera));
//        testXRays.add(new TestXRay(new Rectangle(800, 30, 400, 400), camera));
    }

    public void update(float dt) {
        player.update(dt);
        antPunch.update(dt);
        andClimbPunch.update(dt);
        for (TestXRay testXRay : testXRays) {
            testXRay.update(dt);
        }
        for (int i = structures.size-1; i >=0; i--) {
            Structure structure = structures.get(i);
            structure.update(dt);
            if (structure.collapsed) {
                structures.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0, camera.viewportWidth, camera.viewportHeight);
        for (Structure structure : structures) {
            structure.render(batch);
        }
        antPunch.render(batch);
        andClimbPunch.render(batch);
        player.render(batch);
        for (TestXRay testXRay : testXRays) {
            testXRay.render(batch);
        }
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        levelMap.renderDebug(shapes);
        player.renderDebug(batch, shapes);
        for (Structure structure : structures) {
            structure.renderDebug(batch, shapes);
        }
    }

    public void renderFrameBuffers(SpriteBatch batch) {
        for (Structure structure : structures) {
            structure.renderFrameBuffers(batch);
        }
        for (TestXRay testXRay : testXRays) {
            testXRay.renderFrameBuffers(batch);
        }
    }

    public void paintGridAt(int x, int y) {
        var solid = true;
        levelMap.solidCollider.setGridTileSolid(x, y, solid);

        // temp
        for (Structure structure : structures) {
            structure.damage(player, Gdx.input.getX(), (int) camera.viewportHeight - Gdx.input.getY());
        }
    }

    public void eraseGridAt(int x, int y) {
        var solid = false;
        levelMap.solidCollider.setGridTileSolid(x, y, solid);
    }
}
