package knight.arkham.helpers;

import com.badlogic.gdx.math.Vector2;

public class GameData {
    public String screenName;
    public Vector2 position;

    public GameData(String screenName, Vector2 position) {
        this.screenName = screenName;
        this.position = position;
    }
}
