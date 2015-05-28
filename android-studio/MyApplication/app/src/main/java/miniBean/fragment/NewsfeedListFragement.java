package miniBean.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewsfeedListFragement extends AbstractNewsfeedListFragement {

    private static final String TAG = NewsfeedListFragement.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    protected void loadNewsfeed(int offset) {
        Log.d(this.getClass().getSimpleName(), "InfiniteScrollListener offset="+offset+" with key="+getArguments().getString("key"));
        switch (getArguments().getString("key")) {
            case "userquestion":
                getUserQuestion(offset,getArguments().getLong("id"));
                break;
            case "useranswer":
                getUserAnswer(offset,getArguments().getLong("id"));
                break;
            case "question":
                getUserQuestion(offset,getArguments().getLong("id"));
                break;
            case "answer":
                getUserAnswer(offset,getArguments().getLong("id"));
                break;
            case "bookmark":
                getBookmark(offset);
                break;
            case "feed":
                getNewsFeed(offset);
                break;
            default:
                Log.w(this.getClass().getSimpleName(), "InfiniteScrollListener unknown default case with key - "+getArguments().getString("key"));
        }
    }

    private void getNewsFeed(int offset) {
        AppController.getApi().getNewsfeed(Long.valueOf(offset), AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(final PostArray array, retrofit.client.Response response) {
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                error.printStackTrace();
            }
        });
    }

    private void getUserQuestion(int offset,Long id) {
        AppController.getApi().getUserPosts(Long.valueOf(offset), id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, Response response2) {
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                error.printStackTrace();
            }
        });
    }

    private void getUserAnswer(int offset,Long id) {
        AppController.getApi().getUserComments(Long.valueOf(offset), id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {

            @Override
            public void success(PostArray array, Response response2) {
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                error.printStackTrace();
            }
        });
    }

    private void getBookmark(int offset) {
        AppController.getApi().getBookmarkedPosts(Long.valueOf(offset), AppController.getInstance().getSessionId(), new Callback<List<CommunityPostVM>>() {
            @Override
            public void success(List<CommunityPostVM> posts, Response response) {
                loadFeedItemsToList(posts);
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                error.printStackTrace();
            }
        });
    }
}