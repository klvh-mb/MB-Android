package miniBean.fragement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import miniBean.CommunityActivity;
import miniBean.DetailActivity;
import miniBean.InfiniteScrollListener;
import miniBean.MyApi;
import miniBean.R;
import miniBean.adapter.FeedListAdapter;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.Post;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;


public class NewsFeedFragement extends Fragment {

    private static final String TAG = NewsFeedFragement.class.getName();
    public SharedPreferences session = null;
    public MyApi api;
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<Post> feedItems;
    ProgressBar progressBarFeed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //getMaEventsSao().addCacheChangedListener(this);
        View view = inflater.inflate(R.layout.newsfeed_activity, container, false);
        session = getActivity().getSharedPreferences("prefs", 0);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();

        api = restAdapter.create(MyApi.class);

        listView = (ListView) view.findViewById(R.id.list);
        progressBarFeed= (ProgressBar) view.findViewById(R.id.progressFeed);
        progressBarFeed.setVisibility(View.VISIBLE);



        feedItems = new ArrayList<Post>();


        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), DetailActivity.class);
                Post post = listAdapter.getItem(position);
                intent.putExtra("postId",post.getId());
                System.out.println("postid+"+post.getId());
                intent.putExtra("commId",post.getCid());
                System.out.println("commid+"+post.getCid());
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new InfiniteScrollListener(5) {

            @Override
            public void loadMore(int page, int totalItemsCount) {
                // TODO Auto-generated method stub
                System.out.println("Page ::::::::::: " + (page - 1));
                getNewsFeed(page - 1);
            }
        });
        getNewsFeed(0);

        return view;
    }

    private void getNewsFeed(int offset) {
        api.getNewsfeed(Long.valueOf(offset), session.getString("sessionID", null), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, retrofit.client.Response response) {
                feedItems.addAll(array.getPosts());
                listAdapter.notifyDataSetChanged();
                progressBarFeed.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }


}
