package miniBean.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import miniBean.R;
import miniBean.util.ImageUtil;
import miniBean.util.ViewUtil;

public class FullScreenActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.full_screen_activity);

        ImageView imageView = (ImageView) findViewById(R.id.image);

        //imageView.setAdjustViewBounds(true);
        //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //imageView.setPadding(0, 0, 0, ViewUtil.getRealDimension(10, this.getResources()));

        ImageUtil.displayOriginalMessageImage(getIntent().getLongExtra("image", 0l), imageView,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage != null) {
                    Log.d(this.getClass().getSimpleName(), "onLoadingComplete: loaded bitmap - " + loadedImage.getWidth() + "|" + loadedImage.getHeight());

                    int width = loadedImage.getWidth();
                    int height = loadedImage.getHeight();

                    // always stretch to message width
                    int displayWidth = ViewUtil.getDisplayDimensions(FullScreenActivity.this).width();
                    float scaleAspect = (float) displayWidth / (float) width;
                    width = displayWidth;
                    height = (int) (height * scaleAspect);

                    Log.d(this.getClass().getSimpleName(), "onLoadingComplete: after resize - " + width + "|" + height + " with scaleAspect=" + scaleAspect);

                    Drawable d = new BitmapDrawable(
                            FullScreenActivity.this.getResources(),
                            Bitmap.createScaledBitmap(loadedImage, width, height, false));
                    ImageView imageView = (ImageView) view;
                    imageView.setImageDrawable(d);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        });


    }
}
