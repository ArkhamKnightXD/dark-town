package knight.arkham.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;

public class GameDataHelper {
    private static final String dataFilename = "dark-town";

    public static void saveGameData(GameData gameData) {

        Preferences preferences = Gdx.app.getPreferences(dataFilename);

        preferences.putString("screenName", gameData.screenName);

        preferences.putFloat("positionX", gameData.position.x);
        preferences.putFloat("positionY", gameData.position.y);

        preferences.flush();
    }

    public static GameData loadGameData() {

        Preferences preferences = Gdx.app.getPreferences(dataFilename);

        float positionX = preferences.getFloat("positionX");
        float positionY = preferences.getFloat("positionY");

        String screenName = preferences.getString("screenName");

        return new GameData(screenName, new Vector2(positionX, positionY));
    }
}
