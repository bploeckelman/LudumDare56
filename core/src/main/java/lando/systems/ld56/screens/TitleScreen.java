package lando.systems.ld56.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld56.assets.Anims;
import lando.systems.ld56.audio.AudioManager;
import lando.systems.ld56.ui.TitleScreenUI;
import lando.systems.ld56.utils.Calc;

import java.util.HashMap;
import java.util.Map;

public class TitleScreen extends BaseScreen {
    TitleScreenUI titleScreenUI;

    private final TextureRegion backgroundDoorClosed;
    private final TextureRegion backgroundDoorOpen;
    private final TextureRegion rampageText;
    private final TextureRegion tinySign;

    // have to use 'String' vs 'Character' key because there are two A's, left and right
    enum Letter {
        R, A1, M, P, A2, G, E;
        public Letter next() {
            switch (this) {
                case R: return A1;
                case A1: return M;
                case M: return P;
                case P: return A2;
                case A2: return G;
                case G: return E;
                case E:
                default: return R;
            }
        }
    }
    static class LetterData {
        public float x;
        public float y;
        public float yStart;
        public float yEnd;
        public float accum;
        public boolean dropped;
        public boolean dropping;
        LetterData(float x, float yStart, float yEnd) {
            this.x = x;
            this.y = yStart;
            this.yStart = yStart;
            this.yEnd = yEnd;
            this.accum = 0f;
            this.dropped = false;
            this.dropping = false;
        }
    }
    private final Map<Letter, LetterData> letterData;
    private final Map<Letter, TextureRegion> letterTextures;
    private Letter droppingLetter = Letter.R; // start with R

    private TextureRegion background;
    private float accum = 0f;
    private float signPosX = 70f;
    private float signPosY;
    private boolean showRampageFullText = false;
    private boolean showTinySign = false;

    private boolean skipButtonHeld = false;
    private float skipButtonHeldTime = 0f;
    private float skipButtonHoldDuration = 1.5f;
    private float skipOverlayAlpha = 0f;
    private float skipOverlayAlphaTarget = 0f;
    private Animation<TextureRegion> skipTimerAnim;

    private final float initialDelay = 0.3f;
    private final float letterDropInterval = 0.3f;
    private final float letterDropDuration = 1f;
    private final float doorOpenExplosionDuration = 1f;
    private final float signDropDuration = 2f;

    enum State { START, LETTER_DROP, DOOR_OPEN, SIGN_DROP, DONE }
    private State state = State.START;

    public TitleScreen() {
        super();
        titleScreenUI = new TitleScreenUI(100, 100, 200, 50, assets.font, TitleScreenUI.ButtonOrientation.HORIZONTAL);

        var atlas = assets.atlas;
        var backgroundDoorFrames = atlas.findRegions("title/title-screen-rampage-blank");
        backgroundDoorClosed = backgroundDoorFrames.get(0);
        backgroundDoorOpen = backgroundDoorFrames.get(1);
        background = backgroundDoorClosed;

        // TODO: do these find the single frame, or need findRegions().first()?
        rampageText = atlas.findRegion("title/title-screen-rampage-text");
        tinySign = atlas.findRegion("title/title-screen-tiny-text");

        letterData = new HashMap<>();
        letterData.put(Letter.R,  new LetterData(235, 800, 385)); // width 180px
        letterData.put(Letter.A1, new LetterData(415, 800, 440)); // width 109px
        letterData.put(Letter.M,  new LetterData(503, 800, 468)); // width 151px
        letterData.put(Letter.P,  new LetterData(660, 800, 480)); // width 106px
        letterData.put(Letter.A2, new LetterData(727, 800, 457)); // width 107px
        letterData.put(Letter.G,  new LetterData(845, 800, 430)); // width 138px
        letterData.put(Letter.E,  new LetterData(925, 800, 370)); // width 151px

        letterTextures = new HashMap<>();
        letterTextures.put(Letter.R,  atlas.findRegion("title/title-screen-text-r"));
        letterTextures.put(Letter.A1, atlas.findRegion("title/title-screen-text-a"));
        letterTextures.put(Letter.M,  atlas.findRegion("title/title-screen-text-m"));
        letterTextures.put(Letter.P,  atlas.findRegion("title/title-screen-text-p"));
        letterTextures.put(Letter.A2, atlas.findRegion("title/title-screen-text-aa"));
        letterTextures.put(Letter.G,  atlas.findRegion("title/title-screen-text-g"));
        letterTextures.put(Letter.E,  atlas.findRegion("title/title-screen-text-e"));

        skipTimerAnim = Anims.get(Anims.Handcrafted.TIMER_CW);

        audioManager.playMusic(AudioManager.Musics.introMusic);
    }

    @Override
    public void update(float delta) {
        var wasSkipButtonHeld = skipButtonHeld;
        skipButtonHeld = Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) || Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        // handle 'skip' button input
        if (state != State.DONE) {
            if (skipButtonHeld) {
                if (!wasSkipButtonHeld) {
                    // 'just pressed': fade in overlay
                    skipOverlayAlpha = 0f;
                    skipOverlayAlphaTarget = 0.5f;
                }

                skipButtonHeldTime += delta;
                if (skipButtonHeldTime > skipButtonHoldDuration) {
                    skipButtonHeldTime = skipButtonHoldDuration;

                    // skip to 'done' state
                    showTinySign = true;
                    showRampageFullText = true;
                    signPosY = windowCamera.viewportHeight - tinySign.getRegionHeight();
                    state = State.DONE;
                }
            } else {
                if (wasSkipButtonHeld) {
                    // 'just released': fade out overlay
                    skipOverlayAlpha = 0.5f;
                    skipOverlayAlphaTarget = 0.0f;
                }
            }
        }
        // this catches some weird edge case
        if (state == State.DONE) {
            skipOverlayAlphaTarget = 0f;
        }
        skipOverlayAlpha = Calc.approach(skipOverlayAlpha, skipOverlayAlphaTarget, delta * 2);

        switch (state) {
            case START: {
                accum += delta;
                if (accum >= initialDelay) {
                    accum = 0f;
                    droppingLetter = Letter.R;
                    letterData.get(droppingLetter).dropping = true;
                    state = State.LETTER_DROP;
                }
            } break;
            case LETTER_DROP: {
                // christ, I should have just used tween engine
                accum += delta;

                // drop each letter in order
                if (accum >= letterDropInterval) {
                    accum = 0f;
                    droppingLetter = droppingLetter.next();
                    var letter = letterData.get(droppingLetter);
                    if (!letter.dropped) {
                        letter.dropping = true;
                        letter.accum = 0f;
                    }
                }

                // lerp dropping letters from start to end y-pos
                var allLettersDropped = true;
                for (var letterType : Letter.values()) {
                    var letter = letterData.get(letterType);
                    if (!letter.dropping) continue;

                    letter.accum += delta;
                    var interp = Calc.clampf(letter.accum / letterDropDuration, 0f, 1f);
                    interp = Interpolation.bounceOut.apply(interp);
                    letter.y = letter.yStart - interp * (letter.yStart - letter.yEnd);
                    if (letter.y <= letter.yEnd) {
                        letter.y = letter.yEnd;
                    }

                    // set letter done
                    if (letter.y == letter.yEnd) {
                        letter.dropped = true;
                        letter.dropping = false;
                    }

                    if (!letter.dropped) {
                        allLettersDropped = false;
                    }
                }

                // move to next state
                if (allLettersDropped) {
                    background = backgroundDoorOpen;
                    showRampageFullText = true;
                    // NOTE: pv both effect/sound should last roughly 'doorOpenExplosionDuration' seconds
                    // TODO: trigger an explosion particle effect here
                    // TODO: pv - explosion sound
                    // TODO: if time for fancy, make 'rampage full text' 'expand' in to full size, in place, and send individual letters flying offscreen
                    state = State.DOOR_OPEN;
                }
            } break;
            case DOOR_OPEN: {
                accum += delta;
                if (accum > doorOpenExplosionDuration) {
                    accum = 0f;
                    state = State.SIGN_DROP;
                }
            } break;
            case SIGN_DROP: {
                showTinySign = true;

                accum += delta;
                if (accum < signDropDuration) {
                    var interp = accum / signDropDuration;
                    var startY = windowCamera.viewportHeight + tinySign.getRegionHeight();
                    var endY = windowCamera.viewportHeight - tinySign.getRegionHeight();
                    signPosY = Interpolation.bounceOut.apply(startY, endY, interp);
                } else {
                    state = State.DONE;
                }
            } break;
            case DONE: {
//                if (!exitingScreen && Gdx.input.justTouched()){
//                    exitingScreen = game.setScreen(new IntroScreen());
//                }

                vec3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                worldCamera.unproject(vec3);
                titleScreenUI.update(vec3.x, vec3.y);
            } break;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.DARK_GRAY);

        var camera = windowCamera;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            batch.draw(background, 0, 0, camera.viewportWidth, camera.viewportHeight);

            if (state == State.LETTER_DROP) {
                for (var letterType : Letter.values()) {
                    var letter = letterData.get(letterType);
                    var texture = letterTextures.get(letterType);

                    // undropped letters stay offscreen, dropped letters stay in final position, dropping letter moves
                    var letterX = letter.x;
                    var letterY = letter.yStart;
                    if (letter.dropping) {
                        letterY = letter.y;
                    } else if (letter.dropped) {
                        letterY = letter.yEnd;
                    }

                    batch.draw(texture, letterX, letterY);
                }
            }

            if (showRampageFullText) {
                batch.draw(rampageText, 220, 360);
            }

            if (showTinySign) {
                batch.draw(tinySign, signPosX, signPosY);
            }

            if (state == State.DONE) {
                titleScreenUI.draw(batch);
            }

            // dim overlay for skip button, always draw because of fancy fade in/out
            batch.setColor(0f, 0f, 0f, skipOverlayAlpha);
            batch.draw(assets.pixelRegion, 0, 0, camera.viewportWidth, camera.viewportHeight);
            batch.setColor(Color.WHITE);

            if (skipButtonHeld && state != State.DONE) {
                var interp = Calc.clampf(skipButtonHeldTime / skipButtonHoldDuration, 0f, 1f);
                var frames = Anims.Handcrafted.TIMER_CW.frames;

                // this crap is to account for 5 frames representing 0 and quarters
                var keyframe = frames.get(0);
                if      (interp >= 0.95f)  keyframe = frames.get(4);
                else if (interp >= 0.75f) keyframe = frames.get(3);
                else if (interp >= 0.5f)  keyframe = frames.get(2);
                else if (interp >= 0.25f) keyframe = frames.get(1);

                var scale = 1f;
                var yPos = 50f;
                var width = scale * keyframe.getRegionWidth();
                var height = scale * keyframe.getRegionHeight();
                batch.draw(keyframe, (windowCamera.viewportWidth - width) / 2f, yPos, width, height);

                assets.font.getData().setScale(1f);
                assets.layout.setText(assets.font, "Hold to Skip", Color.WHITE, camera.viewportWidth, Align.center, false);
                assets.font.draw(batch, assets.layout, 0, yPos + height + assets.layout.height + 10f);
                assets.font.getData().setScale(1f);
            }
        }
        batch.end();
    }
}
