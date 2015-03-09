package miniBean.activity;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import miniBean.R;
import miniBean.app.MyApi;
import miniBean.fragement.NewsFeedFragement;
import miniBean.fragement.UserProfileFragment;
import retrofit.RestAdapter;
import retrofit.client.OkClient;


public class NewsfeedActivity extends FragmentActivity {

    public SharedPreferences session = null;
    public MyApi api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();
        setContentView(R.layout.feed_activity);
        session = getSharedPreferences("prefs", 0);
        api = restAdapter.create(MyApi.class);

        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
       // getActionBar().setCustomView(R.layout.feed_activity);
        String id=getIntent().getStringExtra("id");
        Bundle bundle=new Bundle();
        bundle.putString("id",id);
        System.out.println("newsfeed::"+getIntent().getStringExtra("id"));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        NewsFeedFragement newsfeedFragment = new NewsFeedFragement();
        newsfeedFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.placeHolders, newsfeedFragment).commit();
    }

}


