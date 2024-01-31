package knight.arkham.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import knight.arkham.helpers.Box2DBody;
import knight.arkham.helpers.Box2DHelper;

public class Box extends GameObject {

    public Box(Rectangle bounds, World world) {
        super(bounds, world, new TextureRegion(new Texture("images/box.png")));

    }

    @Override
    protected Body createBody() {
        return Box2DHelper.createBody(
            new Box2DBody(actualBounds, 20, actualWorld, this)
        );
    }

    @Override
    protected void childUpdate(float deltaTime) {

    }
}
