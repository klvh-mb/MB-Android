package miniBean.activity;

import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewKGPostActivity extends NewPostActivity {

    private List<CommunitiesWidgetChildVM> bookmarkedSchoolCommunities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_maroon));

        selectCommunityText.setText(R.string.new_post_select_kg_community);

        getBookmarkedSchoolCommunities();
    }

    @Override
    protected List<CommunitiesWidgetChildVM> getMyCommunities() {
        return bookmarkedSchoolCommunities;
    }

    private void getBookmarkedSchoolCommunities() {
        AppController.getApi().getBookmarkedKGCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>() {
            @Override
            public void success(CommunitiesParentVM parent, Response response) {
                if (parent != null) {
                    bookmarkedSchoolCommunities = parent.getCommunities();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}
