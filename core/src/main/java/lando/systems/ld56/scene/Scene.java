package lando.systems.ld56.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.entities.LevelMap;
import lando.systems.ld56.entities.Npc;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.Structure;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Scene {

    public Assets assets;
    public Player player;
    public Npc antPunch;
    public Npc andClimbPunch;
    public LevelMap levelMap;
    public Structure structure;
    public TextureRegion background;
    public OrthographicCamera camera;

    public Scene(Assets assets, OrthographicCamera camera, int tileSize, int cols, int rows) {
        this.assets = assets;
        this.camera = camera;
        this.player = new Player(assets, (cols * tileSize) / 2f, 50);
        this.antPunch = new Npc((cols * tileSize) / 3, tileSize, Anims.Type.ANT_PUNCH);
        this.andClimbPunch = new Npc((int) ((cols * tileSize) * (2 / 3f)), tileSize, Anims.Type.ANT_CLIMB_PUNCH);
        this.levelMap = new LevelMap(tileSize, cols, rows);
        this.structure = new Structure(assets, (int) camera.viewportWidth / 2, 0);
        this.background = assets.atlas.findRegions("backgrounds/background-level-1").first();

        var solid = true;
        int w = levelMap.collider.grid.cols - 1;
        int h = levelMap.collider.grid.rows - 1;
        levelMap.collider.setGridTilesSolid(0, 0, w, 1, solid);
        levelMap.collider.setGridTilesSolid(0, 0, 1, h, solid);
        levelMap.collider.setGridTilesSolid(0, h, w, 1, solid);
        levelMap.collider.setGridTilesSolid(w, 0, 1, h, solid);
    }

    public void update(float dt) {
        player.update(dt);
        antPunch.update(dt);
        andClimbPunch.update(dt);
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0, camera.viewportWidth, camera.viewportHeight);
        structure.render(batch);
        antPunch.render(batch);
        andClimbPunch.render(batch);
        player.render(batch);
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        levelMap.renderDebug(shapes);
        player.renderDebug(batch, shapes);
        structure.renderDebug(batch, shapes);
    }

    public void paintGridAt(int x, int y) {
        var solid = true;
        levelMap.collider.setGridTileSolid(x, y, solid);

        // temp
        structure.damage(player, Gdx.input.getX(), (int)camera.viewportHeight - Gdx.input.getY());
    }

    public void eraseGridAt(int x, int y) {
        var solid = false;
        levelMap.collider.setGridTileSolid(x, y, solid);
    }
}
