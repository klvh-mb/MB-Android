package miniBean.app;

import java.util.ArrayList;
import java.util.List;

import miniBean.viewmodel.EmoticonVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EmoticonCache {

    private static List<EmoticonVM> emoticons = new ArrayList<>();

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
        if (emoticons.isEmpty()) {
            AppController.getApi().getEmoticons(AppController.getInstance().getSessionId(), new Callback<List<EmoticonVM>>() {
                @Override
                public void success(List<EmoticonVM> vms, Response response) {
                    emoticons = vms;
                    if (callback != null) {
                        callback.success(emoticons, response);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        }
    }

    public static List<EmoticonVM> getEmoticons() {
        return emoticons;
    }

    public static void clear() {
        emoticons = null;
    }
}
