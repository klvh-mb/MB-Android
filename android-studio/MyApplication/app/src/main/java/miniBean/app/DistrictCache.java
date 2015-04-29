package miniBean.app;

import android.util.Log;

import java.util.List;

import miniBean.viewmodel.LocationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DistrictCache {

    private static List<LocationVM> districts;

    private DistrictCache() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<List<LocationVM>> callback) {
        Log.d(DistrictCache.class.getSimpleName(), "refresh");

        AppController.getApi().getAllDistricts(AppController.getInstance().getSessionId(),new Callback<List< LocationVM>>(){
            @Override
            public void success(List<LocationVM> locationVMs, Response response) {
                districts = locationVMs;
                if (callback != null) {
                    callback.success(districts, response);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }

    public static List<LocationVM> getDistricts() {
        return districts;
    }

    public static void clear() {
        districts.clear();
        districts = null;
    }
}
