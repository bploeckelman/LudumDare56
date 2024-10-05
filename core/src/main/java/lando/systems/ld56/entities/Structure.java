package lando.systems.ld56.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld56.assets.Assets;
import lando.systems.ld56.utils.RectangleI;

public class Structure extends Entity {

    public TextureRegion internals;
    public TextureRegion externals;
    public RectangleI bounds;

    public Structure(Assets assets, int centerX, int bottomY) {
        this.internals = assets.atlas.findRegions("structures/building-brick-back").first();
        this.externals = assets.atlas.findRegions("structures/building-brick-front").first();

        var scale = 2;
        var width = scale * internals.getRegionWidth();
        var height = scale * internals.getRegionHeight();
        this.bounds = new RectangleI(centerX - width / 2, bottomY, width, height);
    }

    public void render(SpriteBatch batch) {
        batch.draw(internals, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.draw(externals, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
