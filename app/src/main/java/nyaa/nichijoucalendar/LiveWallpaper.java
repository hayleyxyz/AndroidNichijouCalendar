package nyaa.nichijoucalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Oscar on 18/01/2015.
 */
public class LiveWallpaper extends WallpaperService {

    @Override
    public Engine onCreateEngine() {


        return new LiveWallpaperEngine(this);
    }

    private class LiveWallpaperEngine extends Engine implements Runnable {

        private Context mContext;
        private Handler mHandler;

        private SurfaceHolder mSurfaceHolder;
        private int mWidth;
        private int mHeight;
        private boolean mPortrait;

        private Paint mPaint;
        private Bitmap mImage;

        public LiveWallpaperEngine(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            mHandler = new Handler();
            mPaint = new Paint();

            requestDraw();
        }

        private void loadPlaceholderImage() {
            ImageLoader imageLoader = ImageLoader.getInstance();
            CanvasBitmapProcessor bitmapProcessor = new CanvasBitmapProcessor(mWidth, mHeight);
            DisplayImageOptions opts = new DisplayImageOptions.Builder()
                    .preProcessor(bitmapProcessor)
                    .build();

            String imageUrl = "assets://placeholder_1080.jpg";

            imageLoader.loadImage(imageUrl, opts, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if(mImage == null) {
                        mImage = loadedImage;
                        requestDraw();
                    }
                }
            });
        }

        private void loadCalendarImage() {
            if(mImage == null) { // display placeholder while image is loading
                loadPlaceholderImage();
            }

            String imageUrl = CalendarImageUrl.getUrl();

            ImageLoader imageLoader = ImageLoader.getInstance();
            CanvasBitmapProcessor bitmapProcessor = new CanvasBitmapProcessor(mWidth, mHeight);
            DisplayImageOptions opts = new DisplayImageOptions.Builder()
                    .preProcessor(bitmapProcessor)
                    .build();

            imageLoader.loadImage(imageUrl, opts, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mImage = loadedImage;
                    requestDraw();
                }
            });
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            mWidth = width;
            mHeight = height;
            mPortrait = (mHeight > mWidth);

            loadCalendarImage();
            requestDraw();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mHandler.removeCallbacks(this);

            if(visible) {
                loadCalendarImage();
                requestDraw();
            }
        }

        private void draw(Canvas canvas) {
            if(mImage != null) {
                canvas.drawColor(Color.WHITE);

                Rect src = new Rect(0, 0, mImage.getWidth(), mImage.getHeight());
                Rect dst = ImageUtils.canvasCenter(mImage.getWidth(), mImage.getHeight(), mWidth, mHeight);

                canvas.drawBitmap(mImage, src, dst, mPaint);
            }
        }

        @Override
        public void run() {
            Canvas canvas = mSurfaceHolder.lockCanvas();
            if(canvas != null) {
                try {
                    draw(canvas);
                }
                finally {
                    if(canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void requestDraw() {
            mHandler.post(this);
        }

    }

}
