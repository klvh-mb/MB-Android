package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import miniBean.Listener.InfiniteScrollListener;
import miniBean.R;
import miniBean.activity.DetailActivity;
import miniBean.adapter.NewsfeedListAdapter;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewsfeedListFragement extends Fragment {

    private static final String TAG = NewsfeedListFragement.class.getName();
    private ListView listView;
    private BaseAdapter listAdapter;
    private List<CommunityPostVM> feedItems;
    private View header,loadingFooter;

    private boolean hasHeader = false;
    private int headerResouceId = -1;

    public void setHeader(int resouceId) {
        this.headerResouceId = resouceId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.newsfeed_list_fragment, container, false);

        loadingFooter = inflater.inflate(R.layout.list_loading_footer, null);

        feedItems = new ArrayList<CommunityPostVM>();

        listView = (ListView) view.findViewById(R.id.list);
        if (headerResouceId != -1) {
            header = inflater.inflate(headerResouceId, null);
            listView.addHeaderView(header);
            hasHeader = true;
        }
        listView.addFooterView(loadingFooter);      // need to add footer before set adapter
        listAdapter = getAdapterByFlow("");
        listView.setAdapter(listAdapter);
        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = listView.getHeaderViewsCount();
                if (position < headerViewsCount) {
                    // listview header
                    return;
                }

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                CommunityPostVM post = (CommunityPostVM) listAdapter.getItem(position - headerViewsCount);
                if (post != null) {
                    intent.putExtra("postId", post.getId());
                    intent.putExtra("commId", post.getCid());
                    intent.putExtra("flag","FromNewsfeed");
                    startActivity(intent);
                }
            }
        });

        Log.d(this.getClass().getSimpleName(), "onCreateView: setOnScrollListener");

        // pass hasFooter = true to InfiniteScrollListener
        listView.setOnScrollListener(new InfiniteScrollListener(
                DefaultValues.DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD, hasHeader, true) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadingFooter.setVisibility(View.VISIBLE);
                loadNewsfeed(page-1);
            }
        });

        loadNewsfeed(0);

        return view;
    }

    public BaseAdapter getAdapterByFlow(String flowName) {
        return new NewsfeedListAdapter(getActivity(), feedItems);
    }

    private void setFooterText(int text) {
        TextView footerText = (TextView) listView.findViewById(R.id.listLoadingFooterText);
        footerText.setText(text);
    }

    private void loadNewsfeed(int offset){
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

    private void loadFeedItemsToList(final List<CommunityPostVM> posts) {
        if (posts == null || posts.size() == 0) {
            setFooterText(R.string.list_loaded_all);
        } else {
            setFooterText(R.string.list_loading);
        }

        // NOTE: delay infinite scroll by a short interval to make UI looks smooth
        if (feedItems.size() == 0) {
            Log.d(this.getClass().getSimpleName(), "loadFeedItemsToList: first batch completed");
            feedItems.addAll(posts);
            listAdapter.notifyDataSetChanged();
        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    feedItems.addAll(posts);
                    listAdapter.notifyDataSetChanged();
                }
            }, DefaultValues.DEFAULT_INFINITE_SCROLL_DELAY);
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
