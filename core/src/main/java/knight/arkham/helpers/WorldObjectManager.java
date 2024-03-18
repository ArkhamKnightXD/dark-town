package knight.arkham.helpers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import knight.arkham.objects.GameObject;
import knight.arkham.objects.structures.Checkpoint;

public class WorldObjectManager {
    private final Array<GameObject> gameObjects;
    private final Array<Checkpoint> checkpoints;

    public WorldObjectManager() {
        gameObjects = new Array<>();
        checkpoints = new Array<>();
    }

    public void createGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public void createCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
    }

    public void update(float deltaTime) {

        for (GameObject gameObject : gameObjects)
            gameObject.update(deltaTime);

        for (Checkpoint checkpoint : checkpoints)
            checkpoint.update(deltaTime);
    }

    public void draw(Batch batch) {

        for (GameObject gameObject : gameObjects)
            gameObject.draw(batch);

        for (Checkpoint checkpoint : checkpoints)
            checkpoint.draw(batch);
    }

    public void dispose() {

        for (GameObject gameObject : gameObjects)
            gameObject.dispose();

        for (Checkpoint checkpoint : checkpoints)
            checkpoint.dispose();
    }
}
