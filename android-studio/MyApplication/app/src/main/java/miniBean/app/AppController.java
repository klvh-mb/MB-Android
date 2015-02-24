package miniBean.app;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import miniBean.R;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    public static ImageLoader mImageLoader;
    public static MyApi api;
    private static AppController mInstance;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();

        api = restAdapter.create(MyApi.class);
        System.out.println(" :::::::::::::::::::::::::::::::::::::::::::::::::::::::: ");
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config); //
        mImageLoader = ImageLoader.getInstance();

    }

}