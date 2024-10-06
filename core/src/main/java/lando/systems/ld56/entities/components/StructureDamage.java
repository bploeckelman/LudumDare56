package lando.systems.ld56.entities.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld56.Main;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.Structure;
import lando.systems.ld56.utils.Calc;
import lando.systems.ld56.utils.RectangleI;
import lando.systems.ld56.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class StructureDamage {
    public final Structure structure;
    public final int rows;
    public final int columns;

    private float[][] damage;

    private RectangleI bounds;
    private float tileWidth;
    private float tileHeight;

    public StructureDamage(Structure structure, int rows, int columns) {
        this.structure = structure;
        this.rows = rows;
        this.columns = columns;
        this.damage = new float[columns][rows];

        setBounds(structure.bounds);
    }

    public void setBounds(RectangleI bounds) {
        this.bounds = bounds;
        this.tileWidth = (float)bounds.width / columns;
        this.tileHeight = (float)bounds.height / rows;
    }

    public boolean applyDamage(Player player, int posX, int posY) {
        int xOffset = (int)((posX - bounds.getX()) / tileWidth);
        int yOffset = (int)((posY - bounds.getY()) / tileHeight);

        // calc offset instead of using bounds
        if (xOffset < 0 || xOffset >= columns || yOffset < 0 || yOffset >= rows) { return false; }

        int xMod = player.animator.facing;
        int damageOffset = xOffset - xMod;
        while (Calc.inRange(damageOffset, 0, columns)) {
            if (this.damage[damageOffset][yOffset] < 1f) {
                return false;
            }
            damageOffset -= xMod;
        }

        // get amount of damage from player
        this.damage[xOffset][yOffset] += player.damage;
        return true;
    }

    private Color damageColor = new Color(Color.RED);
    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        float dy = (float)bounds.getY();
        for (int y = 0; y < rows; y++) {
            float dx = (float)bounds.getX();
            for (int x = 0; x < columns; x++) {
                float tileDamage = this.damage[x][y];
                if (tileDamage > 0f) {
                    shapes.rectangle(dx, dy, tileWidth, tileHeight, Color.GOLD, 1);
                    batch.setColor(damageColor.set(1, 0, 0, Calc.clampf(tileDamage, 0, 1)));
                    batch.draw(Main.game.assets.pixel, dx, dy, tileWidth, tileHeight);
                }
                dx += tileWidth;
            }
            dy += tileHeight;
        }

        batch.setColor(Color.WHITE);
    }
}
