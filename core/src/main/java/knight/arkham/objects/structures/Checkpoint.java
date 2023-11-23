package knight.arkham.objects.structures;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import knight.arkham.helpers.Box2DBody;
import knight.arkham.helpers.Box2DHelper;
import knight.arkham.helpers.GameData;
import knight.arkham.helpers.GameDataHelper;

public class Checkpoint extends InteractiveStructure {
    private final Animation<TextureRegion> animation;
    private float stateTimer;
    private boolean isActive;

    public Checkpoint(Rectangle rectangle, World world, TextureAtlas.AtlasRegion region, int totalFrames) {
        super(
            rectangle, world,
            new TextureRegion(
                region, 0, 0,
                region.getRegionWidth() / totalFrames,
                region.getRegionHeight())
        );

        animation = makeAnimationByRegion(region, totalFrames);
    }

    @Override
    protected Fixture createFixture() {
        return Box2DHelper.createStaticFixture(
            new Box2DBody(actualBounds, 0, actualWorld, this)
        );
    }

    public void update(float deltaTime) {

        stateTimer += deltaTime;

        if (isActive)
            actualRegion = animation.getKeyFrame(stateTimer, true);
    }

    public void createCheckpoint() {

        collisionWithPlayer();

        isActive = true;

        GameDataHelper.saveGameData(new GameData("first", body.getPosition()));
    }
}
