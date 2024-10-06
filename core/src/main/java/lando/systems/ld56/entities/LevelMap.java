package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.Color;
import lando.systems.ld56.entities.components.Collider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class LevelMap extends Entity {

    public final Collider collider;

    public LevelMap(int tileSize, int cols, int rows) {
        this.collider = Collider.makeGrid(this, Collider.Type.solid, 0, 0, tileSize, cols, rows);
    }

    public void renderDebug(ShapeDrawer shapes) {
        var grid = collider.grid;
        for (int y = 0; y < grid.rows; y++) {
            for (int x = 0; x < grid.cols; x++) {
                int index = y * grid.cols + x;
                var tile = grid.tiles[index];

                int rectX = collider.origin.x + x * grid.tileSize;
                int rectY = collider.origin.y + y * grid.tileSize;
                int size = grid.tileSize;

                if (tile.climbable) {
                    shapes.rectangle(rectX, rectY, size, size, Color.LIME, 2);
                }
                if (tile.solid) {
                    shapes.rectangle(rectX, rectY, size, size, Color.SCARLET, 1);
                }
            }
        }
    }
}
