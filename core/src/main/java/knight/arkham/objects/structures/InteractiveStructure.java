package knight.arkham.objects.structures;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

import static knight.arkham.helpers.Constants.DESTROYED_BIT;

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

    protected void collisionWithPlayer() {

        Filter filter = new Filter();

        filter.categoryBits = DESTROYED_BIT;
        fixture.setFilterData(filter);
    }

    public void dispose(){
        actualRegion.getTexture().dispose();
    }
}
