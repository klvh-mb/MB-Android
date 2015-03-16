package miniBean.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import miniBean.R;
import miniBean.fragement.NewsFeedFragement;

public class NewsfeedActivity extends FragmentActivity {

    public SharedPreferences session = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feed_activity);

        session = getSharedPreferences("prefs", 0);

        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getActionBar().setCustomView(R.layout.feed_activity);
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

