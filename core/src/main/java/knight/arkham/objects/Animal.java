package knight.arkham.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import knight.arkham.helpers.Box2DBody;

import static knight.arkham.helpers.AnimationHelper.makeAnimation;
import static knight.arkham.helpers.Box2DHelper.createBody;

public class Animal extends GameObject {
    private final Animation<TextureRegion> animation;
    private float animationTimer;

    public Animal(Rectangle bounds, World world, TextureAtlas.AtlasRegion region, int totalFrames) {
        super(
            bounds, world,
            new TextureRegion(
                region, 0, 0, region.getRegionWidth() / totalFrames, region.getRegionHeight()
            )
        );

        animation = makeAnimation(region, frameWidth, frameHeight, totalFrames, 0.2f);
    }

    @Override
    protected Body createObjectBody() {

        return createBody(
            new Box2DBody(actualBounds, 0, actualWorld, this)
        );
    }

    protected void childUpdate(float deltaTime) {

        animationTimer += deltaTime;

        actualRegion = animation.getKeyFrame(animationTimer, true);
    }
}
