package knight.arkham;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import knight.arkham.screens.GameScreen;

public class Dark extends Game {
    public static Dark INSTANCE;
    public OrthographicCamera camera;
    public Viewport viewport;
    public int screenWidth;
    public int screenHeight;

    public Dark() {

        INSTANCE = this;
    }

    @Override
    public void create() {

        camera = new OrthographicCamera();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        viewport = new FitViewport(screenWidth / 32f, screenHeight / 32f, camera);

        camera.zoom -= 0.3f;

        camera.position.set(screenWidth / 2f / 32f, screenHeight / 2f / 32f, 0);

        setScreen(new GameScreen());
    }
}
