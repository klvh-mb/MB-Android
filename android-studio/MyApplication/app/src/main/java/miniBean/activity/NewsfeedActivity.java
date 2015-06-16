package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import miniBean.R;
import miniBean.app.TrackedFragmentActivity;
import miniBean.fragment.NewsfeedListFragement;

public class NewsfeedActivity extends TrackedFragmentActivity {

    private TextView titleText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newsfeed_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.newsfeed_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        titleText = (TextView) findViewById(R.id.title);

        Long id = getIntent().getLongExtra("id", 0L);
        String key = getIntent().getStringExtra("key");

        Log.d(this.getClass().getSimpleName(), "onCreate: Id=" + id + " key=" + key);
        switch (getIntent().getStringExtra("key")) {
            case "question":
            case "userquestion":
                titleText.setText(getString(R.string.questions_title));
                break;
            case "answer":
            case "useranswer":
                titleText.setText(getString(R.string.answers_title));
                break;
            case "bookmark":
                titleText.setText(getString(R.string.bookmarks_title));
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("key", key);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        NewsfeedListFragement newsfeedFragment = new NewsfeedListFragement();
        newsfeedFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.placeHolders, newsfeedFragment).commit();
    }
}


