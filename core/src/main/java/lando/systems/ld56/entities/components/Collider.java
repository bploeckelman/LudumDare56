package lando.systems.ld56.entities.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import lando.systems.ld56.Main;
import lando.systems.ld56.entities.Entity;
import lando.systems.ld56.utils.Calc;
import lando.systems.ld56.utils.RectangleI;
import lando.systems.ld56.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;
import text.formic.Stringf;

public class Collider extends Component {

    public enum Type {solid, climbable, player}
    public enum Shape {rect, grid}

    public final GridPoint2 origin;
    public final Type type;
    public final Shape shape;
    public final RectangleI rect;
    public final Grid grid;

    public static Collider makeRect(Entity entity, Type type, int x, int y, int width, int height) {
        return new Collider(entity, type, x, y, width, height);
    }

    public static Collider makeGrid(Entity entity, Type type, int originX, int originY, int tileSize, int cols, int rows) {
        return new Collider(entity, type, originX, originY, tileSize, cols, rows);
    }

    public static class Grid {
        public int tileSize;
        public int cols;
        public int rows;
        public Tile[] tiles;

        public Grid(int tileSize, int cols, int rows) {
            this.tileSize = tileSize;
            this.cols = cols;
            this.rows = rows;
            this.tiles = new Tile[cols*rows];
            for (int i = 0; i < cols*rows; i++) {
                tiles[i] = new Tile();
            }
        }

        public static class Tile {
            public boolean solid = false;
            public boolean climbable = false;
        }
    }

    // temporary objects for collision tests and rendering
    public final RectangleI rectA = new RectangleI();
    private final RectangleI rectB = new RectangleI();
    private final GridPoint2 pointA = new GridPoint2();
    private final GridPoint2 pointB = new GridPoint2();

    private Collider(Entity entity, Type type, int x, int y, int width, int height) {
        super(entity, Collider.class);
        this.origin = new GridPoint2(0, 0);
        this.type = type;
        this.shape = Shape.rect;
        this.rect = new RectangleI(x, y, width, height);
        this.grid = null;
    }

    private Collider(Entity entity, Type type, int originX, int originY, int tileSize, int cols, int rows) {
        super(entity, Collider.class);
        this.origin = new GridPoint2(originX, originY);
        this.type = type;
        this.shape = Shape.grid;
        this.rect = null;
        this.grid = new Grid(tileSize, cols, rows);
    }

    public void render(ShapeDrawer shapes) {
        var pos = Main.game.entityData.get(entity, Position.class);
        int x = (pos != null) ? (int) pos.value.x : 0;
        int y = (pos != null) ? (int) pos.value.y : 0;
        rectA.set(
            x + this.origin.x + this.rect.x,
            y + this.origin.y + this.rect.y,
            this.rect.width, this.rect.height);
        shapes.rectangle(rectA.x, rectA.y, rectA.width, rectA.height, Color.YELLOW, 1);
    }

    public Grid.Tile getGridTile(int x, int y) {
        if (shape != Shape.grid) {
            Utils.log("Collider", Stringf.format("Can't get grid tile at (%d,%d), collider is not a grid", x, y));
            return null;
        }
        if (x < 0 || y < 0 || x >= grid.cols || y >= grid.rows) {
            Utils.log("Collider", Stringf.format("Can't get grid tile at (%d,%d), coords are out of bounds (%d,%d)", x, y, grid.cols, grid.rows));
            return null;
        }
        int index = y * grid.cols + x;
        return grid.tiles[index];
    }

    public Collider setGridTileSolid(int x, int y, boolean solid) {
        var tile = getGridTile(x, y);
        if (tile != null) {
            tile.solid = solid;
        }
        return this;
    }

    public Collider setGridTileClimbable(int x, int y, boolean climbable) {
        var tile = getGridTile(x, y);
        if (tile != null) {
            tile.climbable = climbable;
        }
        return this;
    }

    public Collider setGridTilesSolid(int x, int y, int w, int h, boolean solid) {
        for (int iy = y; iy < y + h; iy++) {
            for (int ix = x; ix < x + w; ix++) {
                var tile = getGridTile(ix, iy);
                if (tile == null) continue;
                tile.solid = solid;
            }
        }
        return this;
    }

    public Collider setGridTilesClimbable(int x, int y, int w, int h, boolean climbable) {
        for (int iy = y; iy < y + h; iy++) {
            for (int ix = x; ix < x + w; ix++) {
                var tile = getGridTile(ix, iy);
                if (tile == null) continue;
                tile.climbable = climbable;
            }
        }
        return this;
    }

    public Collider removeAllTiles(int x, int y, int w, int h) {
        for (int iy = y; iy< y + h; iy++) {
            for (int ix = x; ix < x+w; ix++) {
                var tile = getGridTile(ix, iy);
                if (tile == null) continue;
                tile.climbable = false;
                tile.solid = false;
            }
        }
        return this;
    }

    public boolean check(GridPoint2 offset, Type type) {
        var colliders = Main.game.entityData.getComponents(Collider.class);
        for (var collider : colliders) {
            if (collider == this) continue;
            if (collider.type != type) continue;
            var isOverlapping = overlaps(collider, offset, type);
            if (isOverlapping) {
                return true;
            }
        }
        return false;
    }

    public boolean overlaps(Collider other, GridPoint2 offset, Type type) {
        if (shape == Shape.rect) {
            if (other.shape == Shape.rect) {
                return overlapsRectRect(this, other, offset);
            } else if (other.shape == Shape.grid) {
                return overlapsRectGrid(this, other, offset, type);
            }
        } else if (shape == Shape.grid) {
            if (other.shape == Shape.rect) {
                return overlapsRectGrid(other, this, offset, type);
            } else if (other.shape == Shape.grid) {
                Utils.log("Collider", "Grid/Grid overlap check unsupported");
            }
        }
        return false;
    }

    public boolean overlapsRectRect(Collider a, Collider b, GridPoint2 offset) {
        var entityData = Main.game.entityData;

        pointA.set(0, 0);
        pointB.set(0, 0);
        var posA = entityData.get(a.entity, Position.class);
        var posB = entityData.get(b.entity, Position.class);
        if (posA != null) pointA.set((int) posA.x(), (int) posA.y());
        if (posB != null) pointB.set((int) posB.x(), (int) posB.y());

        rectA.set(
            a.origin.x + a.rect.x + pointA.x + offset.x,
            a.origin.y + a.rect.y + pointA.y + offset.x,
            a.rect.width, a.rect.height);
        rectB.set(
            b.origin.x + b.rect.x + pointB.x,
            b.origin.y + b.rect.y + pointB.y,
            b.rect.width, b.rect.height);

        return rectA.overlaps(rectB);
    }

    public boolean overlapsRectGrid(Collider a, Collider b, GridPoint2 offset, Type type) {
        var entityData = Main.game.entityData;

        pointA.set(0, 0);
        pointB.set(0, 0);
        var posA = entityData.get(a.entity, Position.class);
        var posB = entityData.get(b.entity, Position.class);
        if (posA != null) pointA.set((int) posA.x(), (int) posA.y());
        if (posB != null) pointB.set((int) posB.x(), (int) posB.y());

        // get a relative rectangle to the grid
        rectA.set(a.rect);
        rectA.x += pointA.x + offset.x - pointB.x;
        rectA.y += pointA.y + offset.y - pointB.y;

        // get the extents for grid tiles covered by the rect
        int left   = Calc.clampInt((int) Calc.floor((float) rectA.x / b.grid.tileSize), 0, b.grid.cols);
        int bottom = Calc.clampInt((int) Calc.floor((float) rectA.y / b.grid.tileSize), 0, b.grid.rows);
        int right  = Calc.clampInt((int) Calc.floor((float) (rectA.x + rectA.width)  / b.grid.tileSize), 0, b.grid.cols);
        int top    = Calc.clampInt((int) Calc.floor((float) (rectA.y + rectA.height) / b.grid.tileSize), 0, b.grid.rows);

        // check each tile
        for (int x = left; x <= right; x++) {
            for (int y = bottom; y <= top; y++) {
                var tile = b.getGridTile(x, y);
                if (tile == null) continue;
                if (tile.solid && type == Type.solid) {
                    return true;
                }
                if (tile.climbable && type == Type.climbable) {
                    return true;
                }
            }
        }

        // no overlap detected
        return false;
    }
}
