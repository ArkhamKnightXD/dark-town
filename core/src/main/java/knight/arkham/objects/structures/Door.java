package knight.arkham.objects.structures;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import knight.arkham.helpers.Box2DBody;
import knight.arkham.helpers.Box2DHelper;

public class Door {

    public Door(Rectangle bounds, World world) {

        Box2DHelper.createStaticFixture(
            new Box2DBody(bounds, world, this)
        );
    }
}
