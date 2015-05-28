package miniBean.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SchoolsNewsfeedListFragement extends AbstractNewsfeedListFragement {

    private static final String TAG = SchoolsNewsfeedListFragement.class.getName();

    private boolean isPN = false;

    public void setIsPN(boolean isPN) {
        this.isPN = isPN;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    protected void loadNewsfeed(int offset) {
        Callback<PostArray> callback = new Callback<PostArray>() {
            @Override
            public void success(final PostArray array, Response response) {
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                error.printStackTrace();
            }
        };

        if (isPN) {
            AppController.getApi().getPNNewsfeed(Long.valueOf(offset), AppController.getInstance().getSessionId(), callback);
        } else {
            AppController.getApi().getKGNewsfeed(Long.valueOf(offset), AppController.getInstance().getSessionId(), callback);
        }
    }
}