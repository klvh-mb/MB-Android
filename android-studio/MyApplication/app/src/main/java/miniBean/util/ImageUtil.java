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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.io.FileOutputStream;

import miniBean.R;
import miniBean.app.AppController;

/**
 * Created by keithlei on 3/16/15.
 */
public class ImageUtil {

    public static final String MINIBEAN_TEMP_DIR_NAME = "miniBean";

    public static final int PREVIEW_THUMBNAIL_MAX_WIDTH = 350;
    public static final int PREVIEW_THUMBNAIL_MAX_HEIGHT = 350;

    public static final int IMAGE_UPLOAD_MAX_WIDTH = 1024;
    public static final int IMAGE_UPLOAD_MAX_HEIGHT = 1024;

    public static final int IMAGE_COMPRESS_QUALITY = 85;

    public static final String COMMUNITY_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-cover-community-image-by-id/";
    public static final String THUMBNAIL_COMMUNITY_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-cover-community-image-by-id/";
    public static final String COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-cover-image-by-id/";
    public static final String THUMBNAIL_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-cover-image-by-id/";
    public static final String PROFILE_IMAGE_URL = AppController.BASE_URL + "/image/get-profile-image-by-id/";
    public static final String PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-profile-image-by-id/";
    public static final String THUMBNAIL_PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-image-by-id/";
    public static final String POST_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-post-image-by-id/";
    public static final String ORIGINAL_POST_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-original-post-image-by-id/";
    //public static final String COMMENT_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-comment-image-by-id/";
    public static final String MESSAGE_IMAGE_BY_ID_URL= AppController.BASE_URL + "/image/get-message-image-by-id/";
    public static final String ORIGINAL_MESSAGE_IMAGE_BY_ID_URL= AppController.BASE_URL + "/image/get-original-private-image-by-id/";

    public static DisplayImageOptions DEFAULT_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().
                    cacheInMemory(true).
                    cacheOnDisk(true).
                    bitmapConfig(Bitmap.Config.RGB_565).
                    imageScaleType(ImageScaleType.IN_SAMPLE_INT).
                    showImageOnLoading(R.drawable.image_loading).
                    displayer(new RoundedBitmapDisplayer(0)).build();

    public static DisplayImageOptions ROUNDED_CORNERS_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().
                    cacheInMemory(true).
                    cacheOnDisk(true).
                    bitmapConfig(Bitmap.Config.RGB_565).
                    imageScaleType(ImageScaleType.IN_SAMPLE_INT).
                    showImageOnLoading(R.drawable.image_loading).
                    displayer(new RoundedBitmapDisplayer(DefaultValues.IMAGE_ROUNDED_CORNERS_RADIUS)).build();

    public static DisplayImageOptions THIN_ROUNDED_CORNERS_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().
                    cacheInMemory(true).
                    cacheOnDisk(true).
                    bitmapConfig(Bitmap.Config.RGB_565).
                    imageScaleType(ImageScaleType.IN_SAMPLE_INT).
                    showImageOnLoading(R.drawable.image_loading).
                    displayer(new RoundedBitmapDisplayer(DefaultValues.IMAGE_THIN_ROUNDED_CORNERS_RADIUS)).build();

    public static DisplayImageOptions ROUND_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().
                    cacheInMemory(true).
                    cacheOnDisk(true).
                    bitmapConfig(Bitmap.Config.RGB_565).
                    imageScaleType(ImageScaleType.IN_SAMPLE_INT).
                    showImageOnLoading(R.drawable.image_loading).
                    displayer(new RoundedBitmapDisplayer(DefaultValues.IMAGE_ROUND_RADIUS)).build();

    private static ImageLoader mImageLoader;

    private static File tempDir;

    static {
        init();
    }

    public static synchronized ImageLoader getImageLoader() {
        return mImageLoader;
    }

    private ImageUtil() {}

    public static void init() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                AppController.getInstance().getApplicationContext()).
                threadPoolSize(5).
                threadPriority(Thread.MIN_PRIORITY + 3).
                denyCacheImageMultipleSizesInMemory().
                memoryCache(new WeakMemoryCache()).
                defaultDisplayImageOptions(DEFAULT_IMAGE_OPTIONS).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
        mImageLoader = ImageLoader.getInstance();

        initImageTempDir();
    }

    // miniBean temp directory

    public static void initImageTempDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalRoot = Environment.getExternalStorageDirectory();
            Log.d(ImageUtil.class.getSimpleName(), "initImageTempDir: externalRoot="+externalRoot.getAbsolutePath());

            tempDir = new File(externalRoot, MINIBEAN_TEMP_DIR_NAME);
            if (!tempDir.exists()) {
                tempDir.mkdir();
                Log.d(ImageUtil.class.getSimpleName(), "initImageTempDir: create tempDir=" + tempDir.getAbsolutePath());
            } else {
                clearTempDir();
            }
        } else {
            Log.e(ImageUtil.class.getSimpleName(), "initImageTempDir: no external storage!!!");
            tempDir = null;
        }
    }

    public static File getTempDir() {
        return tempDir;
    }

    private static void clearTempDir() {
        if (tempDir != null && tempDir.exists()) {
            File[] children = tempDir.listFiles();
            for (File f : children) {
                if (!f.isDirectory()) {
                    f.delete();
                }
            }
        }
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
        ImageLoader.getInstance().displayImage(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id, imageView, ROUND_IMAGE_OPTIONS);
    }

    public static void displayThumbnailProfileImage(long id, ImageView imageView, ImageLoadingListener listener) {
        ImageLoader.getInstance().displayImage(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id, imageView, ROUND_IMAGE_OPTIONS, listener);
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

    public static void displayMessageImage(long id, ImageView imageView, ImageLoadingListener listener) {
        Log.d(ImageUtil.class.getSimpleName(), "displayPostImage: loading "+MESSAGE_IMAGE_BY_ID_URL + id);
        getImageLoader().displayImage(MESSAGE_IMAGE_BY_ID_URL + id, imageView, listener);
    }

    public static void displayOriginalMessageImage(long id, ImageView imageView, ImageLoadingListener listener) {
        Log.d(ImageUtil.class.getSimpleName(), "displayPostImage: loading "+ORIGINAL_MESSAGE_IMAGE_BY_ID_URL + id);
        getImageLoader().displayImage(ORIGINAL_MESSAGE_IMAGE_BY_ID_URL + id, imageView, listener);
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

    public static void displayThinRoundedCornersImage(String url, ImageView imageView) {
        if (!url.startsWith(AppController.BASE_URL))
            url = AppController.BASE_URL + url;
        ImageLoader.getInstance().displayImage(url, imageView, THIN_ROUNDED_CORNERS_IMAGE_OPTIONS);
    }

    public static void displayRoundImage(String url, ImageView imageView) {
        if (!url.startsWith(AppController.BASE_URL))
            url = AppController.BASE_URL + url;
        ImageLoader.getInstance().displayImage(url, imageView, ROUND_IMAGE_OPTIONS);
    }

    public static void clearProfileImageCache(long id){
        clearImageCache(PROFILE_IMAGE_URL + id);
        clearImageCache(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id);
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
        activity.startActivityForResult(Intent.createChooser(intent, title), ViewUtil.SELECT_PICTURE_REQUEST_CODE);
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
        return resizeImage(path, PREVIEW_THUMBNAIL_MAX_WIDTH, PREVIEW_THUMBNAIL_MAX_HEIGHT);
    }

    public static Bitmap resizeToUpload(String path) {
        return resizeImage(path, IMAGE_UPLOAD_MAX_WIDTH, IMAGE_UPLOAD_MAX_HEIGHT);
    }

    public static Bitmap resizeImage(String path, int maxWidth, int maxHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bp = BitmapFactory.decodeFile(path, opts);

        int originalHeight = opts.outHeight;
        int originalWidth = opts.outWidth;
        int resizeScale = 1;

        Log.d(ImageUtil.class.getSimpleName(), "resizeImage: outWidth="+originalWidth+" outHeight="+originalHeight);
        if ( originalWidth > maxWidth || originalHeight > maxHeight ) {
            final int widthRatio = Math.round((float) originalWidth / (float) maxWidth);
            final int heightRatio = Math.round((float) originalHeight / (float) maxHeight);
            resizeScale = heightRatio < widthRatio ? heightRatio : widthRatio;
            Log.d(ImageUtil.class.getSimpleName(), "resizeImage: resizeScale="+resizeScale);
        }

        // put the scale instruction (1 -> scale to (1/1); 8-> scale to 1/8)
        opts.inSampleSize = resizeScale;
        opts.inJustDecodeBounds = false;

        /*
        int bmSize = (originalWidth / resizeScale) * (originalHeight / resizeScale) * 4;
        if ( Runtime.getRuntime().freeMemory() > bmSize ) {
            bp = BitmapFactory.decodeFile(path, opts);
        } else {
            return null;
        }
        */

        bp = BitmapFactory.decodeFile(path, opts);
        return bp;
    }

    public static File resizeAsJPG(File image) {
        return resizeAsFormat(Bitmap.CompressFormat.JPEG, image);
    }

    public static File resizeAsFormat(Bitmap.CompressFormat format, File image) {
        if (tempDir == null) {
            Log.e(ImageUtil.class.getSimpleName(), "resizeAsFormat: tempDir is null!!!");
            return image;
        }

        File resizedImage = new File(tempDir, image.getName());
        if (!tempDir.canWrite()) {
            Log.e(ImageUtil.class.getSimpleName(), "resizeAsFormat: "+tempDir.getAbsolutePath()+" cannot be written!!!");
            return image;
        }

        try {
            FileOutputStream out = new FileOutputStream(resizedImage);
            Bitmap resizedBitmap = ImageUtil.resizeToUpload(image.getAbsolutePath());
            resizedBitmap.compress(format, IMAGE_COMPRESS_QUALITY, out);
            Log.d(ImageUtil.class.getSimpleName(), "resizeAsFormat: successfully resized to path=" + resizedImage.getAbsolutePath());
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (Exception e) {
            Log.e(ImageUtil.class.getSimpleName(), "resizeAsFormat: " + e.getMessage(), e);
        }

        return resizedImage;
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
