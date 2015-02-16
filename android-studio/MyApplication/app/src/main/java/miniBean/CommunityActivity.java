
package miniBean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import miniBean.adapter.FeedListAdapter;
import miniBean.app.AppController;
import miniBean.viewmodel.Post;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

public class CommunityActivity extends FragmentActivity {

    TextView noPost,noMember;
    ListView listView;
    FeedListAdapter feedListAdapter;
    List<Post> feedItems;
    public SharedPreferences session = null;
    public SharedPreferences mPref = null;
    public MyApi api;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();
        session = getSharedPreferences("prefs", 0);
        setContentView(R.layout.community_view);
        api = restAdapter.create(MyApi.class);

        progressBar= (ProgressBar) findViewById(R.id.progressCommunity);
        progressBar.setVisibility(View.VISIBLE);


        noPost= (TextView) findViewById(R.id.noPostComm);
        noMember= (TextView) findViewById(R.id.noMemberComm);

        feedItems = new ArrayList<Post>();
        feedListAdapter = new FeedListAdapter(this, feedItems);
        listView= (ListView) findViewById(R.id.listCommunityFeed);
        listView.setAdapter(feedListAdapter);

        Intent intent=getIntent();

        String name=intent.getStringExtra("commName");//selected community name

        String totalMember=intent.getStringExtra("noMember");
        noMember.setText(totalMember);

        String totalPost=intent.getStringExtra("noPost");
        noPost.setText(totalPost);

        String id=intent.getStringExtra("id");
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView communityPic = (NetworkImageView) findViewById(R.id.communityPic);
        communityPic.setImageUrl(this.getResources().getString(R.string.base_url) + "/image/get-cover-image-by-id/" + id, imageLoader);
        getNewsFeedByCommuityId(Long.parseLong(id));

    }

    private void getNewsFeedByCommuityId(long id) {
        api.getCommNewsfeed(id, session.getString("sessionID", null), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, retrofit.client.Response response) {
                feedItems.addAll(array.getPosts());
                feedListAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }


}
