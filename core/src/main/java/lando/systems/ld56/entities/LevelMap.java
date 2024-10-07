package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.Color;
import lando.systems.ld56.entities.components.Collider;
import lando.systems.ld56.utils.Calc;
import lando.systems.ld56.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;
import text.formic.Stringf;

public class LevelMap extends Entity {

    public final int tileSize;
    public final Collider solidCollider;
    public final Collider climbableCollider;

    public LevelMap(int tileSize, int cols, int rows) {
        this.tileSize = tileSize;
        this.solidCollider = Collider.makeGrid(this, Collider.Type.solid, 0, 0, tileSize, cols, rows);
        this.climbableCollider = Collider.makeGrid(this, Collider.Type.climbable, 0, 0, tileSize, cols, rows);
    }

    public void setClimbable(Structure structure) {
        var grid = solidCollider.grid;
        var bounds = structure.bounds;

        // convert bounds px to tile grid coords edges
        var left   = Calc.clampInt((int) Calc.floor((float) bounds.x / grid.tileSize), 0, grid.cols);
        var bottom = Calc.clampInt((int) Calc.floor((float) bounds.y / grid.tileSize), 0, grid.rows);
        var right  = Calc.clampInt((int) Calc.floor((float) (bounds.x + bounds.width)  / grid.tileSize), 0, grid.cols);
        var top    = Calc.clampInt((int) Calc.floor((float) (bounds.y + bounds.height) / grid.tileSize), 0, grid.rows);
        var w = right - left;
        var h = top - bottom;

        // set left and right edges climbable
        var climbable = true;
        climbableCollider.setGridTilesClimbable(left, bottom, 1, h, climbable);
        climbableCollider.setGridTilesClimbable(right - 1, bottom, 1, h, climbable);

        // TODO(brian): not really the right place for this, but the tops of structures should probably be walkable?
        var solid = true;
        solidCollider.setGridTilesSolid(left + 1, top - 1, w - 1, 1, solid);
    }

    public void removeStructure(Structure structure) {
        var grid = solidCollider.grid;
        var bounds = structure.bounds;

        // convert bounds px to tile grid coords edges
        var left   = Calc.clampInt((int) Calc.floor((float) bounds.x / grid.tileSize), 0, grid.cols);
        var bottom = Calc.clampInt((int) Calc.floor((float) bounds.y / grid.tileSize), 0, grid.rows);
        var right  = Calc.clampInt((int) Calc.floor((float) (bounds.x + bounds.width)  / grid.tileSize), 0, grid.cols);
        var top    = Calc.clampInt((int) Calc.floor((float) (bounds.y + bounds.height) / grid.tileSize), 0, grid.rows);
        var w = right - left;
        var h = top - bottom;

        // set left and right edges climbable
        var climbable = false;
        climbableCollider.setGridTilesClimbable(left, bottom, 1, h, climbable);
        climbableCollider.setGridTilesClimbable(right - 1, bottom, 1, h, climbable);

        // TODO(brian): not really the right place for this, but the tops of structures should probably be walkable?
        var solid = false;
        solidCollider.setGridTilesSolid(left + 1, top - 1, w - 1, 1, solid);
    }

    public void setBorderSolid() {
        int w = solidCollider.grid.cols - 1;
        int h = solidCollider.grid.rows - 1;

        var solid = true;
        solidCollider.setGridTilesSolid(0, 0, w, 1, solid);
        solidCollider.setGridTilesSolid(0, 0, 1, h, solid);
        solidCollider.setGridTilesSolid(0, h, w, 1, solid);
        solidCollider.setGridTilesSolid(w, 0, 1, h, solid);
    }

    public void renderDebug(ShapeDrawer shapes) {
        var climbableGrid = climbableCollider.grid;
        for (int y = 0; y < climbableGrid.rows; y++) {
            for (int x = 0; x < climbableGrid.cols; x++) {
                int index = y * climbableGrid.cols + x;
                var tile = climbableGrid.tiles[index];

                int rectX = climbableCollider.origin.x + x * climbableGrid.tileSize;
                int rectY = climbableCollider.origin.y + y * climbableGrid.tileSize;
                int size = climbableGrid.tileSize;

                if (tile.climbable) {
                    shapes.rectangle(rectX, rectY, size, size, Color.LIME, 2);
                }
            }
        }
        var solidGrid = solidCollider.grid;
        for (int y = 0; y < solidGrid.rows; y++) {
            for (int x = 0; x < solidGrid.cols; x++) {
                int index = y * solidGrid.cols + x;
                var tile = solidGrid.tiles[index];

                int rectX = solidCollider.origin.x + x * solidGrid.tileSize;
                int rectY = solidCollider.origin.y + y * solidGrid.tileSize;
                int size = solidGrid.tileSize;

                if (tile.solid) {
                    shapes.rectangle(rectX, rectY, size, size, Color.SCARLET, 1);
                }
            }
        }
    }

    public void setRowSolid(int y) {
        var collider = solidCollider;
        if (y < 0 || y > collider.grid.rows) {
            Utils.log("LevelMap", Stringf.format("Unable to set row solid, %d out of bounds: rows %d", y, collider.grid.rows));
            return;
        }
        collider.setGridTilesSolid(0, y, collider.grid.cols, 1, true);
    }
}
