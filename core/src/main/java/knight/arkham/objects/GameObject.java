package knight.arkham.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import static knight.arkham.helpers.Constants.PIXELS_PER_METER;

public abstract class GameObject {
    protected final Rectangle actualBounds;
    protected final World actualWorld;
    protected final Body body;
    protected TextureRegion actualRegion;

    protected GameObject(Rectangle bounds, World world, TextureRegion region) {

        actualBounds = bounds;
        actualWorld = world;
        actualRegion = region;

        body = createBody();
    }

    protected abstract Body createBody();

    private Rectangle getDrawBounds() {

        return new Rectangle(
            body.getPosition().x - (actualBounds.width / 2 / PIXELS_PER_METER),
            body.getPosition().y - (actualBounds.height / 2 / PIXELS_PER_METER),
            actualBounds.width / PIXELS_PER_METER,
            actualBounds.height / PIXELS_PER_METER
        );
    }

    public void draw(Batch batch) {

        Rectangle drawBounds = getDrawBounds();

        batch.draw(actualRegion, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
    }

    protected void applyLinealImpulse(Vector2 impulseDirection) {
        body.applyLinearImpulse(impulseDirection, body.getWorldCenter(), true);
    }

    public Vector2 getPixelPosition() {

        return new Vector2(body.getPosition().x * PIXELS_PER_METER, body.getPosition().y * PIXELS_PER_METER);
    }

    public Vector2 getWorldPosition() {return body.getPosition();}

    protected Animation<TextureRegion> makeAnimationByFrameRange(TextureRegion characterRegion, int finalFrame, int width, int height) {

        Array<TextureRegion> animationFrames = new Array<>();

        for (int i = 0; i <= finalFrame; i++)
            animationFrames.add(new TextureRegion(characterRegion, i * width, 0, width, height));

        return new Animation<>(0.1f, animationFrames);
    }

    public void dispose() {actualRegion.getTexture().dispose();}
}
