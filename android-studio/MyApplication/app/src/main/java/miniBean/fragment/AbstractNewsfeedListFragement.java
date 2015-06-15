package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import miniBean.Listener.InfiniteScrollListener;
import miniBean.R;
import miniBean.activity.DetailActivity;
import miniBean.adapter.NewsfeedListAdapter;
import miniBean.app.TrackedFragment;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunityPostVM;

public abstract class AbstractNewsfeedListFragement extends TrackedFragment {

    private static final String TAG = AbstractNewsfeedListFragement.class.getName();

    protected ListView listView;
    protected BaseAdapter listAdapter;
    protected List<CommunityPostVM> feedItems;
    protected View header,loadingFooter;
    protected TextView footerText;

    protected boolean hasHeader = false;
    protected int headerResouceId = -1;

    protected PullToRefreshView pullListView;

    abstract protected void loadNewsfeed(int offset);

    protected void setHeaderResouce(int resouceId) {
        this.headerResouceId = resouceId;
    }

    protected View getHeaderView() {
        return this.header;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.newsfeed_list_fragment, container, false);

        loadingFooter = inflater.inflate(R.layout.list_loading_footer, null);
        pullListView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);

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

        footerText = (TextView) listView.findViewById(R.id.listLoadingFooterText);

        pullListView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullListView.setRefreshing(false);
                        refreshList();
                    }
                }, DefaultValues.PULL_TO_REFRESH_DELAY);
            }
        });

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

    protected BaseAdapter getAdapterByFlow(String flowName) {
        return new NewsfeedListAdapter(getActivity(), feedItems);
    }

    protected void loadFeedItemsToList(final List<CommunityPostVM> posts) {
        if (feedItems.size() == 0) {
            //Log.d(this.getClass().getSimpleName(), "loadFeedItemsToList: first batch completed");
            feedItems.addAll(posts);
            listAdapter.notifyDataSetChanged();
            showFooter(false);
        } else {
            // NOTE: delay infinite scroll by a short interval to make UI looks smooth
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    feedItems.addAll(posts);
                    listAdapter.notifyDataSetChanged();
                }
            }, DefaultValues.DEFAULT_INFINITE_SCROLL_DELAY);
            showFooter(true);
        }

        if (posts == null || posts.size() == 0) {
            setFooterText(R.string.list_loaded_all);
        } else {
            setFooterText(R.string.list_loading);
        }
    }

    protected void refreshList() {
        feedItems.clear();
        loadNewsfeed(0);
        listAdapter.notifyDataSetChanged();
    }

    protected void setFooterText(int text) {
        showFooter(true);
        footerText.setText(text);
    }

    protected void showFooter(boolean show) {
        loadingFooter.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
