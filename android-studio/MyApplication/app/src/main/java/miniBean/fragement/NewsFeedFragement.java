package miniBean.fragement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import miniBean.Listener.InfiniteScrollListener;
import miniBean.R;
import miniBean.activity.DetailActivity;
import miniBean.adapter.FeedListAdapter;
import miniBean.app.AppController;
import miniBean.app.MyApi;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class NewsFeedFragement extends Fragment {

    private static final String TAG = NewsFeedFragement.class.getName();
    public SharedPreferences session = null;
    public MyApi api;
    ProgressBar progressBarFeed;
    private ListView listView;
    private BaseAdapter listAdapter;
    private List<CommunityPostVM> feedItems;

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
        progressBarFeed = (ProgressBar) view.findViewById(R.id.progressFeed);
        progressBarFeed.setVisibility(View.VISIBLE);
        feedItems = new ArrayList<CommunityPostVM>();
        listAdapter = getAdapterByFlow("");
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                CommunityPostVM post = (CommunityPostVM) listAdapter.getItem(position);
                intent.putExtra("postId", post.getId());
                intent.putExtra("commId", post.getCid());
                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new InfiniteScrollListener(1) {


            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                System.out.println("totalCount::" + totalItemsCount);
                System.out.println("in loadmore::::::::");
                functionCall(0);
            }
        });

        functionCall(0);

        System.out.println("lastid::"+getArguments().getString("id"));
        //Long id=Long.parseLong(getArguments().getString("id"));
        //getUserQuestion(0,id);

        return view;
    }
    public BaseAdapter getAdapterByFlow( String flowName){
        return new FeedListAdapter(getActivity(), feedItems);
    }

    private void functionCall(int offset){
        switch (getArguments().getString("key"))
        {

            case "userquestion":
                System.out.println("case1");
                getUserQuestion(offset,Long.parseLong(getArguments().getString("id")));
                break;
            case "useranswer":
                getUserAnswer(offset,Long.parseLong(getArguments().getString("id")));
                break;
            case "question":
                getUserQuestion(offset,Long.parseLong(getArguments().getString("id")));
                break;
            case "answer":
                getUserAnswer(offset,Long.parseLong(getArguments().getString("id")));
                break;
            case "bookmark":
                getBookmark(offset);
                break;
            case "feed":
                System.out.println("casetest");
                getNewsFeed(offset);
                break;
            default:
                System.out.println("this is default.........");
        }
    }


    private void getNewsFeed(int offset) {
        System.out.println("newsfedd::::");
        api.getNewsfeed(Long.valueOf(offset), session.getString("sessionID", null), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, retrofit.client.Response response) {
                System.out.println("innewsfeed::"+array.getPosts());
                if(array.getPosts() != null)
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
    void getUserQuestion(int offset,Long id)
    {
        AppController.api.getUserPost(Long.valueOf(offset),id,session.getString("sessionID", null), new Callback<PostArray>(){
            @Override
            public void success(PostArray array, Response response2) {
                System.out.println("postarray::"+array.getPosts());
                if(array.getPosts() != null)
                    feedItems.addAll(array.getPosts());
                listAdapter.notifyDataSetChanged();
                progressBarFeed.setVisibility(View.GONE);
                System.out.println("sucess1::::"+array);
            }
            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
    void getUserAnswer(int offset,Long id) {
        AppController.api.getUserComment(Long.valueOf(offset), id, session.getString("sessionID", null), new Callback<PostArray>() {

            @Override
            public void success(PostArray array, Response response2) {
                if(array.getPosts() != null)
                    feedItems.addAll(array.getPosts());
                listAdapter.notifyDataSetChanged();
                progressBarFeed.setVisibility(View.GONE);

                System.out.println("sucess2::::" + array);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });




    }
void getBookmark(int offset)
{
    AppController.api.getBookmark(Long.valueOf(offset),session.getString("sessionID", null),new Callback<PostArray>() {
        @Override
        public void success(PostArray postArray, Response response) {
            if(postArray.getPosts() != null)
                feedItems.addAll(postArray.getPosts());
            listAdapter.notifyDataSetChanged();
            progressBarFeed.setVisibility(View.GONE);

            System.out.println("sucess2::::" + postArray);

        }

        @Override
        public void failure(RetrofitError error) {
            error.printStackTrace();
        }
    });
}
}
