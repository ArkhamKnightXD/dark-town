package knight.arkham.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import knight.arkham.helpers.Box2DBody;
import knight.arkham.helpers.Box2DHelper;
import knight.arkham.helpers.GameDataHelper;

import static knight.arkham.helpers.AssetsHelper.loadSound;
import static knight.arkham.helpers.Constants.PIXELS_PER_METER;

public class Player extends GameObject {
    private enum AnimationState {FALLING, JUMPING, STANDING, RUNNING, DYING}
    private AnimationState actualState;
    private AnimationState previousState;
    private final TextureRegion jumpingRegion;
    private final Animation<TextureRegion> standingAnimation;
    private final Animation<TextureRegion> runningAnimation;
    private final Animation<TextureRegion> dyingAnimation;
    private float animationTimer;
    private float deadTimer;
    private boolean isMovingRight;
    private boolean isDead;
    private final Sound jumpSound;
    private final Sound deathSound;

    public Player(Rectangle bounds, World world, TextureAtlas atlas) {
        super(
            bounds, world,
            new TextureRegion(atlas.findRegion("smoking"), 0, 0, 16, 22)
        );

        previousState = AnimationState.STANDING;
        actualState = AnimationState.STANDING;

        jumpingRegion = new TextureRegion(atlas.findRegion("jumping"), 0, 0, 16, 22);

        standingAnimation = makeAnimationByRegion(atlas.findRegion("smoking"), 6, 0.2f);
        runningAnimation = makeAnimationByRegion(atlas.findRegion("walking"), 8, 0.1f);
        dyingAnimation = makeAnimationByRegion(atlas.findRegion("dying"), 8, 0.1f);

        jumpSound = loadSound("magic.wav");
        deathSound = loadSound("fall.wav");
    }

    @Override
    protected Body createBody() {

        return Box2DHelper.createBody(
            new Box2DBody(actualBounds, 10, actualWorld, this)
        );
    }

    private void movement() {

        if (Gdx.input.isKeyPressed(Input.Keys.D) && body.getLinearVelocity().x <= 6)
            applyLinealImpulse(new Vector2(6, 0));

        else if (Gdx.input.isKeyPressed(Input.Keys.A) && body.getLinearVelocity().x >= -6)
            applyLinealImpulse(new Vector2(-6, 0));

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && body.getLinearVelocity().y == 0) {

            applyLinealImpulse(new Vector2(0, 140));
            jumpSound.play();
        }
    }

    protected void childUpdate(float deltaTime) {

        getAnimationRegion(deltaTime);

        if (!isDead)
            movement();

        else {

            deadTimer += deltaTime;

            if (deadTimer >= 1) {

                isDead = false;
                deadTimer = 0;
                actualState = AnimationState.STANDING;

                spawnToPreviousCheckpoint();
            }
        }

        if (getPixelPosition().y < -100)
            spawnToPreviousCheckpoint();
    }

    public Vector2 getPixelPosition() {
        return body.getPosition().scl(PIXELS_PER_METER);
    }


    private void spawnToPreviousCheckpoint() {

        body.setLinearVelocity(0, 0);

        Vector2 savedPosition = GameDataHelper.loadGameData().position;

        body.setTransform(savedPosition, 0);
    }

    private AnimationState getCurrentAnimationState() {

        if (isDead)
            return AnimationState.DYING;

        else if (body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y < 0 && previousState == AnimationState.JUMPING))
            return AnimationState.JUMPING;

        else if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D))
            return AnimationState.RUNNING;

        else if (body.getLinearVelocity().y < 0)
            return AnimationState.FALLING;

        else
            return AnimationState.STANDING;
    }

    private void getAnimationRegion(float deltaTime) {

        actualState = getCurrentAnimationState();

        switch (actualState) {

            case JUMPING:
                actualRegion = jumpingRegion;
                break;

            case RUNNING:
                actualRegion = runningAnimation.getKeyFrame(animationTimer, true);
                break;

            case DYING:
                actualRegion = dyingAnimation.getKeyFrame(animationTimer, false);
                break;

            case FALLING:
            case STANDING:
            default:
                actualRegion = standingAnimation.getKeyFrame(animationTimer, true);
        }

        flipRegionOnXAxis(actualRegion);

        animationTimer = actualState == previousState ? animationTimer + deltaTime : 0;
        previousState = actualState;
    }

    private void flipRegionOnXAxis(TextureRegion region) {

        if ((body.getLinearVelocity().x > 0 || isMovingRight) && region.isFlipX()) {

            region.flip(true, false);
            isMovingRight = true;
        }
        if ((body.getLinearVelocity().x < 0 || !isMovingRight) && !region.isFlipX()) {

            region.flip(true, false);
            isMovingRight = false;
        }
    }

    public Vector2 getWorldPosition() {return body.getPosition();}

    public void hitByEnemy() {
        isDead = true;
        deathSound.play();
    }

    @Override
    public void dispose() {
        jumpSound.dispose();
        super.dispose();
    }
}
