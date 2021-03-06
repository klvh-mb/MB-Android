package miniBean.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import miniBean.R;
import miniBean.app.TrackedFragmentActivity;
import miniBean.fragment.CommunityFragment;
import miniBean.util.SharingUtil;

public class CommunityActivity extends TrackedFragmentActivity {
    private ImageView backImage, whatsappAction, newPostAction;
    private TextView titleAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.community_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.community_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        backImage = (ImageView) this.findViewById(R.id.backImage);
        titleAction = (TextView) this.findViewById(R.id.actionbarTitle);
        newPostAction = (ImageView) this.findViewById(R.id.newPostIcon);

        Bundle bundle = new Bundle();
        if (getIntent().getStringExtra("flag") != null) {
            bundle.putString("flag", (getIntent().getStringExtra("flag")));
        }

        bundle.putLong("id", getIntent().getLongExtra("id",0L));
        CommunityFragment fragment = new CommunityFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.children_layout, fragment).commit();

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //finish();
                /*Intent intent = new Intent(CommunityActivity.this, ActivityMain.class);
                startActivity(intent);*/
            }
        });

        newPostAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this,NewPostActivity.class);
                intent.putExtra("id",getIntent().getLongExtra("id",0L));
                intent.putExtra("flag","FromCommActivity");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}
