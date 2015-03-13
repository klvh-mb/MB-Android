package miniBean.activity;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import miniBean.R;
import miniBean.app.MyApi;
import miniBean.fragement.CommFragment;
import miniBean.fragement.PostFragment;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class CommunityActivity extends FragmentActivity {
    public SharedPreferences session = null;
    public MyApi api;
    ProgressBar progressBar, spinner;
    ImageView backImage,editAction;
    TextView titleAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();
        session = getSharedPreferences("prefs", 0);
        setContentView(R.layout.comm_activity);
        api = restAdapter.create(MyApi.class);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.community_actionbar);
        backImage = (ImageView) this.findViewById(R.id.backImage);
        titleAction= (TextView) this.findViewById(R.id.titleAction);
        spinner = (ProgressBar) this.findViewById(R.id.loadCover);
        progressBar = (ProgressBar) this.findViewById(R.id.progressCommunity);
        editAction= (ImageView) this.findViewById(R.id.editAction);
        //progressBar.setVisibility(View.VISIBLE);

        Bundle bundle=new Bundle();
        bundle.putString("id",getIntent().getStringExtra("id"));
        bundle.putString("noMember",getIntent().getStringExtra("noMember"));
        bundle.putString("commName",getIntent().getStringExtra("commName"));
        bundle.putString("icon",getIntent().getStringExtra("icon"));
        bundle.putBoolean("isM",getIntent().getBooleanExtra("isM",true));
        CommFragment fragment = new CommFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.children_layout, fragment).commit();

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });


        editAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("id",getIntent().getStringExtra("id"));

                PostFragment fragment = new PostFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment.setArguments(bundle);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.children_layout, fragment).commit();

            }
        });

    }
}
