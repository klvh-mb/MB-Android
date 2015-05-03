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

public class KindyNewsfeedListFragement extends Fragment {

    private static final String TAG = KindyNewsfeedListFragement.class.getName();
    private ListView listView;
    private BaseAdapter listAdapter;
    private List<CommunityPostVM> feedItems;
    private View loadingFooter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.newsfeed_list_fragment, container, false);

        loadingFooter = inflater.inflate(R.layout.list_loading_footer, null);

        feedItems = new ArrayList<CommunityPostVM>();

        listView = (ListView) view.findViewById(R.id.list);
        listView.addFooterView(loadingFooter);      // need to add footer before set adapter
        listAdapter = getAdapterByFlow("");
        listView.setAdapter(listAdapter);
        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                CommunityPostVM post = (CommunityPostVM) listAdapter.getItem(position);
                if (post != null) {
                    intent.putExtra("postId", post.getId());
                    intent.putExtra("commId", post.getCid());
                    intent.putExtra("flag","FromSchool");
                    startActivity(intent);
                }
            }
        });

        Log.d(this.getClass().getSimpleName(), "onCreateView: setOnScrollListener");

        // pass hasFooter = true to InfiniteScrollListener
        listView.setOnScrollListener(new InfiniteScrollListener(
                DefaultValues.DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD, false, true) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadingFooter.setVisibility(View.VISIBLE);
               // getNewsFeed(page - 1);
            }
        });

       // getNewsFeed(0);

        return view;
    }

    public BaseAdapter getAdapterByFlow(String flowName) {
        return new NewsfeedListAdapter(getActivity(), feedItems);
    }

    private void setFooterText(int text) {
        TextView footerText = (TextView) listView.findViewById(R.id.listLoadingFooterText);
        footerText.setText(text);
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
        AppController.getApi().getPNNewsfeed(Long.valueOf(offset), AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(final PostArray array, Response response) {
                System.out.println("postarray::::::"+array.getPosts().size());
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                error.printStackTrace();
            }
        });
    }

}
