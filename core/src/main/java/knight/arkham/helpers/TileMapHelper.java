package knight.arkham.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import knight.arkham.objects.Animal;
import knight.arkham.objects.Enemy;
import knight.arkham.objects.Player;

import static knight.arkham.helpers.Constants.PIXELS_PER_METER;

public class TileMapHelper {
    private final TiledMap tiledMap;
    private final TextureAtlas atlas;
    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Player player;
    private final Array<Enemy> enemies;
    private final Array<Animal> animals;
    private float accumulator;
    private final float TIME_STEP;

    public TileMapHelper(String mapFilePath, String atlasFilePath, World world) {

        tiledMap = new TmxMapLoader().load(mapFilePath);

        atlas = new TextureAtlas(atlasFilePath);

        this.world = world;

        player = new Player(new Rectangle(20, 50, 32, 32), world, atlas);
        enemies = new Array<>();
        animals = new Array<>();

        mapRenderer = setupMap();

        debugRenderer = new Box2DDebugRenderer();
        TIME_STEP = 1/240f;
    }

    public OrthogonalTiledMapRenderer setupMap() {

        for (MapLayer mapLayer : tiledMap.getLayers())
            parseMapObjectsToBox2DBodies(mapLayer.getObjects(), mapLayer.getName());

        return new OrthogonalTiledMapRenderer(tiledMap, 1 / PIXELS_PER_METER);
    }

    private void parseMapObjectsToBox2DBodies(MapObjects mapObjects, String objectsName) {

        for (MapObject mapObject : mapObjects) {

            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

            Rectangle box2DRectangle = getBox2dRectangle(rectangle);

            if (objectsName.equals("Enemies")) {

                if (mapObject.getName().equals("blob"))
                    enemies.add(new Enemy(box2DRectangle, world, atlas.findRegion("enemy"), 2));

                else
                    enemies.add(new Enemy(box2DRectangle, world, atlas.findRegion("snake"), 2));
            }

            else if (objectsName.equals("Animals")) {

                if (mapObject.getName().equals("cat"))
                    animals.add(new Animal(box2DRectangle, world, atlas.findRegion("cat"), 3));

                else
                    animals.add(new Animal(box2DRectangle, world, atlas.findRegion("bats"), 2));
            }

            else
                Box2DHelper.createBody(new Box2DBody(box2DRectangle, world));
        }
    }

    private Rectangle getBox2dRectangle(Rectangle rectangle){
        return new Rectangle(
            rectangle.x + rectangle.width / 2,
            rectangle.y + rectangle.height / 2,
            rectangle.width, rectangle.height
        );
    }

    public void updateCameraPosition(OrthographicCamera camera) {

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            camera.position.x += 0.1f;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            camera.position.x -= 0.1f;

        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            camera.position.y += 0.1f;

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            camera.position.y -= 0.1f;

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3))
            camera.zoom += 0.1f;

        if (Gdx.input.isKeyJustPressed(Input.Keys.F4))
            camera.zoom -= 0.1f;

        camera.position.set(player.getWorldPosition().x, 5.2f, 0);

        camera.update();
    }

    public void update(float deltaTime, OrthographicCamera camera){

        player.update(deltaTime);

        updateCameraPosition(camera);

        for (Enemy enemy : enemies)
            enemy.update(deltaTime);

        for (Animal animal : animals)
            animal.update(deltaTime);

        doPhysicsTimeStep(deltaTime);
    }

    private void doPhysicsTimeStep(float deltaTime) {

        float frameTime = Math.min(deltaTime, 0.25f);

        accumulator += frameTime;

        while(accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 6,2);
            accumulator -= TIME_STEP;
        }
    }

    public void draw(OrthographicCamera camera){

        mapRenderer.setView(camera);

        mapRenderer.render();

        mapRenderer.getBatch().setProjectionMatrix(camera.combined);

        mapRenderer.getBatch().begin();

        player.draw(mapRenderer.getBatch());

        for (Enemy enemy : enemies)
            enemy.draw(mapRenderer.getBatch());

        for (Animal cat : animals)
            cat.draw(mapRenderer.getBatch());

        mapRenderer.getBatch().end();

//        debugRenderer.render(world, camera.combined);
    }

    public void dispose(){

        player.dispose();
        tiledMap.dispose();
        atlas.dispose();
        mapRenderer.dispose();
        world.dispose();
        debugRenderer.dispose();

        for (Enemy enemy : enemies)
            enemy.dispose();

        for (Animal cat : animals)
            cat.dispose();
    }
}
