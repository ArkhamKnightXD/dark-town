package knight.arkham.objects.structures;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import knight.arkham.helpers.Box2DBody;
import knight.arkham.helpers.Box2DHelper;

public class Checkpoint extends InteractiveStructure {
    private final Animation<TextureRegion> animation;
    private float stateTimer;
    private boolean isActive;
    private final TextureAtlas atlas;

    public Checkpoint(Rectangle rectangle, World world, TextureAtlas atlas) {
        super(
            rectangle, world,
            new TextureRegion(atlas.findRegion("no-flag"), 0, 0, 64, 64)
        );

        animation = makeAnimationByFrameRange(atlas.findRegion("flag"));
        this.atlas = atlas;
    }

    @Override
    protected Fixture createFixture() {
        return Box2DHelper.createStaticFixture(
            new Box2DBody(actualBounds, 0, actualWorld, this)
        );
    }

    private Animation<TextureRegion> makeAnimationByFrameRange(TextureRegion characterRegion) {

        Array<TextureRegion> animationFrames = new Array<>();

        for (int i = 0; i <= 9; i++)
            animationFrames.add(new TextureRegion(characterRegion, i * 64, 0, 64, 64));

        return new Animation<>(0.1f, animationFrames);
    }

    public void update(float deltaTime) {

        stateTimer += deltaTime;

        if (isActive)
            actualRegion = animation.getKeyFrame(stateTimer, true);
        else
            actualRegion = atlas.findRegion("No-Flag");
    }

    public void createCheckpoint() {

        collisionWithPlayer();

        isActive = true;
    }
}
