package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public abstract class AbstractSchoolCommunityFragment extends MyFragment {

    protected NewsfeedListAdapter feedListAdapter;
    protected List<CommunityPostVM> feedItems;

    protected View listHeader, loadingFooter;
    protected ListView listView;

    abstract protected View getListHeader(LayoutInflater inflater);

    abstract protected String getIntentFlag();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.school_community_fragment, container, false);

        // header
        listHeader = getListHeader(inflater);

        // footer
        loadingFooter = inflater.inflate(R.layout.list_loading_footer, null);

        // list
        listView = (ListView) view.findViewById(R.id.postList);
        listView.addHeaderView(listHeader);
        listView.addFooterView(loadingFooter);      // need to add footer before set adapter

        getNewsFeedByCommunityId(getArguments().getLong("commId"));

        feedItems = new ArrayList<>();
        feedListAdapter = new NewsfeedListAdapter(getActivity(), feedItems, false);
        listView.setAdapter(feedListAdapter);

        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
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
                CommunityPostVM post = feedListAdapter.getItem(position - headerViewsCount);
                if (post != null) {
                    intent.putExtra("postId", post.getId());
                    intent.putExtra("commId", post.getCid());
                    intent.putExtra("id", getArguments().getLong("id"));
                    intent.putExtra("commId", getArguments().getLong("commId"));
                    intent.putExtra("flag", getIntentFlag());
                    startActivity(intent);
                }
            }
        });

        listView.setOnScrollListener(new InfiniteScrollListener(
                DefaultValues.DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD, true, true) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadingFooter.setVisibility(View.VISIBLE);
                loadNewsfeed(
                        getArguments().getLong("id"),
                        feedItems.get(feedItems.size() - 1).getUt() + "",       // NOTE: use updateTime not createTime!!
                        page - 1);
            }
        });

        listView.post(new Runnable() {
            @Override
            public void run() {
                if ("FromCommentImage".equals(getArguments().getString("flag"))) {
                    //listView.smoothScrollToPosition(1);
                } else {
                    listView.scrollTo(0, 0);
                }
            }
        });

        return view;
    }

    protected void getNewsFeedByCommunityId(Long commId) {
        AppController.getApi().getCommunityInitialPosts(commId, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, Response response) {
                if (array.getPosts().isEmpty())
                    setFooterText(R.string.list_no_posts);

                feedItems.addAll(array.getPosts());
                feedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    protected void loadNewsfeed(Long id, String date, int offset) {
        setFooterText(R.string.list_loading);
        AppController.getApi().getCommunityNextPosts(id, date, AppController.getInstance().getSessionId(), new Callback<List<CommunityPostVM>>() {
            @Override
            public void success(List<CommunityPostVM> communityPostVMs, Response response) {
                if (communityPostVMs == null || communityPostVMs.size() == 0) {
                    setFooterText(R.string.list_loaded_all);
                }

                feedItems.addAll(communityPostVMs);
                feedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    protected void setFooterText(int text) {
        TextView footerText = (TextView) listView.findViewById(R.id.listLoadingFooterText);
        footerText.setText(text);
    }
}

