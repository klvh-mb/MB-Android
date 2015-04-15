package miniBean.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;

import miniBean.R;
import miniBean.app.AppController;

/**
 * Created by keithlei on 3/16/15.
 */
public class ImageUtil {

    public static final int SELECT_PICTURE = 1;

    public static final int PREVIEW_THUMBNAIL_MAX_WIDTH = 350;
    public static final int PREVIEW_THUMBNAIL_MAX_HEIGHT = 350;

    public static final String COMMUNITY_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-cover-community-image-by-id/";
    public static final String THUMBNAIL_COMMUNITY_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-cover-community-image-by-id/";
    public static final String COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-cover-image-by-id/";
    public static final String THUMBNAIL_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-cover-image-by-id/";
    public static final String PROFILE_IMAGE_URL = AppController.BASE_URL + "/image/get-profile-image-by-id/";
    public static final String PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-profile-image-by-id/";
    public static final String THUMBNAIL_PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-image-by-id/";
    public static final String MINI_PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-mini-image-by-id/";
    public static final String POST_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-post-image-by-id/";
    public static final String ORIGINAL_POST_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-original-post-image-by-id/";
    //public static final String COMMENT_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-comment-image-by-id/";

    public static DisplayImageOptions DEFAULT_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().
                    cacheInMemory(true).
                    cacheOnDisk(true).
                    showImageOnLoading(R.drawable.image_loading).
                    displayer(new RoundedBitmapDisplayer(0)).build();

    public static DisplayImageOptions ROUNDED_CORNERS_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().
                    cacheInMemory(true).
                    cacheOnDisk(true).
                    showImageOnLoading(R.drawable.image_loading).
                    displayer(new RoundedBitmapDisplayer(DefaultValues.IMAGE_CORNERS_ROUNDED_VALUE)).build();

    public static DisplayImageOptions ROUND_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().
                    cacheInMemory(true).
                    cacheOnDisk(true).
                    showImageOnLoading(R.drawable.image_loading).
                    displayer(new RoundedBitmapDisplayer(DefaultValues.IMAGE_ROUND_ROUNDED_VALUE)).build();

    private static ImageLoader mImageLoader;

    static {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                AppController.getInstance().getApplicationContext()).
                defaultDisplayImageOptions(DEFAULT_IMAGE_OPTIONS).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
        mImageLoader = ImageLoader.getInstance();
    }

    public static synchronized ImageLoader getImageLoader() {
        return mImageLoader;
    }

    private ImageUtil() {}

    public static void init() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                AppController.getInstance().getApplicationContext()).
                defaultDisplayImageOptions(DEFAULT_IMAGE_OPTIONS).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
        mImageLoader = ImageLoader.getInstance();
    }

    // Community cover image

    public static void displayCommunityCoverImage(long id, ImageView imageView) {
        getImageLoader().displayImage(COMMUNITY_COVER_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayCommunityCoverImage(long id, ImageView imageView, ImageLoadingListener listener) {
        getImageLoader().displayImage(COMMUNITY_COVER_IMAGE_BY_ID_URL + id, imageView, listener);
    }

    public static void displayThumbnailCommunityCoverImage(long id, ImageView imageView) {
        getImageLoader().displayImage(THUMBNAIL_COMMUNITY_COVER_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayThumbnailCommunityCoverImage(long id, ImageView imageView, ImageLoadingListener listener) {
        getImageLoader().displayImage(THUMBNAIL_COMMUNITY_COVER_IMAGE_BY_ID_URL + id, imageView, listener);
    }

    // Cover image

    public static void displayCoverImage(long id, ImageView imageView) {
        getImageLoader().displayImage(COVER_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayCoverImage(long id, ImageView imageView, ImageLoadingListener listener) {
        getImageLoader().displayImage(COVER_IMAGE_BY_ID_URL + id, imageView, listener);
    }

    public static void displayThumbnailCoverImage(long id, ImageView imageView) {
        getImageLoader().displayImage(THUMBNAIL_COVER_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayThumbnailCoverImage(long id, ImageView imageView, ImageLoadingListener listener) {
        getImageLoader().displayImage(THUMBNAIL_COVER_IMAGE_BY_ID_URL + id, imageView, listener);
    }

    // Profile image

    public static void displayProfileImage(ImageView imageView) {
        ImageLoader.getInstance().displayImage(PROFILE_IMAGE_URL, imageView, ROUNDED_CORNERS_IMAGE_OPTIONS);
    }

    public static void displayProfileImage(long id, ImageView imageView) {
        ImageLoader.getInstance().displayImage(PROFILE_IMAGE_BY_ID_URL + id, imageView, ROUNDED_CORNERS_IMAGE_OPTIONS);
    }

    public static void displayProfileImage(long id, ImageView imageView, ImageLoadingListener listener) {
        ImageLoader.getInstance().displayImage(PROFILE_IMAGE_URL + id, imageView, ROUNDED_CORNERS_IMAGE_OPTIONS, listener);
    }

    public static void displayThumbnailProfileImage(long id, ImageView imageView) {
        ImageLoader.getInstance().displayImage(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id, imageView, ROUNDED_CORNERS_IMAGE_OPTIONS);
    }

    public static void displayThumbnailProfileImage(long id, ImageView imageView, ImageLoadingListener listener) {
        ImageLoader.getInstance().displayImage(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id, imageView, ROUNDED_CORNERS_IMAGE_OPTIONS, listener);
    }

    public static void displayMiniProfileImage(long id, ImageView imageView) {
        ImageLoader.getInstance().displayImage(MINI_PROFILE_IMAGE_BY_ID_URL + id, imageView, ROUND_IMAGE_OPTIONS);
    }

    public static void displayMiniProfileImage(long id, ImageView imageView, ImageLoadingListener listener) {
        ImageLoader.getInstance().displayImage(MINI_PROFILE_IMAGE_BY_ID_URL + id, imageView, ROUND_IMAGE_OPTIONS, listener);
    }

    // Post image

    public static void displayPostImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayPostImage: loading "+POST_IMAGE_BY_ID_URL + id);
        getImageLoader().displayImage(POST_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayPostImage(long id, ImageView imageView, ImageLoadingListener listener) {
        Log.d(ImageUtil.class.getSimpleName(), "displayPostImage: loading "+POST_IMAGE_BY_ID_URL + id);
        getImageLoader().displayImage(POST_IMAGE_BY_ID_URL + id, imageView, listener);
    }

    public static void displayOriginalPostImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayOriginalPostImage: loading "+ORIGINAL_POST_IMAGE_BY_ID_URL + id);
        getImageLoader().displayImage(ORIGINAL_POST_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayOriginalPostImage(long id, ImageView imageView, ImageLoadingListener listener) {
        Log.d(ImageUtil.class.getSimpleName(), "displayOriginalPostImage: loading "+ORIGINAL_POST_IMAGE_BY_ID_URL + id);
        getImageLoader().displayImage(ORIGINAL_POST_IMAGE_BY_ID_URL + id, imageView, listener);
    }

    // Generic

    public static void displayImage(String url, ImageView imageView, ImageLoadingListener listener) {
        if (!url.startsWith(AppController.BASE_URL))
            url = AppController.BASE_URL + url;
        getImageLoader().displayImage(url, imageView, listener);
    }

    public static void displayImage(String url, ImageView imageView) {
        if (!url.startsWith(AppController.BASE_URL))
            url = AppController.BASE_URL + url;
        getImageLoader().displayImage(url, imageView);
    }

    public static void displayRoundedCornersImage(String url, ImageView imageView) {
        if (!url.startsWith(AppController.BASE_URL))
            url = AppController.BASE_URL + url;
        ImageLoader.getInstance().displayImage(url, imageView, ROUNDED_CORNERS_IMAGE_OPTIONS);
    }

    public static void displayRoundImage(String url, ImageView imageView) {
        if (!url.startsWith(AppController.BASE_URL))
            url = AppController.BASE_URL + url;
        ImageLoader.getInstance().displayImage(url, imageView, ROUND_IMAGE_OPTIONS);
    }

    public static void clearProfileImageCache(long id){
        clearImageCache(PROFILE_IMAGE_URL + id);
        clearImageCache(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id);
        clearImageCache(MINI_PROFILE_IMAGE_BY_ID_URL + id);
    }

    public static void clearCoverImageCache(long id){
        clearImageCache(COVER_IMAGE_BY_ID_URL + id);
        clearImageCache(THUMBNAIL_COVER_IMAGE_BY_ID_URL + id);
    }

    private static void clearImageCache(String url) {
        File imageFile = mImageLoader.getDiskCache().get(url);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        DiskCacheUtils.removeFromCache(url, mImageLoader.getDiskCache());
        MemoryCacheUtils.removeFromCache(url, mImageLoader.getMemoryCache());
    }

    // Select photo

    public static void openPhotoPicker(Activity activity) {
        openPhotoPicker(activity, activity.getString(R.string.photo_select));
    }

    public static void openPhotoPicker(Activity activity, String title) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, title), SELECT_PICTURE);
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, filePathColumn, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                return filePath;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    public static Bitmap resizeAsPreviewThumbnail(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bp = BitmapFactory.decodeFile(path, opts);

        int orignalHeight = opts.outHeight;
        int orignalWidth = opts.outWidth;
        int resizeScale = 1;

        if ( orignalWidth > PREVIEW_THUMBNAIL_MAX_WIDTH || orignalHeight > PREVIEW_THUMBNAIL_MAX_HEIGHT ) {
            final int widthRatio = Math.round((float) orignalWidth / (float) PREVIEW_THUMBNAIL_MAX_WIDTH);
            final int heightRatio = Math.round((float) orignalHeight / (float) PREVIEW_THUMBNAIL_MAX_HEIGHT);
            resizeScale = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        // put the scale instruction (1 -> scale to (1/1); 8-> scale to 1/8)
        opts.inSampleSize = resizeScale;
        opts.inJustDecodeBounds = false;

        int bmSize = (orignalWidth / resizeScale) * (orignalHeight / resizeScale) * 4;
        if ( Runtime.getRuntime().freeMemory() > bmSize ) {
            bp = BitmapFactory.decodeFile(path, opts);
        } else {
            return null;
        }
        return bp;
    }

    public static Bitmap cropToSquare(Bitmap bitmap) {
        return cropToSquare(bitmap, -1);
    }

    public static Bitmap cropToSquare(Bitmap bitmap, int dimension) {
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int crop = (width - height) / 2;
        crop = (crop < 0)? 0: crop;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, crop, 0, newWidth, newHeight);

        if (dimension != -1)
            cropImg = Bitmap.createScaledBitmap(cropImg, dimension, dimension, false);
        return cropImg;
    }

    public static Drawable getEmptyDrawable() {
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = AppController.getInstance().getResources().getDrawable(R.drawable.empty);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        return d;
    }
}
