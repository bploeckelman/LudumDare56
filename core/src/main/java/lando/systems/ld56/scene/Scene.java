package lando.systems.ld56.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.entities.LevelMap;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.Structure;
import lando.systems.ld56.entities.TestXRay;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.screens.GameScreen;
import lando.systems.ld56.utils.Calc;
import lando.systems.ld56.utils.RectangleI;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Scene {

    public enum Type {
          MICROBIOME        (Player.CreatureType.PHAGE, Player.CreatureType.PARASITE)
        , NEIGHBORHOOD      (Player.CreatureType.WORM, Player.CreatureType.ANT)
        , CITY              (Player.CreatureType.RAT, Player.CreatureType.SNAKE)
        , MUSHROOM_KINGDOM  (Player.CreatureType.MARIO, Player.CreatureType.LUIGI)
        ;
        public final Player.CreatureType creatureTypeA;
        public final Player.CreatureType creatureTypeB;
        Type(Player.CreatureType creatureTypeA, Player.CreatureType creatureTypeB) {
            this.creatureTypeA = creatureTypeA;
            this.creatureTypeB = creatureTypeB;
        }
    }

    public final GameScreen screen;
    public final Assets assets;
    public final OrthographicCamera camera;
    public final ParticleManager particleManager;
    public final Type type;

    public Player player;
    public LevelMap levelMap;
    public TextureRegion background;
    public Array<Structure> structures;
    public Array<TestXRay> testXRays;

    public Scene(GameScreen screen, Type type) {
        this.screen = screen;
        this.assets = screen.assets;
        this.camera = screen.worldCamera;
        this.particleManager = screen.particles;
        this.type = type;
        this.structures = new Array<>();
        this.testXRays = new Array<>();

        init();
    }

    private void init() {
        int tileSize = 16;
        int baseGridY = 4;
        int cols  = (int) Calc.ceiling(camera.viewportWidth  / tileSize);
        int rows = (int) Calc.ceiling(camera.viewportHeight / tileSize);
        levelMap = new LevelMap(tileSize, cols, rows);

        // TODO: change player, npc, enemy setup based on scene type
        var basePixelsY = baseGridY * tileSize;
        var creatureType = type.creatureTypeA;
        player = new Player(creatureType, (cols * tileSize) / 2f, basePixelsY + (2 * tileSize), particleManager);

        int widthNormal = 12 * tileSize;
        int widthNarrow = 6 * tileSize;
        int heightNormal = 16 * tileSize;
        int heightTall = 20 * tileSize;

        // these are pixel positions that align with the LevelMap grid
        int left   = (int) Calc.floor(((1 / 4f) * camera.viewportWidth - (widthNormal / 2f)));
        int middle = (int) Calc.floor(((2 / 4f) * camera.viewportWidth - (widthNarrow / 2f)));
        int right  = (int) Calc.floor(((3 / 4f) * camera.viewportWidth - (widthNormal / 2f)));

        var gridRect1 = new RectangleI(left, basePixelsY, widthNormal, heightNormal);
        var gridRect2 = new RectangleI(middle, basePixelsY, widthNarrow, heightTall);
        var gridRect3 = new RectangleI(right, basePixelsY, widthNormal, heightNormal);

        structures.add(new Structure(this, gridRect1));
        structures.add(new Structure(this, gridRect2));
        structures.add(new Structure(this, gridRect3));

        switch (type) {
            case MICROBIOME: {
                // TODO: animated background
                background = assets.atlas.findRegions("backgrounds/background-biome").first();
            } break;
            case NEIGHBORHOOD:
            case CITY: // TODO: create background for this level
            case MUSHROOM_KINGDOM: // TODO: create background for this level
            {
                // TODO: create background for this level
                background = assets.atlas.findRegions("backgrounds/background-level-1").first();
            } break;
        }

        levelMap.setBorderSolid();
        levelMap.setRowSolid(baseGridY - 1);
        for (Structure structure : structures) {
            levelMap.setClimbable(structure);
        }
    }

    public void update(float dt) {
        player.update(dt);
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

        particleManager.draw(batch, ParticleManager.Layer.BACKGROUND);
        for (Structure structure : structures) {
            structure.render(batch);
        }
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
