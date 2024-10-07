package lando.systems.ld56.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld56.Main;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.assets.Structures;
import lando.systems.ld56.entities.Follower;
import lando.systems.ld56.entities.LevelMap;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.Structure;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.particles.ParticleManager;
import lando.systems.ld56.physics.base.Collidable;
import lando.systems.ld56.physics.base.Influencer;
import lando.systems.ld56.physics.base.PhysicsSystem;
import lando.systems.ld56.physics.game.Debris;
import lando.systems.ld56.physics.game.GameBoundSegment;
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
    public Player.CreatureType creatureType;
    public LevelMap levelMap;
    public Array<TextureRegion> backgroundLayers;
    public Rectangle backgroundRectangle;
    public Array<Structure> structures;
    public Array<Follower> detachedFollowers = new Array<>();

    // Debris things
    public PhysicsSystem physics;
    public Array<Collidable> collidables = new Array<>();
    public Array<Influencer> influencers = new Array<>();

    // working data
    private final GridPoint2 offset = new GridPoint2(0, 0);

    public Scene(GameScreen screen, Type type, Player.CreatureType creatureType) {
        this.screen = screen;
        this.assets = screen.assets;
        this.camera = screen.worldCamera;
        this.particleManager = screen.particles;
        this.type = type;
        this.structures = new Array<>();
        this.creatureType = creatureType;

        init();
        physics = new PhysicsSystem(new Rectangle(0, 0, backgroundRectangle.width, backgroundRectangle.height));
        collidables.add(new GameBoundSegment(backgroundRectangle.width, 4 * 16, 0, 4 * 16 ));
        collidables.add(new GameBoundSegment(0, 4 * 16, 0, backgroundRectangle.height));
        collidables.add(new GameBoundSegment(backgroundRectangle.width, backgroundRectangle.height, backgroundRectangle.width, 4 * 16));
    }

    private void init() {
        Main.game.entityData.clearAllComponents(Collider.class);

        backgroundLayers = new Array<>();
        backgroundLayers.add(assets.atlas.findRegions("backgrounds/background-level-1").first());
        backgroundRectangle = new Rectangle(0,0, camera.viewportWidth, camera.viewportHeight);

        // Set up Backgrounds
        switch (type) {
            case MICROBIOME: {
                initMicroBiome();
            } break;
            case NEIGHBORHOOD:
                initNeighborhood();
                break;
            case CITY: // TODO: create background for this level
                initCity();
                break;
            case MUSHROOM_KINGDOM: // TODO: create background for this level
            {
                // TODO: create background for this level
            } break;
        }

        int tileSize = 16;
        int baseGridY = 4;
        int cols  = (int) Calc.ceiling(backgroundRectangle.width  / tileSize);
        int rows = (int) Calc.ceiling(backgroundRectangle.height / tileSize);
        levelMap = new LevelMap(tileSize, cols, rows);

        // TODO: change player, npc, enemy setup based on scene type
        var basePixelsY = baseGridY * tileSize;
        player = new Player(this, creatureType, (cols * tileSize) / 2f, basePixelsY + (2 * tileSize), particleManager);


        // Create Structures
        switch (type) {
            case MICROBIOME:
                initMicroBiomeStructures(basePixelsY);
                break;
            case NEIGHBORHOOD:
                initNeighborhoodStructures(basePixelsY);
                break;
            case CITY:
                break;
            case MUSHROOM_KINGDOM:
                break;
        }


        levelMap.setBorderSolid();
        levelMap.setRowSolid(baseGridY - 1);
        for (Structure structure : structures) {
            levelMap.setClimbable(structure);
        }
    }

    private void initMicroBiome() {
        backgroundLayers.clear();
        backgroundLayers.add(assets.atlas.findRegions("backgrounds/background-biome").first());
        backgroundRectangle = new Rectangle(0,0, camera.viewportWidth, camera.viewportHeight);
    }



    private void initNeighborhood() {
        backgroundLayers.clear();
        backgroundLayers.add(assets.atlas.findRegions("backgrounds/background-neighborhood-sky").first());
        backgroundLayers.add(assets.atlas.findRegions("backgrounds/background-neighborhood-overlay").first());
        backgroundRectangle = new Rectangle(0,0, 3840, 720);
    }

    private void initCity() {
        backgroundLayers.clear();
        backgroundLayers.add(assets.atlas.findRegions("backgrounds/background-biome").first());        backgroundRectangle = new Rectangle(0,0, camera.viewportWidth, camera.viewportHeight);
    }

    private void initMicroBiomeStructures(int basePixelsY) {
        structures.add(new Structure(this, new RectangleI(224, basePixelsY, 192, 256), Structures.Type.BACTERIA));
        structures.add(new Structure(this, new RectangleI(592, basePixelsY, 96, 320), Structures.Type.BACTERIA));
        structures.add(new Structure(this, new RectangleI(864, basePixelsY, 192, 256), Structures.Type.BACTERIA));


    }

    private void initNeighborhoodStructures(int basePixelsY) {
        structures.add(new Structure(this, new RectangleI(224, basePixelsY, 192, 256), Structures.Type.HOUSE_A));
        structures.add(new Structure(this, new RectangleI(592, basePixelsY, 192, 256), Structures.Type.HOUSE_B));
        structures.add(new Structure(this, new RectangleI(960, basePixelsY, 192, 256), Structures.Type.HOUSE_C));
        structures.add(new Structure(this, new RectangleI(1328, basePixelsY, 192, 256), Structures.Type.HOUSE_D));
        structures.add(new Structure(this, new RectangleI(1696, basePixelsY, 192, 256), Structures.Type.HOUSE_E));
        structures.add(new Structure(this, new RectangleI(2064, basePixelsY, 192, 256), Structures.Type.HOUSE_B));
        structures.add(new Structure(this, new RectangleI(2432, basePixelsY, 192, 256), Structures.Type.HOUSE_D));
        structures.add(new Structure(this, new RectangleI(2800, basePixelsY, 192, 256), Structures.Type.HOUSE_A));
        structures.add(new Structure(this, new RectangleI(3168, basePixelsY, 192, 256), Structures.Type.HOUSE_C));

    }

    public void update(float dt, boolean gameEnding) {
        physics.update(dt, collidables, influencers);
        player.update(dt, gameEnding);

        for (int i = structures.size-1; i >=0; i--) {
            Structure structure = structures.get(i);
            structure.update(dt);
            if (structure.collapsed) {
                structures.removeIndex(i);
            }
        }

        for (int i = collidables.size -1; i >= 0; i--) {
            Collidable c = collidables.get(i);
            if (c instanceof Debris) {
                Debris d = (Debris) c;
                d.update(dt);
                if (d.shouldRemove()) {
                    collidables.removeIndex(i);
                }
            }
        }

        for (int i = detachedFollowers.size - 1; i >= 0; i--) {
            var follower = detachedFollowers.get(i);
            follower.update(dt);

            if (follower.canPickup()) {
                player.pickup(follower);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (TextureRegion background : backgroundLayers) {
            batch.draw(background, backgroundRectangle.x, backgroundRectangle.y, backgroundRectangle.width, backgroundRectangle.height);
        }

        particleManager.draw(batch, ParticleManager.Layer.BACKGROUND);

        for (Structure structure : structures) {
            structure.render(batch);
        }
        for (Collidable c : collidables) {
            if (c instanceof Debris) {
                ((Debris) c).render(batch);
            }
//            c.renderDebug(batch);
        }
        for (var follower : detachedFollowers) {
            follower.render(batch);
        }
        player.render(batch);
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        levelMap.renderDebug(shapes);
        for (Structure structure : structures) {
            structure.renderDebug(batch, shapes);
        }
        for (var follower : detachedFollowers) {
            follower.renderDebug(batch, shapes);
        }
        player.renderDebug(batch, shapes);
    }

    public void renderFrameBuffers(SpriteBatch batch) {
        for (Structure structure : structures) {
            structure.renderFrameBuffers(batch);
        }
    }

    public Vector2 getPlayerPosition() {
        return player.position.value;
    }

    public void paintGridAt(int x, int y) {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            var solid = true;
            levelMap.solidCollider.setGridTileSolid(x, y, solid);
        }

        // temp
        for (Structure structure : structures) {
            structure.damage(player, Gdx.input.getX(), (int) camera.viewportHeight - Gdx.input.getY());
        }
    }

    public void eraseGridAt(int x, int y) {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            var solid = false;
            levelMap.solidCollider.setGridTileSolid(x, y, solid);
        }
    }

    public boolean gameOver() {
        return structures.size <= 0;
    }
}
