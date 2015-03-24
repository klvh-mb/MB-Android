package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import miniBean.R;
import miniBean.fragement.NewsFeedFragement;

public class MyNewsfeedActivity extends FragmentActivity {

    TextView titleText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newsfeed_activity);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.newsfeed_actionbar);

        titleText= (TextView) findViewById(R.id.titleAction);

        String id = getIntent().getStringExtra("id");
        String key=getIntent().getStringExtra("key");

        switch (getIntent().getStringExtra("key"))
        {
            case "question":
                            titleText.setText("Question");
                            break;
            case "answer":
                            titleText.setText("Answer");
                            break;
            case "bookmark":
                            titleText.setText("Bookmark");
                            break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("key",key);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        NewsFeedFragement newsfeedFragment = new NewsFeedFragement();
        newsfeedFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.placeHolders, newsfeedFragment).commit();
    }

}


