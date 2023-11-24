package knight.arkham.helpers;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import knight.arkham.objects.Animal;
import knight.arkham.objects.Enemy;
import knight.arkham.objects.Player;
import knight.arkham.objects.structures.Checkpoint;

import static knight.arkham.helpers.Constants.PIXELS_PER_METER;

public class TileMapHelper {
    private final TiledMap tiledMap;
    private final TextureAtlas atlas;
    private final World world;
    private final RayHandler rayHandler;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Player player;
    private final Array<Enemy> enemies;
    private final Array<Animal> animals;
    private final Array<Checkpoint> checkpoints;
    private final Array<ConeLight> coneLights;
    private float accumulator;
    private final float TIME_STEP;
    private float stateTimer;

    public TileMapHelper(String mapFilePath, String atlasFilePath, World world) {

        tiledMap = new TmxMapLoader().load(mapFilePath);
        atlas = new TextureAtlas(atlasFilePath);
        this.world = world;

        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(.2f);

        player = new Player(new Rectangle(20, 50, 32, 32), world, atlas);

        GameDataHelper.saveGameData(new GameData("first", player.getWorldPosition()));

        enemies = new Array<>();
        animals = new Array<>();
        checkpoints = new Array<>();

        coneLights = new Array<>();

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

            Rectangle mapRectangle = getTileMapRectangle(((RectangleMapObject) mapObject).getRectangle());

            switch (objectsName) {

                case "Enemies":

                    if (mapObject.getName().equals("blob"))
                        enemies.add(new Enemy(mapRectangle, world, atlas.findRegion("enemy"), 2));

                    else
                        enemies.add(new Enemy(mapRectangle, world, atlas.findRegion("snake"), 2));
                    break;

                case "Animals":

                    if (mapObject.getName().equals("cat"))
                        animals.add(new Animal(mapRectangle, world, atlas.findRegion("cat"), 3));

                    else
                        animals.add(new Animal(mapRectangle, world, atlas.findRegion("bats"), 2));
                    break;

                case "Lights":

                    Vector2 lightPosition = new Vector2(mapRectangle.x, mapRectangle.y);

                    if (mapObject.getName().equals("cone"))
                        coneLights.add(LightHelper.createConeLight(rayHandler, lightPosition));

                    else
                        LightHelper.createPointLight(rayHandler, lightPosition);
                    break;

                case "Checkpoints":

                    checkpoints.add(new Checkpoint(mapRectangle, world, atlas.findRegion("checkpoint"), 2));
                    break;

                case "Enemy-Stopper":

                    Box2DHelper.createStaticFixture(new Box2DBody(mapRectangle, world));
                    break;

                default:
                    Box2DHelper.createBody(new Box2DBody(mapRectangle, world));
                    break;
            }
        }
    }

    private Rectangle getTileMapRectangle(Rectangle rectangle){
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

    public void update(float deltaTime, OrthographicCamera camera) {

        //I also have call the rayHandler update method for everything to work accordingly.
        rayHandler.update();

        player.update(deltaTime);

        updateCameraPosition(camera);

        for (Enemy enemy : enemies)
            enemy.update(deltaTime);

        for (Animal animal : animals)
            animal.update(deltaTime);

        for (Checkpoint checkpoint : checkpoints)
            checkpoint.update(deltaTime);

        stateTimer += deltaTime;

        if (stateTimer > 2) {

            stateTimer = 0;

            for (ConeLight light : coneLights)
                light.setActive(!light.isActive());
        }

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

        for (Animal animal : animals)
            animal.draw(mapRenderer.getBatch());

        for (Checkpoint checkpoint : checkpoints)
            checkpoint.draw(mapRenderer.getBatch());

        mapRenderer.getBatch().end();

//        debugRenderer.render(world, camera.combined);

        rayHandler.setCombinedMatrix(camera);
        //The render method of the rayHandler should be put after all the others objects
        rayHandler.render();
    }

    public void dispose(){

        player.dispose();
        tiledMap.dispose();
        atlas.dispose();
        mapRenderer.dispose();
        world.dispose();
        rayHandler.dispose();
        debugRenderer.dispose();

        for (Enemy enemy : enemies)
            enemy.dispose();

        for (Animal cat : animals)
            cat.dispose();

        for (Checkpoint checkpoint : checkpoints)
            checkpoint.dispose();
    }
}
