package knight.arkham.objects.structures;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import knight.arkham.helpers.Box2DBody;
import knight.arkham.helpers.Box2DHelper;

public class Box extends InteractiveStructure {

    public Box(Rectangle bounds, World world) {
        super(bounds, world, new TextureRegion(new Texture("images/box.png")));

    }

    @Override
    protected Fixture createFixture() {
        return Box2DHelper.createStaticFixture(
            new Box2DBody(actualBounds, 20, actualWorld, this)
        );
    }
}
