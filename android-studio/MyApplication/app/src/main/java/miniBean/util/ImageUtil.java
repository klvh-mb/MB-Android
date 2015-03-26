package miniBean.util;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import miniBean.R;
import miniBean.app.AppController;

/**
 * Created by keithlei on 3/16/15.
 */
public class ImageUtil {

    public static final String COMMUNITY_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-cover-community-image-by-id/";
    public static final String THUMBNAIL_COMMUNITY_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-cover-community-image-by-id/";
    public static final String COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-cover-image-by-id/";
    public static final String THUMBNAIL_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-cover-image-by-id/";
    public static final String PROFILE_IMAGE_URL = AppController.BASE_URL + "/image/get-profile-image-by-id/";
    public static final String PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-profile-image-by-id/";
    public static final String THUMBNAIL_PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-image-by-id/";
    public static final String MINI_PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-mini-image-by-id/";

    public static DisplayImageOptions DEFAULT_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().
                    cacheInMemory(true).
                    showImageOnLoading(R.drawable.image_loading).
                    displayer(new RoundedBitmapDisplayer(0)).build();

    public static DisplayImageOptions ROUNDED_CORNERS_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().
                    cacheInMemory(true).
                    showImageOnLoading(R.drawable.image_loading).
                    displayer(new RoundedBitmapDisplayer(DefaultValues.IMAGE_CORNERS_ROUNDED_VALUE)).build();

    public static DisplayImageOptions ROUND_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().
                    cacheInMemory(true).
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
}
