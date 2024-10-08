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
    public ShaderProgram debrisShader;

    public TextureRegion pixelRegion;
    public TextureRegion fuzzyCircle;

    public static class SoundRes {
        public static final String Coin = "audio/sounds/coin.ogg";
        public static final String StructureDamage = "audio/sounds/structureDamage.ogg";
        public static final String RatAttack = "audio/sounds/structureDamage.ogg";
        public static final String Collapse1 = "audio/sounds/collapse1.ogg";
        public static final String Impact1 = "audio/sounds/impact1.ogg";
        public static final String Impact2 = "audio/sounds/impact2.ogg";
        public static final String Impact3 = "audio/sounds/impact3.ogg";
        public static final String Impact4 = "audio/sounds/impact4.ogg";
        public static final String LevelComplete1 = "audio/sounds/levelcomplete.ogg";
        public static final String LevelCompleteNarration = "audio/sounds/levelcompletenarration" +
            ".ogg";
        public static final String Swipe1 = "audio/sounds/swipe1.ogg";
        public static final String Squelch1 = "audio/sounds/squelch1.ogg";
        public static final String Squelch2 = "audio/sounds/squelch2.ogg";
        public static final String Squelch3 = "audio/sounds/squelch3.ogg";
        public static final String CollectFollower1 = "audio/sounds/collectFollower1.ogg";
        public static final String Boing1 = "audio/sounds/boing1.ogg";
        public static final String Jump1 = "audio/sounds/jump1.ogg";
        public static final String Thud1 = "audio/sounds/thud1.ogg";
        public static final String Intro1 = "audio/sounds/intro1.ogg";
        public static final String Outro1 = "audio/sounds/outro1.ogg";
        public static final String Ant1 = "audio/sounds/ant1.ogg";
        public static final String Rat1 = "audio/sounds/rat1.ogg";
        public static final String Phage1 = "audio/sounds/phage1.ogg";
        public static final String Snake1 = "audio/sounds/snake1.ogg";
        public static final String Tardigrade1 = "audio/sounds/tardigrade1.ogg";
        public static final String Nematode1 = "audio/sounds/nematode1.ogg";
        public static final String Earthworm1 = "audio/sounds/earthworm1.ogg";
        public static final String Parasite1 = "audio/sounds/parasite1.ogg";
        public static final String Mario1 = "audio/sounds/mario1.ogg";
        public static final String Luigi1 = "audio/sounds/luigi1.ogg";
    }

    public Sound coin;
    public Sound structureDamage;
    public Sound ratAttack;
    public Sound collapse1;
    public Sound impact1;
    public Sound impact2;
    public Sound impact3;
    public Sound impact4;
    public Sound levelComplete1;
    public Sound levelCompleteNarration;
    public Sound swipe1;
    public Sound squelch1;
    public Sound squelch2;
    public Sound squelch3;
    public Sound collectFollower1;
    public Sound boing1;
    public Sound jump1;
    public Sound thud1;
    public Sound intro1;
    public Sound outro1;
    public Sound tardigrade1;
    public Sound nematode1;
    public Sound rat1;
    public Sound ant1;
    public Sound phage1;
    public Sound snake1;
    public Sound earthworm1;
    public Sound parasite1;
    public Sound mario1;
    public Sound luigi1;

    public Music mainMusic;
    public Music introMusic;

    public InputPrompts inputPrompts;


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

//            mgr.load("images/structures/building-bacteria-front_00.png", Texture.class);
//            mgr.load("images/structures/building-bacteria-back_00.png", Texture.class);
//            mgr.load("images/structures/building-brick-front_upscale_00.png", Texture.class);
//            mgr.load("images/structures/building-brick-back_upscale_00.png", Texture.class);

            mgr.load("audio/music/intro-music.ogg", Music.class);
            mgr.load("audio/music/main-music.ogg", Music.class);

            mgr.load(SoundRes.Coin, Sound.class);
            mgr.load(SoundRes.StructureDamage, Sound.class);
            mgr.load(SoundRes.RatAttack, Sound.class);
            mgr.load(SoundRes.Collapse1, Sound.class);
            mgr.load(SoundRes.Impact1, Sound.class);
            mgr.load(SoundRes.Impact2, Sound.class);
            mgr.load(SoundRes.Impact3, Sound.class);
            mgr.load(SoundRes.Impact4, Sound.class);
            mgr.load(SoundRes.LevelComplete1, Sound.class);
            mgr.load(SoundRes.LevelCompleteNarration, Sound.class);
            mgr.load(SoundRes.Swipe1, Sound.class);
            mgr.load(SoundRes.Squelch1, Sound.class);
            mgr.load(SoundRes.Squelch2, Sound.class);
            mgr.load(SoundRes.Squelch3, Sound.class);
            mgr.load(SoundRes.CollectFollower1, Sound.class);
            mgr.load(SoundRes.Boing1, Sound.class);
            mgr.load(SoundRes.Jump1, Sound.class);
            mgr.load(SoundRes.Thud1, Sound.class);
            mgr.load(SoundRes.Intro1, Sound.class);
            mgr.load(SoundRes.Outro1, Sound.class);
            mgr.load(SoundRes.Tardigrade1, Sound.class);
            mgr.load(SoundRes.Nematode1, Sound.class);
            mgr.load(SoundRes.Rat1, Sound.class);
            mgr.load(SoundRes.Ant1, Sound.class);
            mgr.load(SoundRes.Phage1, Sound.class);
            mgr.load(SoundRes.Snake1, Sound.class);
            mgr.load(SoundRes.Earthworm1, Sound.class);
            mgr.load(SoundRes.Thud1, Sound.class);
            mgr.load(SoundRes.Parasite1, Sound.class);
            mgr.load(SoundRes.Mario1, Sound.class);
            mgr.load(SoundRes.Luigi1, Sound.class);

            for (Structures.Type type : Structures.Type.values()){
                mgr.load(type.externalTextureName, Texture.class);
                mgr.load(type.internalTextureName, Texture.class);
            }
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
//        var ttf = new FreeTypeFontGenerator(Gdx.files.internal("fonts/airstrip.ttf"));
        var ttf = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silvertones.ttf"));
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
        impact1 = mgr.get(SoundRes.Impact1, Sound.class);
        impact2 = mgr.get(SoundRes.Impact2, Sound.class);
        impact3 = mgr.get(SoundRes.Impact3, Sound.class);
        impact4 = mgr.get(SoundRes.Impact4, Sound.class);
        levelComplete1 = mgr.get(SoundRes.LevelComplete1, Sound.class);
        levelCompleteNarration = mgr.get(SoundRes.LevelCompleteNarration, Sound.class);
        swipe1 = mgr.get(SoundRes.Swipe1, Sound.class);
        squelch1 = mgr.get(SoundRes.Squelch1, Sound.class);
        squelch2 = mgr.get(SoundRes.Squelch2, Sound.class);
        squelch3 = mgr.get(SoundRes.Squelch3, Sound.class);
        collectFollower1 = mgr.get(SoundRes.CollectFollower1, Sound.class);
        boing1 = mgr.get(SoundRes.Boing1, Sound.class);
        jump1 = mgr.get(SoundRes.Jump1, Sound.class);
        thud1 = mgr.get(SoundRes.Thud1, Sound.class);
        intro1 = mgr.get(SoundRes.Intro1, Sound.class);
        outro1 = mgr.get(SoundRes.Outro1, Sound.class);
        tardigrade1 = mgr.get(SoundRes.Tardigrade1, Sound.class);
        nematode1 = mgr.get(SoundRes.Nematode1, Sound.class);
        rat1 = mgr.get(SoundRes.Rat1, Sound.class);
        ant1 = mgr.get(SoundRes.Ant1, Sound.class);
        phage1 = mgr.get(SoundRes.Phage1, Sound.class);
        snake1 = mgr.get(SoundRes.Snake1, Sound.class);
        earthworm1 = mgr.get(SoundRes.Earthworm1, Sound.class);
        parasite1 = mgr.get(SoundRes.Parasite1, Sound.class);
        mario1 = mgr.get(SoundRes.Mario1, Sound.class);
        luigi1 = mgr.get(SoundRes.Luigi1, Sound.class);


        // initialize static asset classes
        Icons.init(this);
        Patches.init(this);
        Anims.init(this);
        Particles.init(this);
        Structures.init(this);
        Transition.init();

        inputPrompts = new InputPrompts(this);


        xRayShader = loadShader("shaders/default.vert", "shaders/xray.frag");
        debrisShader = loadShader("shaders/default.vert", "shaders/debris.frag");

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
