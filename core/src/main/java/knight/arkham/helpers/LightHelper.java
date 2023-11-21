package knight.arkham.helpers;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;

import static com.badlogic.gdx.graphics.Color.WHITE;

public class LightHelper {

    public static void createConeLight(RayHandler rayHandler, Vector2 position) {

        new ConeLight(rayHandler, 10, WHITE, 10, position.x, position.y, -90, 30);
    }

    public static void createPointLight(RayHandler rayHandler, Vector2 position) {

        new PointLight(rayHandler, 10, WHITE, 5, position.x, position.y);
    }
}
