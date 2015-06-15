package miniBean.app;

import android.util.Log;

import java.util.List;

import miniBean.util.SharedPreferencesUtil;
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

        AppController.getApi().getAllDistricts(AppController.getInstance().getSessionId(), new Callback<List<LocationVM>>() {
            @Override
            public void success(List<LocationVM> vms, Response response) {
                districts = vms;
                SharedPreferencesUtil.getInstance().saveDistricts(vms);
                if (callback != null) {
                    callback.success(vms, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
                Log.e(DistrictCache.class.getSimpleName(), "refresh: failure", error);
            }
        });
    }

    public static List<LocationVM> getDistricts() {
        if (districts == null)
            districts = SharedPreferencesUtil.getInstance().getDistricts();
        return districts;
    }

    public static void clear() {
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.DISTRICTS);
    }
}
