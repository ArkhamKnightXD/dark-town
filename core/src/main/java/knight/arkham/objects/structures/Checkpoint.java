package knight.arkham.objects.structures;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import knight.arkham.helpers.Box2DBody;
import knight.arkham.helpers.Box2DHelper;

import static knight.arkham.helpers.Constants.PIXELS_PER_METER;

public class Checkpoint extends InteractiveStructure {
    private final Animation<TextureRegion> animation;
    private float stateTimer;
    private boolean isActive;
    private final TextureAtlas atlas;

    public Checkpoint(Rectangle rectangle, World world, TextureAtlas atlas) {
        super(
            rectangle, world,
            new TextureRegion(atlas.findRegion("No-Flag"), 0, 0, 64, 64)
        );

        animation = makeAnimationByFrameRange(atlas.findRegion("Flag"));
        this.atlas = atlas;
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

    private Rectangle getDrawBounds() {

        return new Rectangle(
            body.getPosition().x - (actualBounds.width / 2 / PIXELS_PER_METER),
            body.getPosition().y - (actualBounds.height / 2 / PIXELS_PER_METER),
            actualBounds.width / PIXELS_PER_METER,
            actualBounds.height / PIXELS_PER_METER
        );
    }

    @Override
    protected Fixture createFixture() {
        return Box2DHelper.createStaticFixture(
            new Box2DBody(actualBounds, 0, actualWorld, this)
        );
    }

    public void draw(Batch batch) {

        Rectangle drawBounds = getDrawBounds();

        batch.draw(actualRegion, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
    }

    public void createCheckpoint() {

        collisionWithPlayer();

        isActive = true;
    }
}
