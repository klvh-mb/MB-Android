package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import miniBean.R;
import miniBean.app.TrackedFragment;
import miniBean.app.TrackedFragmentActivity;
import miniBean.fragment.MessageListFragment;

public class MessageActivity extends TrackedFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.message_actionbar);

        TrackedFragment fragment = new MessageListFragment();
        fragment.setTrackedOnce();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.placeHolder1, fragment,"message").commit();

        ImageView backImage = (ImageView) this.findViewById(R.id.backAction);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
