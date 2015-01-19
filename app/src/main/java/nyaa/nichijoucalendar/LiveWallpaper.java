package nyaa.nichijoucalendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.View;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

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

        private Paint mPaint;
        private Bitmap mImage;

        private boolean mDirty;

        public LiveWallpaperEngine(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            mHandler = new Handler();
            mPaint = new Paint();
            mDirty = true;

            loadImage();
        }

        private void loadImage() {
            String imageUrl = CalendarImageUrl.getUrl();

            ImageLoader imageLoader = ImageLoader.getInstance();

            ImageSize targetSize = new ImageSize(mWidth, mHeight);
            imageLoader.loadImage(imageUrl, targetSize, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mImage = loadedImage;
                    mDirty = true;

                    Log.d("LWP", "Width: " + loadedImage.getWidth());
                    Log.d("LWP", "Height: " + loadedImage.getHeight());
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
            mDirty = true;

            loadImage();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mHandler.removeCallbacks(this);

            mDirty = true;

            if(visible) {
                loadImage();
                mHandler.post(this);
            }
        }

        private void draw(Canvas canvas) {
            if(mImage != null && mDirty) {
                canvas.drawColor(Color.WHITE);

                Rect src = new Rect(0, 0, mImage.getWidth(), mImage.getHeight());
                Rect dst = new Rect(0, 0, mWidth, mHeight);

                canvas.drawBitmap(mImage, src, dst, mPaint);

                mDirty = false;
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

            mHandler.postDelayed(this, 22);
        }

        private float dipToPx(int dip) {
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
        }
    }

}
