package miniBean.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import miniBean.R;
import miniBean.fragement.CommFragment;

public class CommunityActivity extends FragmentActivity {
    ImageView backImage, editAction;
    TextView titleAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.comm_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.community_actionbar);
        backImage = (ImageView) this.findViewById(R.id.backImage);
        titleAction= (TextView) this.findViewById(R.id.titleAction);
        editAction= (ImageView) this.findViewById(R.id.editAction);

        Intent intent = getIntent();

        System.out.println("flag comm::"+intent.getStringExtra("flag"));
        if(intent.getStringExtra("flag")!=null) {
            System.out.println("flag:::::::" + intent.getStringExtra("flag").toString());
        }

        Bundle bundle = new Bundle();
        if(getIntent().getStringExtra("flag") != null) {
            bundle.putString("flag", (getIntent().getStringExtra("flag")));
        }

        bundle.putString("id", getIntent().getStringExtra("id"));
        bundle.putString("commName", getIntent().getStringExtra("commName"));
        CommFragment fragment = new CommFragment();
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

        editAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Bundle bundle = new Bundle();
                bundle.putString("id",getIntent().getStringExtra("id"));
                bundle.putString("commName", getIntent().getStringExtra("commName"));

                PostFragment fragment = new PostFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.children_layout, fragment).commit();*/

                Intent intent=new Intent(CommunityActivity.this,PostActivity.class);
                intent.putExtra("id",getIntent().getStringExtra("id"));
                intent.putExtra("commName", getIntent().getStringExtra("commName"));
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}
