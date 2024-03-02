package knight.arkham.objects.structures;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

import static knight.arkham.helpers.Box2DHelper.getDrawBounds;
import static knight.arkham.helpers.Constants.DESTROYED_BIT;

public abstract class InteractiveStructure {
    protected final Rectangle actualBounds;
    protected final World actualWorld;
    protected TextureRegion actualRegion;
    protected final Fixture fixture;
    protected final Body body;
    protected final int frameWidth;
    protected final int frameHeight;

    public InteractiveStructure(Rectangle bounds, World world, TextureRegion region) {

        actualBounds = bounds;
        actualWorld = world;
        actualRegion = region;

        frameWidth = region.getRegionWidth();
        frameHeight = region.getRegionHeight();

        fixture = createObjectFixture();
        body = fixture.getBody();
    }

    protected abstract Fixture createObjectFixture();

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
