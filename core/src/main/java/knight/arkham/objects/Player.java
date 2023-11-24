package knight.arkham.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class Player extends GameObject {
    private enum AnimationState {FALLING, JUMPING, STANDING, RUNNING}
    private AnimationState actualState;
    private AnimationState previousState;
    private final TextureRegion jumpingRegion;
    private final Animation<TextureRegion> standingAnimation;
    private final Animation<TextureRegion> runningAnimation;
    private float animationTimer;
    private boolean isMovingRight;

    public Player(Rectangle bounds, World world, TextureAtlas atlas) {
        super(
            bounds, world,
            new TextureRegion(atlas.findRegion("smoking"), 0, 0, 16, 22)
        );

        previousState = AnimationState.STANDING;
        actualState = AnimationState.STANDING;

        standingAnimation = makeAnimationByRegion(atlas.findRegion("smoking"), 6, 0.2f);

        jumpingRegion = new TextureRegion(atlas.findRegion("jumping"), 0, 0, 16, 22);

        runningAnimation = makeAnimationByRegion(atlas.findRegion("walking"), 8, 0.1f);
    }

    @Override
    protected Body createBody() {

        return Box2DHelper.createBody(
            new Box2DBody(actualBounds, 10, actualWorld, this)
        );
    }

    public void update(float deltaTime) {

        getAnimationRegion(deltaTime);

        if (Gdx.input.isKeyPressed(Input.Keys.D) && body.getLinearVelocity().x <= 6)
            applyLinealImpulse(new Vector2(4, 0));

        else if (Gdx.input.isKeyPressed(Input.Keys.A) && body.getLinearVelocity().x >= -6)
            applyLinealImpulse(new Vector2(-4, 0));

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && body.getLinearVelocity().y == 0)
            applyLinealImpulse(new Vector2(0, 140));

        playerFallToDead();
    }

    private void playerFallToDead() {

        if (getPixelPosition().y < -100) {

            body.setLinearVelocity(0, 0);

            Vector2 position = GameDataHelper.loadGameData().position;

            body.setTransform(position, 0);
        }
    }

    private AnimationState getCurrentAnimationState() {

        boolean isPlayerMoving = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D);

        if (body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y < 0 && previousState == AnimationState.JUMPING))
            return AnimationState.JUMPING;

        else if (isPlayerMoving)
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

        if ((body.getLinearVelocity().x < 0 || !isMovingRight) && !region.isFlipX()) {

            region.flip(true, false);
            isMovingRight = false;
        } else if ((body.getLinearVelocity().x > 0 || isMovingRight) && region.isFlipX()) {

            region.flip(true, false);
            isMovingRight = true;
        }
    }

    public Vector2 getWorldPosition() {return body.getPosition();}
}
