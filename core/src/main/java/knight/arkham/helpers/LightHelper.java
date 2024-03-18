package knight.arkham.helpers;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import knight.arkham.objects.Player;

import static com.badlogic.gdx.graphics.Color.WHITE;
import static knight.arkham.helpers.Constants.PIXELS_PER_METER;

public class LightHelper {
    public final RayHandler rayHandler;
    private final Array<ConeLight> coneLights;
    private final Array<PointLight> pointLights;
    private float lightsTimer;

    public LightHelper(World world, float ambientLight) {

        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(.2f);

        coneLights = new Array<>();
        pointLights = new Array<>();
    }

    public void createConeLight(Vector2 position) {

        position.scl(1 / PIXELS_PER_METER);

        ConeLight coneLight = new ConeLight(rayHandler, 10, WHITE, 10, position.x, position.y, -90, 30);

        coneLights.add(coneLight);
    }

    public void createPointLight(Vector2 position) {

        position.scl(1 / PIXELS_PER_METER);

        PointLight pointLight = new PointLight(rayHandler, 10, WHITE, 5, position.x, position.y);

        pointLights.add(pointLight);
    }

    public void update(float deltaTime, Player player) {

        //I also have call the rayHandler update method for everything to work accordingly.
        rayHandler.update();

        lightsTimer += deltaTime;

        if (lightsTimer > 2) {

            lightsTimer = 0;

            for (ConeLight light : coneLights)
                light.setActive(!light.isActive());
        }

        for (PointLight light : pointLights) {

            Vector2 lightPosition = light.getPosition().scl(PIXELS_PER_METER);

            if (lightPosition.dst(player.getPixelPosition()) < 80 && Gdx.input.isKeyJustPressed(Input.Keys.W))
                light.setActive(!light.isActive());
        }
    }

    public void draw(OrthographicCamera camera, boolean isAlterPlayerActive) {

        if (!isAlterPlayerActive) {
            rayHandler.setCombinedMatrix(camera);
            //The render method of the rayHandler should be put after all the others objects
            rayHandler.render();
        }
    }

    public void dispose() {
        // If I make dispose of the rayHandler, I don't need to dispose of the lights.
        rayHandler.dispose();
    }
}
