package miniBean.app;

import android.util.Log;

import miniBean.viewmodel.NotificationsParentVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationCache {

    private static NotificationsParentVM notificationsParentVM;

    private NotificationCache() {}

    static {
        init();
    }

    private static void init() {

    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<NotificationsParentVM> callback) {
        Log.d(NotificationCache.class.getSimpleName(), "refresh");

        AppController.getApi().getHeaderBarData(AppController.getInstance().getSessionId(), new Callback<NotificationsParentVM>() {
            @Override
            public void success(NotificationsParentVM vm, Response response) {
                Log.d(NotificationCache.class.getSimpleName(), "refresh.success: user=" + vm.getName() + " request=" + vm.getRequestCounts() + " notif=" + vm.getNotifyCounts());
                notificationsParentVM = vm;
                if (callback != null) {
                    callback.success(notificationsParentVM, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public static NotificationsParentVM getNotifications() {
        return notificationsParentVM;
    }

    public static void clear() {
        notificationsParentVM = null;
    }
}
