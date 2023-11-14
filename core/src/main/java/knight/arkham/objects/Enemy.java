package knight.arkham.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import knight.arkham.helpers.Box2DBody;
import knight.arkham.helpers.Box2DHelper;

public class Enemy extends GameObject {
    private final Animation<TextureRegion> runningAnimation;
    private float stateTimer;
    public boolean isMovingRight;
    private boolean setToDestroy;
    private boolean isDestroyed;

    public Enemy(Rectangle bounds, World world, TextureAtlas atlas) {
        super(
            bounds, world,
            new TextureRegion(atlas.findRegion("Run-Enemy"), 0, 0, 32, 32)
        );

        isMovingRight = true;

        runningAnimation = makeAnimationByFrameRange(atlas.findRegion("Run-Enemy"), 10, 32, 32);
    }

    @Override
    protected Body createBody() {

        return Box2DHelper.createBody(
            new Box2DBody(actualBounds, 10, actualWorld, this)
        );
    }

    private void destroyEnemy() {

        actualWorld.destroyBody(body);
        isDestroyed = true;

        stateTimer = 0;
    }

    public void update(float deltaTime) {

        stateTimer += deltaTime;

        if (setToDestroy && !isDestroyed)
            destroyEnemy();

        else if (!isDestroyed) {

            actualRegion = runningAnimation.getKeyFrame(stateTimer, true);

            if (isMovingRight && body.getLinearVelocity().x <= 4)
                applyLinealImpulse(new Vector2(2, 0));

            else if (!isMovingRight && body.getLinearVelocity().x >= -4)
                applyLinealImpulse(new Vector2(-2, 0));

            if (getPixelPosition().y < -50)
                setToDestroy = true;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (!isDestroyed || stateTimer < 1)
            super.draw(batch);
    }

    public void changeDirection(){
        isMovingRight = !isMovingRight;
    }
}
