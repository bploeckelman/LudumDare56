package lando.systems.ld56.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import lando.systems.ld56.Config;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Assets implements Disposable {

    //  TEST THINGS GO HERE


    // END TEST THINGS - REMOVE AT RELEASE

    // sound names

    public enum Load { ASYNC, SYNC }

    public final Preferences preferences;

    public boolean initialized;

    public SpriteBatch batch;
    public ShapeDrawer shapes;
    public GlyphLayout layout;
    public AssetManager mgr;
    public TextureAtlas atlas;
    public I18NBundle strings;

    public BitmapFont font;
    public BitmapFont fontChrustyLg;
    public BitmapFont fontChrustyMd;
    public BitmapFont fontChrustySm;

    public Texture pixel;
    public Texture gdx;
    public Texture noiseTexture;

    public ShaderProgram xRayShader;

    public TextureRegion pixelRegion;
    public TextureRegion fuzzyCircle;

    public static class SoundRes {
        public static final String Coin = "audio/sounds/coin.ogg";
        // todo pete sound when building takes damage
        public static final String StructureDamage = "audio/sounds/structureDamage.ogg";
        // todo pete rat attack sound
        public static final String RatAttack = "audio/sounds/structureDamage.ogg";
        public static final String Collapse1 = "audio/sounds/collapse1.ogg";
    }

    public Sound coin;
    public Sound structureDamage;
    public Sound ratAttack;
    public Sound collapse1;

    public Music mainMusic;
    public Music introMusic;

    public Assets() {
        this(Load.SYNC);
    }

    public Assets(Load load) {
        initialized = false;

        preferences = Gdx.app.getPreferences("ld56-lando-systems");

        // create a single pixel texture and associated region
        var pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        {
            pixmap.setColor(Color.WHITE);
            pixmap.drawPixel(0, 0);
            pixmap.drawPixel(1, 0);
            pixmap.drawPixel(0, 1);
            pixmap.drawPixel(1, 1);
            pixel = new Texture(pixmap);
        }
        pixmap.dispose();
        pixelRegion = new TextureRegion(pixel);

        batch = new SpriteBatch();
        shapes = new ShapeDrawer(batch, pixelRegion);
        layout = new GlyphLayout();

        mgr = new AssetManager();
        {
            mgr.load("sprites/sprites.atlas", TextureAtlas.class);
            mgr.load("i18n/strings", I18NBundle.class);

            mgr.load("fonts/outfit-medium-20px.fnt", BitmapFont.class);
            mgr.load("fonts/outfit-medium-40px.fnt", BitmapFont.class);
            mgr.load("fonts/outfit-medium-80px.fnt", BitmapFont.class);

            mgr.load("images/pixel.png", Texture.class);
            mgr.load("images/libgdx.png", Texture.class);
            mgr.load("images/noise.png", Texture.class);

            mgr.load("images/structures/building-bacteria-front_00.png", Texture.class);
            mgr.load("images/structures/building-bacteria-back_00.png", Texture.class);
            mgr.load("images/structures/building-brick-front_00.png", Texture.class);
            mgr.load("images/structures/building-brick-back_00.png", Texture.class);

            mgr.load("audio/music/intro-music.ogg", Music.class);
            mgr.load("audio/music/main-music.ogg", Music.class);

            mgr.load(SoundRes.Coin, Sound.class);
            mgr.load(SoundRes.StructureDamage, Sound.class);
            mgr.load(SoundRes.RatAttack, Sound.class);
            mgr.load(SoundRes.Collapse1, Sound.class);
        }

        if (load == Load.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1;

        atlas = mgr.get("sprites/sprites.atlas");
        strings = mgr.get("i18n/strings", I18NBundle.class);
        gdx = mgr.get("images/libgdx.png");
        pixel = mgr.get("images/pixel.png");
        noiseTexture = mgr.get("images/noise.png");
        noiseTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        noiseTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        fuzzyCircle = atlas.findRegion("misc/fuzzy-circle");

        var ttfLg = new FreeTypeFontGenerator.FreeTypeFontParameter() {{ size = 80; }};
        var ttfMd = new FreeTypeFontGenerator.FreeTypeFontParameter() {{ size = 40; }};
        var ttfSm = new FreeTypeFontGenerator.FreeTypeFontParameter() {{ size = 20; }};
        var ttf = new FreeTypeFontGenerator(Gdx.files.internal("fonts/good_brush.ttf"));
        fontChrustyLg = ttf.generateFont(ttfLg);
        fontChrustyMd = ttf.generateFont(ttfMd);
        fontChrustySm = ttf.generateFont(ttfSm);
        ttf.dispose();

        font = fontChrustyMd;
        font.setUseIntegerPositions(false);

        // get texture regions from atlas...
        var guy = atlas.findRegion("misc/guy");
        var guyFrames = guy.split(16, 16);
        var guyIdle = new Array<TextureRegion>(); // 3
        var guyRun = new Array<TextureRegion>(); // 7
        var guyJump = new Array<TextureRegion>(); // 6
        for (int i = 0; i < guyFrames[0].length; i++) {
            if      (i < 3)  guyIdle.add(guyFrames[0][i]);
            else if (i < 10) guyRun.add(guyFrames[0][i]);
            else if (i < 16) guyJump.add(guyFrames[0][i]);
        }

        // Audio
        introMusic = mgr.get("audio/music/intro-music.ogg", Music.class);
        mainMusic = mgr.get("audio/music/main-music.ogg", Music.class);

        coin = mgr.get(SoundRes.Coin, Sound.class);
        structureDamage = mgr.get(SoundRes.StructureDamage, Sound.class);
        ratAttack = mgr.get(SoundRes.RatAttack, Sound.class);
        collapse1 = mgr.get(SoundRes.Collapse1, Sound.class);

        // initialize static asset classes
        Icons.init(this);
        Patches.init(this);
        Anims.init(this);
        Particles.init(this);
        Structures.init(this);
        Transition.init();

        xRayShader = loadShader("shaders/default.vert", "shaders/xray.frag");

        initialized = true;
        return 1;
    }

    @Override
    public void dispose() {
        mgr.dispose();
        batch.dispose();
        pixel.dispose();
        fontChrustyLg.dispose();
        fontChrustyMd.dispose();
        fontChrustySm.dispose();
    }

    public static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        var shaderProgram = new ShaderProgram(
            Gdx.files.internal(vertSourcePath),
            Gdx.files.internal(fragSourcePath));
        var log = shaderProgram.getLog();

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("LoadShader", "compilation failed:\n" + log);
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + log);
        } else if (Config.Debug.logging) {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log: " + log);
        }

        return shaderProgram;
    }
}
