package knight.arkham.helpers;

import box2dLight.ConeLight;
import box2dLight.PointLight;
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
import knight.arkham.objects.*;
import knight.arkham.objects.Box;
import knight.arkham.objects.structures.Checkpoint;
import knight.arkham.objects.structures.Door;

import static knight.arkham.helpers.Constants.PIXELS_PER_METER;
import static knight.arkham.helpers.Constants.TIME_STEP;
import static knight.arkham.helpers.GameDataHelper.saveGameData;

public class TileMapHelper {
    private final TiledMap tiledMap;
    private final TextureAtlas atlas;
    private final World world;
    private final RayHandler rayHandler;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Player player;
    private Player alterPlayer;
    private final Array<GameObject> gameObjects;
    private final Array<Checkpoint> checkpoints;
    private final Array<Door> doors;
    private final Array<ConeLight> coneLights;
    private final Array<PointLight> pointLights;
    private float lightsTimer;
    private float accumulator;
    private boolean isAlterPlayerActive;
    public static boolean canChangePlayer;

    public TileMapHelper(String mapFilePath, String atlasFilePath) {

        tiledMap = new TmxMapLoader().load(mapFilePath);
        atlas = new TextureAtlas(atlasFilePath);

        world = new World(new Vector2(0, -40), true);
        world.setContactListener(new GameContactListener());

        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(.2f);

        player = new Player(new Rectangle(20, 65, 32, 32), world, atlas);

        saveGameData(new GameData("first", player.getWorldPosition()));

        gameObjects = new Array<>();

        checkpoints = new Array<>();
        doors = new Array<>();

        coneLights = new Array<>();
        pointLights = new Array<>();

        mapRenderer = setupMap();
        debugRenderer = new Box2DDebugRenderer();
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
                        gameObjects.add(new Enemy(mapRectangle, world, atlas.findRegion("enemy"), 2));

                    else
                        gameObjects.add(new Enemy(mapRectangle, world, atlas.findRegion("snake"), 2));
                    break;

                case "Animals":

                    if (mapObject.getName().equals("cat"))
                        gameObjects.add(new Animal(mapRectangle, world, atlas.findRegion("cat"), 3));

                    else
                        gameObjects.add(new Animal(mapRectangle, world, atlas.findRegion("bats"), 2));
                    break;

                case "Lights":

                    Vector2 lightPosition = new Vector2(mapRectangle.x, mapRectangle.y);

                    if (mapObject.getName().equals("cone"))
                        coneLights.add(LightHelper.createConeLight(rayHandler, lightPosition));

                    else
                        pointLights.add(LightHelper.createPointLight(rayHandler, lightPosition));
                    break;

                case "Checkpoints":

                    checkpoints.add(new Checkpoint(mapRectangle, world, atlas.findRegion("checkpoint")));
                    break;

                case "Doors":

                    doors.add(new Door(mapRectangle, world));
                    break;

                case "Boxes":

                    gameObjects.add(new Box(mapRectangle, world));
                    break;

                case "Alter-Player":

                    alterPlayer = new Player(mapRectangle, world, atlas);
                    break;

                case "Enemy-Stopper":
//                    Since I don't need the userData of this body, it could be null.
                    Box2DHelper.createFixture(new Box2DBody(mapRectangle, world, null));
                    break;

                default:
                    Box2DHelper.createBody(new Box2DBody(mapRectangle, world, null));
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

        if (isAlterPlayerActive)
            camera.position.set(alterPlayer.getWorldPosition().x, 5.2f, 0);
        else
            camera.position.set(player.getWorldPosition().x, 5.2f, 0);

        camera.update();
    }

    public void update(float deltaTime, OrthographicCamera camera) {

        //I also have call the rayHandler update method for everything to work accordingly.
        rayHandler.update();

        if (canChangePlayer && Gdx.input.isKeyJustPressed(Input.Keys.W))
            isAlterPlayerActive = !isAlterPlayerActive;

        if (isAlterPlayerActive)
            alterPlayer.update(deltaTime);
        else
            player.update(deltaTime);

        updateCameraPosition(camera);

        for (GameObject gameObject : gameObjects)
            gameObject.update(deltaTime);

        for (Checkpoint checkpoint : checkpoints)
            checkpoint.update(deltaTime);

        for (PointLight light : pointLights) {

            Vector2 lightPosition = light.getPosition().scl(PIXELS_PER_METER);

            if (lightPosition.dst(player.getPixelPosition()) < 80 && Gdx.input.isKeyJustPressed(Input.Keys.W))
                light.setActive(!light.isActive());
        }

        lightsTimer += deltaTime;

        if (lightsTimer > 2) {

            lightsTimer = 0;

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
        alterPlayer.draw(mapRenderer.getBatch());

        for (GameObject gameObject : gameObjects)
            gameObject.draw(mapRenderer.getBatch());

        for (Checkpoint checkpoint : checkpoints)
            checkpoint.draw(mapRenderer.getBatch());

        mapRenderer.getBatch().end();

        debugRenderer.render(world, camera.combined);

        if (!isAlterPlayerActive) {
            rayHandler.setCombinedMatrix(camera);
            //The render method of the rayHandler should be put after all the others objects
            rayHandler.render();
        }
    }

    public void dispose(){

        player.dispose();
        tiledMap.dispose();
        atlas.dispose();
        mapRenderer.dispose();
        world.dispose();
        debugRenderer.dispose();

//        If I make dispose of the rayHandler, I don't need to dispose of the lights.
        rayHandler.dispose();

        for (GameObject gameObject : gameObjects)
            gameObject.dispose();

        for (Checkpoint checkpoint : checkpoints)
            checkpoint.dispose();
    }
}
