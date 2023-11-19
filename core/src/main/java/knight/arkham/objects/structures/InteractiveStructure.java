package knight.arkham.objects.structures;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import static knight.arkham.helpers.Constants.DESTROYED_BIT;
import static knight.arkham.helpers.Constants.PIXELS_PER_METER;

public abstract class InteractiveStructure {
    protected final Rectangle actualBounds;
    protected final World actualWorld;
    protected TextureRegion actualRegion;
    protected final Fixture fixture;
    protected final Body body;

    public InteractiveStructure(Rectangle rectangle, World world, TextureRegion region) {

        actualBounds = rectangle;
        actualWorld = world;
        actualRegion = region;

        fixture = createFixture();

        body = fixture.getBody();
    }

    protected abstract Fixture createFixture();

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

    protected void collisionWithPlayer() {

        Filter filter = new Filter();

        filter.categoryBits = DESTROYED_BIT;
        fixture.setFilterData(filter);
    }

    public void dispose(){
        actualRegion.getTexture().dispose();
    }
}
