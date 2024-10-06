package lando.systems.ld56.entities.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld56.entities.Structure;
import lando.systems.ld56.utils.RectangleI;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class StructureDamage {
    public final Structure structure;
    public final int rows;
    public final int columns;

    private final RectangleI bounds;

    public StructureDamage(Structure structure, int rows, int columns) {
        this.structure = structure;
        this.rows = rows;
        this.columns = columns;

        this.bounds = structure.bounds;
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        float tileWidth = (float)bounds.width / columns;
        float tileHeight = (float)bounds.height / rows;

        float dx = (float)bounds.getX();
        for (int x = 0; x < columns; x++) {
            float dy = (float)bounds.getY();
            for (int y = 0; y < rows; y++) {
                shapes.rectangle(dx, dy, tileWidth, tileHeight, Color.GOLD, 1);
                dy += tileHeight;
            }
            dx += tileWidth;
        }
    }
}
