package miniBean.app;

import android.util.Log;

import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class UserInfoCache {

    private static UserVM userVM;

    private UserInfoCache() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<UserVM> callback) {
        refresh(AppController.getInstance().getSessionId(), callback);
    }

    public static void refresh(final String sessionId, final Callback<UserVM> callback) {
        Log.d(UserInfoCache.class.getSimpleName(), "refresh");

        AppController.getApi().getUserInfo(sessionId, new Callback<UserVM>() {
            @Override
            public void success(UserVM user, retrofit.client.Response response) {
                userVM = user;
                if (callback != null) {
                    callback.success(userVM, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public static UserVM getUser() {
        return userVM;
    }

    public static void clear() {
        userVM = null;
    }
}
