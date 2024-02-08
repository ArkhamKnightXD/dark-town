package knight.arkham.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import static knight.arkham.helpers.Box2DHelper.getDrawBounds;

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

        body = createObjectBody();
    }

    protected abstract Body createObjectBody();

    protected abstract void childUpdate(float deltaTime);

    public void update(float deltaTime) {

        childUpdate(deltaTime);
    }

    public void draw(Batch batch) {

        Rectangle drawBounds = getDrawBounds(body, actualBounds);

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

    public void dispose() {actualRegion.getTexture().dispose();}
}
