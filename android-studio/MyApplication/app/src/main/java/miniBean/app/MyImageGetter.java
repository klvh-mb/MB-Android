package miniBean.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import miniBean.R;
import miniBean.util.ActivityUtil;
import miniBean.util.EmoticonUtil;

public class MyImageGetter implements Html.ImageGetter{

    private TextView textView;

    private int emoticonWidth, emoticonHeight;

    private Activity activity;

    private ActivityUtil activityUtil;

    public MyImageGetter(Activity activity) {
        this.activity = activity;
        this.activityUtil = new ActivityUtil(activity);
        emoticonWidth = activityUtil.getRealDimension(EmoticonUtil.WIDTH, this.activity.getResources());
        emoticonHeight = activityUtil.getRealDimension(EmoticonUtil.HEIGHT, this.activity.getResources());
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();

        int emoticon = EmoticonUtil.map(source);
        if (emoticon != -1) {
            //Log.d(this.getClass().getSimpleName(), "getDrawable: replace source with emoticon - " + source);
            Drawable emo = activity.getResources().getDrawable(emoticon);
            d.addLevel(0, 0, emo);
            d.setBounds(0, 0, emoticonWidth, emoticonHeight);
        } else {
            Log.d(this.getClass().getSimpleName(), "getDrawable: load emoticon from background - " + source);
            Drawable empty = activity.getResources().getDrawable(R.drawable.empty);
            d.addLevel(0, 0, empty);
            d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
            new LoadImageToBody().execute(source, d);
        }

        return d;
    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        protected ImageView imageView;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            if (!source.startsWith(activity.getResources().getString(R.string.base_url))) {
                source = activity.getResources().getString(R.string.base_url) + source;
            }

            imageView = (ImageView) params[1];
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Log.d(this.getClass().getSimpleName(), "onPostExecute: loaded bitmap - " + bitmap.getWidth() + "|" + bitmap.getHeight());
                Drawable d = new BitmapDrawable(
                        activity.getResources(),
                        Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false));
                imageView.setImageDrawable(d);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    // Obsolete... use universal ImageLoader
    class LoadPostImage extends LoadImage {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Log.d(this.getClass().getSimpleName(), "onPostExecute: loaded bitmap - " + bitmap.getWidth() + "|" + bitmap.getHeight());

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                // always stretch to screen width
                int displayWidth = activityUtil.getDisplayDimensions(MyImageGetter.this.activity).width();
                float scaleAspect = (float)displayWidth / (float)width;
                width = displayWidth;
                height = (int)(height * scaleAspect);

                Log.d(this.getClass().getSimpleName(), "onPostExecute: after shrink - " + width + "|" + height + " with scaleAspect=" + scaleAspect);

                Drawable d = new BitmapDrawable(
                        activity.getResources(),
                        Bitmap.createScaledBitmap(bitmap, width, height, false));
                imageView.setImageDrawable(d);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    class LoadImageToBody extends LoadImage {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null && textView != null) {
                Log.d(this.getClass().getSimpleName(), "onPostExecute: refresh body text");
                // i don't know yet a better way to refresh TextView
                // textView.invalidate() doesn't work as expected
                CharSequence t = textView.getText();
                textView.setText(t);
            }
        }
    }
}


