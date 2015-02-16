package miniBean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import miniBean.CommunityActivity;
import miniBean.MyApi;
import miniBean.R;
import miniBean.adapter.CommunityListAdapter;
import miniBean.adapter.DetailListAdapter;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.CommunityPostCommentVM;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.Post;
import miniBean.viewmodel.PostArray;
import miniBean.viewmodel.PostMap;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;


public class DetailActivity extends FragmentActivity {

    public SharedPreferences session = null;
    public MyApi api;
    private ListView listView;
    private DetailListAdapter listAdapter;
    private List<CommunityPostCommentVM> communityItems;
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();
        setContentView(R.layout.community_detail);
        api = restAdapter.create(MyApi.class);

        listView = (ListView)findViewById(R.id.detail_list);



        communityItems = new ArrayList<>();

        listAdapter = new DetailListAdapter(this, communityItems);
        listView.setAdapter(listAdapter);

        Intent intent=getIntent();

        Long postID = intent.getLongExtra("postId",0L);
        Long commID = intent.getLongExtra("commId",0L);
        Long cId,feedId;
        System.out.println("Before getCommunity");
        getCommunityDetail(postID,commID);
        System.out.println("After getCommunity");
    }

    private void getCommunityDetail(long feedId,long cId) {
        System.out.println("In getCommunity");


        api.qnaLanding(feedId,cId, new Callback<PostArray>() {
            @Override
            public void success(PostArray map, retrofit.client.Response response) {
                System.out.println(":::::::: "+map.getPosts());
               // Post postVM = map.getPosts().get(0);

                //communityItems.addAll(postVM.getCs());
                //listAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors

            }
        });
    }


}
