package miniBean.app;

import android.util.Log;

import miniBean.util.SharedPreferencesUtil;
import miniBean.viewmodel.GameAccountVM;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class UserInfoCache {

    private static UserVM userInfo;
    private static GameAccountVM gameAccount;

    private UserInfoCache() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null, null);
    }

    public static void refresh(final Callback<UserVM> userCallback, final Callback<GameAccountVM> gameAccountCallback) {
        refresh(AppController.getInstance().getSessionId(), userCallback, gameAccountCallback);
    }

    /**
     * For login screen
     * @param sessionId
     * @param userCallback
     * @param gameAccountCallback
     */
    public static void refresh(final String sessionId, final Callback<UserVM> userCallback, final Callback<GameAccountVM> gameAccountCallback) {
        Log.d(UserInfoCache.class.getSimpleName(), "refresh");

        AppController.getApi().getUserInfo(sessionId, new Callback<UserVM>() {
            @Override
            public void success(UserVM userVM, retrofit.client.Response response) {
                userInfo = userVM;
                SharedPreferencesUtil.getInstance().saveUserInfo(userVM);
                if (userCallback != null) {
                    userCallback.success(userVM, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (userCallback != null) {
                    userCallback.failure(error);
                }
                Log.e(UserInfoCache.class.getSimpleName(), "refresh.api.getUserInfo: failure", error);
            }
        });

        AppController.getApi().getGameAccount(sessionId, new Callback<GameAccountVM>() {
            @Override
            public void success(GameAccountVM gameAccountVM, retrofit.client.Response response) {
                gameAccount = gameAccountVM;
                SharedPreferencesUtil.getInstance().saveGameAccount(gameAccountVM);
                if (gameAccountCallback != null) {
                    gameAccountCallback.success(gameAccountVM, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (gameAccountCallback != null) {
                    gameAccountCallback.failure(error);
                }
                Log.e(UserInfoCache.class.getSimpleName(), "refresh.api.getGameAccount: failure", error);
            }
        });
    }

    public static UserVM getUser() {
        if (userInfo == null)
            userInfo = SharedPreferencesUtil.getInstance().getUserInfo();
        return userInfo;
    }

    public static GameAccountVM getGameAccount() {
        if (gameAccount == null)
            gameAccount = SharedPreferencesUtil.getInstance().getGameAccount();
        return gameAccount;
    }

    public static void clear() {
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.USER_INFO);
    }
}
