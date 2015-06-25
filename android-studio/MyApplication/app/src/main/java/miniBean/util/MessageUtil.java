package miniBean.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import miniBean.R;
import miniBean.activity.MessageDetailActivity;
import miniBean.app.AppController;
import miniBean.viewmodel.ConversationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MessageUtil {

    private Activity activity;

    public MessageUtil(Activity activity) {
        this.activity = activity;
    }

    public void startConversation(final long userId) {
        AppController.getApi().startConversation(userId, AppController.getInstance().getSessionId(), new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> conversationVMs, Response response1) {
                ConversationVM conversationVM = conversationVMs.get(0);
                if (conversationVM != null && conversationVM.getUid() == userId) {
                    startMessageDetailActivity(conversationVM.getId(), conversationVM.getUid(), conversationVM.getNm());
                } else {
                    Toast.makeText(activity, activity.getString(R.string.pm_start_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(activity, activity.getString(R.string.pm_start_failed), Toast.LENGTH_SHORT).show();
                Log.e(MessageUtil.class.getSimpleName(), "startConversation: failure", error);
            }
        });
    }

    public void startMessageDetailActivity(long conversationId,  long userId, String userDisplayname) {
        Log.d(this.getClass().getSimpleName(), "startMessageDetailActivity with userId - " + userId);
        Intent intent = new Intent(activity, MessageDetailActivity.class);
        intent.putExtra("user_name", userDisplayname);
        intent.putExtra("uid", userId);
        intent.putExtra("cid", conversationId);
        activity.startActivity(intent);
    }
}
