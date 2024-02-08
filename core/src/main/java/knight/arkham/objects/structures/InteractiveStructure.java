package knight.arkham.objects.structures;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import static knight.arkham.helpers.Box2DHelper.getDrawBounds;
import static knight.arkham.helpers.Constants.DESTROYED_BIT;

public abstract class InteractiveStructure {
    protected final Rectangle actualBounds;
    protected final World actualWorld;
    protected TextureRegion actualRegion;
    protected final Fixture fixture;
    protected final Body body;
    private final int regionWidth;
    private final int regionHeight;

    public InteractiveStructure(Rectangle bounds, World world, TextureRegion region) {

        actualBounds = bounds;
        actualWorld = world;
        actualRegion = region;

        regionWidth = region.getRegionWidth();
        regionHeight = region.getRegionHeight();

        fixture = createObjectFixture();
        body = fixture.getBody();
    }

    protected abstract Fixture createObjectFixture();

    protected Animation<TextureRegion> makeAnimationByRegion(TextureRegion region) {

        Array<TextureRegion> animationFrames = new Array<>();

        for (int i = 0; i < 2; i++)
            animationFrames.add(new TextureRegion(region, i * regionWidth, 0, regionWidth, regionHeight));

        return new Animation<>(0.5f, animationFrames);
    }

    public void draw(Batch batch) {

        Rectangle drawBounds = getDrawBounds(body, actualBounds);

        batch.draw(actualRegion, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
    }

    protected void collisionWithPlayer() {

        Filter filter = new Filter();
        filter.categoryBits = DESTROYED_BIT;

        fixture.setFilterData(filter);
    }

    public void dispose(){
        actualRegion.getTexture().dispose();
    }
}
