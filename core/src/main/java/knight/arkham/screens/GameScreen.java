package knight.arkham.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import knight.arkham.Dark;
import knight.arkham.helpers.GameContactListener;
import knight.arkham.helpers.TileMapHelper;

public class GameScreen extends ScreenAdapter {
    private final Dark game;
    private final OrthographicCamera camera;
    private final TileMapHelper mapHelper;

    public GameScreen() {

        game = Dark.INSTANCE;

        camera = game.camera;

        World world = new World(new Vector2(0, -40), true);

        world.setContactListener(new GameContactListener());

        TextureAtlas atlas = new TextureAtlas("images/test.atlas");

        mapHelper = new TileMapHelper("maps/level1.tmx", world, atlas);
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height);
    }

    @Override
    public void render(float deltaTime) {

        ScreenUtils.clear(0, 0, 0, 0);

        mapHelper.update(deltaTime, camera);

        mapHelper.draw(camera);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {

        mapHelper.dispose();
    }
}
