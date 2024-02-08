package knight.arkham.objects.structures;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import knight.arkham.helpers.Box2DBody;

import static knight.arkham.helpers.Box2DHelper.createBody;

public class Door {

    public Door(Rectangle bounds, World world) {
        createBody(new Box2DBody(bounds, 0, world, this));
    }
}
