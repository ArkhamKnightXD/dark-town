package knight.arkham.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import knight.arkham.helpers.Box2DHelper;

import static knight.arkham.helpers.Constants.PIXELS_PER_METER;

public abstract class GameObject {
    protected final Rectangle actualBounds;
    protected final World actualWorld;
    protected TextureRegion actualRegion;
    protected final Body body;
    private final int regionWidth;
    private final int regionHeight;

    protected GameObject(Rectangle bounds, World world, TextureRegion region) {

        actualBounds = bounds;
        actualWorld = world;
        actualRegion = region;

        regionWidth = region.getRegionWidth();
        regionHeight = region.getRegionHeight();

        body = createBody();
    }

    protected abstract Body createBody();

    public void draw(Batch batch) {

        Rectangle drawBounds = Box2DHelper.getDrawBounds(body, actualBounds);

        batch.draw(actualRegion, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
    }

    protected Animation<TextureRegion> makeAnimationByRegion(TextureRegion region, int totalFrames, float frameDuration) {

        Array<TextureRegion> animationFrames = new Array<>();

        for (int i = 0; i < totalFrames; i++)
            animationFrames.add(new TextureRegion(region, i * regionWidth, 0, regionWidth, regionHeight));

        return new Animation<>(frameDuration, animationFrames);
    }

    protected void applyLinealImpulse(Vector2 impulseDirection) {
        body.applyLinearImpulse(impulseDirection, body.getWorldCenter(), true);
    }

    public Vector2 getPixelPosition() {
        return body.getPosition().scl(PIXELS_PER_METER);
    }

    public void dispose() {actualRegion.getTexture().dispose();}
}
