package lando.systems.ld56.entities.components;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.Main;
import lando.systems.ld56.entities.Entity;
import lando.systems.ld56.entities.XRayable;

public class XRayRender extends Component{

    public final Texture texture;

    public final FrameBuffer fbo;
    public final Texture fboTexture;
    public final OrthographicCamera objectCamera;
    public final Rectangle bounds;
    public final OrthographicCamera worldCamera;
    private final Vector2 noiseOffset;


    public XRayRender(Entity entity, Texture coveredTexture, Texture xrayTexture, Rectangle bounds, OrthographicCamera worldCamera) {
        super(entity, XRayRender.class);
        this.texture = coveredTexture;
        this.bounds = bounds;
        this.worldCamera = worldCamera;
        this.noiseOffset = new Vector2(MathUtils.random(), MathUtils.random());

        // TODO throw expection is images arent the same aspect ratio

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int)bounds.width, (int)bounds.height, true);
        fboTexture = fbo.getColorBufferTexture();
        objectCamera = new OrthographicCamera(fbo.getWidth(), fbo.getHeight());
        objectCamera.setToOrtho(false, fbo.getWidth(), fbo.getHeight());

    }

    public void update(float dt) {

    }

    Vector3 tempVector3 = new Vector3();
    public void renderXrayAlpha(SpriteBatch batch) {
        objectCamera.position.set(bounds.x + bounds.width/2f, bounds.y + bounds.height/2f, 0);
        objectCamera.update();
        var origProjection = batch.getProjectionMatrix();
        // have a way to draw this on the buildings

        batch.setProjectionMatrix(objectCamera.combined);
        fbo.begin();
        batch.setColor(Color.WHITE);
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        batch.begin();

        if (entity instanceof XRayable) {
            ((XRayable) entity).renderMask(batch);
        } else {
            Gdx.app.error("X-Ray render", "Need to implement XRayable");
        }
        batch.end();

        batch.setProjectionMatrix(origProjection);
        batch.setShader(null);
        fbo.end();
    }

    public void render(SpriteBatch batch, Color tintColor) {
        ShaderProgram shader = Main.game.assets.xRayShader;
        batch.setColor(tintColor);
        batch.setShader(shader);

        shader.setUniformf("u_noiseOffset", noiseOffset);
        shader.setUniformf("u_size", bounds.width, bounds.height);

        Main.game.assets.noiseTexture.bind(2);
        shader.setUniformi("u_noise", 2);
        fboTexture.bind(1);
        shader.setUniformi("u_mask", 1);
        texture.bind(0);
        shader.setUniformi("u_texture", 0);

        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);

        batch.setShader(null);

        batch.setColor(1, 1, 1, .3f);
//        batch.draw(fboTexture, bounds.x, bounds.y+bounds.height, bounds.width, -bounds.height);
        batch.setColor(Color.WHITE);

    }
}
