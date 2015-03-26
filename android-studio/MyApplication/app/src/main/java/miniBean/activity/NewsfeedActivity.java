package miniBean.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import miniBean.R;
import miniBean.fragement.NewsfeedListFragement;

public class NewsfeedActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newsfeed_activity);

        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getActionBar().setCustomView(R.layout.newsfeed_activity);
        String id = getIntent().getStringExtra("id");
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        System.out.println("newsfeed::"+getIntent().getStringExtra("id"));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        NewsfeedListFragement newsfeedFragment = new NewsfeedListFragement();
        newsfeedFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.placeHolders, newsfeedFragment).commit();
    }

}


