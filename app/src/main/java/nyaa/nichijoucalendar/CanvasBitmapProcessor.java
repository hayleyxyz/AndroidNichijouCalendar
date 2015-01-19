package nyaa.nichijoucalendar;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * Created by Oscar on 19/01/2015.
 */
public class CanvasBitmapProcessor implements BitmapProcessor {

    protected int mWidth;
    protected int mHeight;
    
    public CanvasBitmapProcessor(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public Bitmap process(Bitmap bitmap) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();

        int newWidth = mWidth;
        int newHeight = ImageUtils.getHeightForWidth(imageWidth, imageHeight, newWidth);

        if(newHeight > mHeight) {
            newHeight = mHeight;
            newWidth = ImageUtils.getWidthForHeight(imageWidth, imageHeight, newHeight);
        }

        Bitmap newBitmap = bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        bitmap.recycle();
        bitmap = null;

        return newBitmap;
    }

}
