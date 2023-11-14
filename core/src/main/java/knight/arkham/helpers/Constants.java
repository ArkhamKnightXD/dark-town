package knight.arkham.helpers;

import com.badlogic.gdx.Gdx;

public class Constants {
    public static final float PIXELS_PER_METER = 32f;
    public static final int MID_SCREEN_WIDTH = Gdx.graphics.getWidth()/ 2;
    public static final short GROUND_BIT = 1;
    public static final short PLAYER_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short CHECKPOINT_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short FINISH_BIT = 256;
    public static final short MARIO_HEAD_BIT = 512;
}
