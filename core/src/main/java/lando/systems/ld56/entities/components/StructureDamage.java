package lando.systems.ld56.entities.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld56.Config;
import lando.systems.ld56.Main;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.entities.Entity;
import lando.systems.ld56.entities.Player;
import lando.systems.ld56.entities.Structure;
import lando.systems.ld56.utils.Calc;
import lando.systems.ld56.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class StructureDamage extends Entity {
    public final Structure structure;
    public final int rows;
    public final int columns;

    public float[][] damage;
    public Collider[][] colliders;

    private Rectangle bounds;
    private float tileWidth;
    private float tileHeight;

    public StructureDamage(Structure structure, int rows, int columns) {
        this.structure = structure;
        this.rows = rows;
        this.columns = columns;
        this.damage = new float[columns][rows];
        setBounds(structure.bounds);
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
        this.tileWidth = bounds.width / columns;
        this.tileHeight = bounds.height / rows;

        this.colliders = new Collider[columns][rows];
        float dy = bounds.getY();
        for (int y = 0; y < rows; y++) {
            float dx = bounds.getX();
            for (int x = 0; x < columns; x++) {
                this.colliders[x][y] = Collider.makeRect(this, Collider.Type.structure, (int)dx, (int)dy, (int)tileWidth, (int)tileHeight);
                dx += tileWidth;
            }
            dy += tileHeight;
        }
    }

    public void setMinDamageForAllTiles(float value) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                damage[x][y] = Math.max(damage[x][y], value);
                Main.game.entityData.remove(this.colliders[x][y], Collider.class);
            }
        }
    }

    public boolean applyDamage(Player player, int posX, int posY) {
        int xOffset = (int)((posX - bounds.getX()) / tileWidth);
        int yOffset = (int)((posY - bounds.getY()) / tileHeight);
        // calc offset instead of using bounds
        if (xOffset < 0 || xOffset >= columns || yOffset < 0 || yOffset >= rows) { return false; }

        int xMod = player.animator.facing;
        int damageOffset = xOffset - xMod;
        while (!Config.Debug.free_attack_mode && Calc.inRange(damageOffset, 0, columns)) {
            if (this.damage[damageOffset][yOffset] < 1f) {
                Main.playSound(AudioManager.Sounds.squelch);
                Gdx.app.log("Generic Squelch", "true");
                return false;
            }
            damageOffset -= xMod;
        }

        if (this.damage[xOffset][yOffset] >= 1f) {
            Main.playSound(AudioManager.Sounds.thud);
            Gdx.app.log("Thud Sound", "true");
            return false;
        }

        // get amount of damage from player
        this.damage[xOffset][yOffset] = Calc.clampf(this.damage[xOffset][yOffset] + player.attackStrength, 0f, 1f);
        player.successfulHitEffect(posX, posY);
        switch(structure.structureType) {
            case BACTERIA_A:
                Main.game.audioManager.playSound(AudioManager.Sounds.squelch);
                Gdx.app.log("Hit Bacteria", "true");
                break;
                case BACTERIA_B:
                Main.game.audioManager.playSound(AudioManager.Sounds.squelch);
                Gdx.app.log("Hit Bacteria", "true");
                break;
                case BACTERIA_C:
                Main.game.audioManager.playSound(AudioManager.Sounds.squelch);
                Gdx.app.log("Hit Bacteria", "true");
                break;
            case HOUSE_A:
                Main.game.audioManager.playSound(AudioManager.Sounds.impact);
                break;
            case HOUSE_B:
                Main.game.audioManager.playSound(AudioManager.Sounds.impact);
                break;
            default:
                Main.game.audioManager.playSound(AudioManager.Sounds.thud);
                break;
        }

        if (this.damage[xOffset][yOffset] >= 1f) { player.successfulDestroyEffect(posX, posY); }
        return true;
    }

    public void renderDebug(SpriteBatch batch, ShapeDrawer shapes) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                colliders[x][y].render(shapes);
            }
        }
    }

    public float getDamagePercent() {
        float totalHealth = 0;
        float totalDamage = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                totalHealth += 1f;
                totalDamage += damage[x][y];
            }
        }
        return totalDamage/totalHealth;
    }

    private final Color damageColor = new Color(Color.RED);
    public void renderMask(SpriteBatch batch) {
        float dy = (float)bounds.getY();
        for (int y = 0; y < rows; y++) {
            float dx = (float)bounds.getX();
            for (int x = 0; x < columns; x++) {
                float tileDamage = this.damage[x][y];
                if (tileDamage > 0f) {
                    batch.setColor(damageColor.set(1, 1, 1, Calc.clampf(tileDamage, 0, 1)));
                    batch.draw(Main.game.assets.pixel, dx, dy, tileWidth, tileHeight);
                }
                dx += tileWidth;
            }
            dy += tileHeight;
        }

        batch.setColor(Color.WHITE);
    }
}
