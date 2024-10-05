package lando.systems.ld56.scene;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld56.Main;
import lando.systems.ld56.entities.Entity;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.utils.RectangleI;
import lando.systems.ld56.utils.Utils;
import text.formic.Stringf;

public class Grid {

    private final int width;
    private final int height;
    private final int tileSize;
    private final Array<Tile> tiles;

    public class Tile extends Entity {
        GridPoint2 pos;
        RectangleI bounds;
        Collider collider;
        boolean solid;

        public Tile(int x, int y) {
            int size = Grid.this.tileSize;
            this.pos = new GridPoint2(x, y);
            this.bounds = new RectangleI(pos.x * size, pos.y * size, size, size);
            this.collider = null;
            this.solid = false;
            Main.game.entityData.addEntity(this);
        }

        public void solid(boolean solid) {
            var wasSolid = this.solid;
            this.solid = solid;
            if (solid && !wasSolid) {
                collider = new Collider(this, Collider.Type.solid, bounds.x, bounds.y, bounds.width, bounds.height);
            } else if (collider != null) {
                Main.game.entityData.remove(collider, Collider.class);
                collider = null;
            }
        }
    }

    public Grid(int tileSize, int width, int height) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.tiles = new Array<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var tile = new Tile(x, y);
                this.tiles.add(tile);
            }
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int tileSize() {
        return tileSize;
    }

    public Array<Tile> tiles() {
        return tiles;
    }

    public Tile tileAtGridPos(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            Utils.log("Grid", Stringf.format("tileAtGridPos(%d, %d) coords out of bounds (%d, %d)", x, y, width, height));
            return null;
        }

        int index = y * width + x;
        return tiles.get(index);
    }

    public Tile tileAtWorldPos(int x, int y) {
        var right = width * tileSize;
        var top = height * tileSize;
        if (x < 0 || y < 0 || x >= right || y >= top) {
            Utils.log("Grid", Stringf.format("tileAtWorldPos(%d, %d) coords out of bounds (%d, %d)", x, y, right, top));
            return null;
        }

        int gridX = x / tileSize;
        int gridY = y / tileSize;
        int index = gridY * width + gridX;
        return tiles.get(index);
    }

    public void set(boolean solid, int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            Utils.log("Grid", Stringf.format("set(%d, %d) coords out of bounds (%d, %d)", x, y, width, height));
            return;
        }

        int index = y * width + x;
        var tile = tiles.get(index);
        tile.solid(solid);
    }

    public void set(boolean solid, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x + w > width || y + h > height) {
            Utils.log("Grid", Stringf.format("set(%d, %d : %d, %d) coords out of bounds (%d, %d)", x, y, w, h, width, height));
            return;
        }

        for (int iy = y; iy < y + h; iy++) {
            for (int ix = x; ix < x + w; ix++) {
                int index = iy * width + ix;
                var tile = tiles.get(index);
                tile.solid(solid);
            }
        }
    }
}
