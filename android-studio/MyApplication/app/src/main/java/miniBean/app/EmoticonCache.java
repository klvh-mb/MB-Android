package miniBean.app;

import android.util.Log;

import java.util.List;

import miniBean.util.SharedPreferencesUtil;
import miniBean.viewmodel.EmoticonVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EmoticonCache {

    private static List<EmoticonVM> emoticons;

    private EmoticonCache() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<List<EmoticonVM>> callback) {
        Log.d(EmoticonCache.class.getSimpleName(), "refresh");

        AppController.getApi().getEmoticons(AppController.getInstance().getSessionId(), new Callback<List<EmoticonVM>>() {
            @Override
            public void success(List<EmoticonVM> vms, Response response) {
                emoticons = vms;
                SharedPreferencesUtil.getInstance().saveEmoticons(vms);
                if (callback != null) {
                    callback.success(vms, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
                Log.e(EmoticonCache.class.getSimpleName(), "refresh: failure", error);
            }
        });
    }

    public static List<EmoticonVM> getEmoticons() {
        if (emoticons == null)
            emoticons = SharedPreferencesUtil.getInstance().getEmoticons();
        return emoticons;
    }

    public static void clear() {
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.EMOTICONS);
    }
}
