package lando.systems.ld56.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.Structure;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Scene {

    public Assets assets;
    public Player player;
    public Structure structure;
    public Grid grid;
    public Grid.Tile highlightedTile;
    public TextureRegion background;
    public OrthographicCamera camera;

    public Scene(Assets assets, OrthographicCamera camera, int tileSize, int initialWidth, int initialHeight) {
        this.assets = assets;
        this.camera = camera;
        this.player = new Player(assets, (initialWidth * tileSize) / 2f, (initialHeight * tileSize) / 2f);
        this.structure = new Structure(assets, (int) camera.viewportWidth / 2, 0);
        this.grid = new Grid(tileSize, initialWidth, initialHeight);
        this.background = assets.atlas.findRegions("backgrounds/background-level-1").first();
        this.highlightedTile = null;

        var solid = true;
        var w = grid.width();
        var h = grid.height();
        grid.set(solid, 0, 0, w - 1, 1);
        grid.set(solid, 0, 0, 1, h - 1);
        grid.set(solid, 0, h - 1, w, 1);
        grid.set(solid, w - 1, 0, 1, h);
    }

    public void update(float dt) {
        player.update(dt);
    }

    private final Color solidColor = new Color(1, 0, 0, 0.2f);
    private final Color defaultColor = new Color(1, 1, 1, 0.2f);

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0, camera.viewportWidth, camera.viewportHeight);
        structure.render(batch);
        gridRender(batch);
        player.render(batch);
        highlightedTileRender(batch);
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        player.renderDebug(batch, shapes);
        structure.renderDebug(batch, shapes);
    }

    private void gridRender(SpriteBatch batch) {
        var pixel = assets.pixelRegion;
        for (var tile : grid.tiles()) {
            batch.setColor((tile.solid) ? solidColor : defaultColor);
            batch.draw(pixel, tile.bounds.x, tile.bounds.y, tile.bounds.width, tile.bounds.height);
        }
        batch.setColor(Color.WHITE);
    }

    private void highlightedTileRender(SpriteBatch batch) {
        if (highlightedTile == null) {
            return;
        }

        var pixel = assets.pixelRegion;
        var bounds = highlightedTile.bounds;
        batch.setColor(1, 0, 1, 0.25f);
        batch.draw(pixel, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);
    }


    public void paintGridAt(int x, int y) {
        var tile = grid.tileAtGridPos(x, y);
        if (tile != null) {
            tile.solid = true;
        }
    }

    public void eraseGridAt(int x, int y) {
        var tile = grid.tileAtGridPos(x, y);
        if (tile != null) {
            tile.solid = false;
        }
    }

    public void highlightGridAt(int x, int y) {
        var tile = grid.tileAtGridPos(x, y);
        if (tile != null) {
            highlightedTile = tile;
        }
    }
}
