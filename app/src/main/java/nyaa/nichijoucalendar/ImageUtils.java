package nyaa.nichijoucalendar;

import android.graphics.Rect;

/**
 * Created by Oscar on 19/01/2015.
 */
public class ImageUtils {

    public static int getWidthForHeight(int origWidth, int origHeight, int height) {
        return (int)Math.floor((((float)origWidth / origHeight) * height));
    }

    public static int getHeightForWidth(int origWidth, int origHeight, int width) {
        return (int)Math.floor(((float)origHeight / origWidth) * width);
    }

    public static Rect canvasCenter(int imageWidth, int imageHeight, int canvasWidth, int canvasHeight) {
        int x = (canvasWidth / 2) - (imageWidth / 2);
        int y = (canvasHeight / 2) - (imageHeight / 2);

        return new Rect(x, y, x + imageWidth, y + imageHeight);
    }

}
