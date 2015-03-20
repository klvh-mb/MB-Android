package miniBean.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import miniBean.R;
import miniBean.fragement.CommFragment;
import miniBean.fragement.MainFragement;
import miniBean.fragement.PostFragment;

public class CommunityActivity extends FragmentActivity {
    ProgressBar progressBar, spinner;
    ImageView backImage,editAction;
    TextView titleAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.comm_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.community_actionbar);
        backImage = (ImageView) this.findViewById(R.id.backImage);
        titleAction= (TextView) this.findViewById(R.id.titleAction);
        spinner = (ProgressBar) this.findViewById(R.id.loadCover);
        progressBar = (ProgressBar) this.findViewById(R.id.progressCommunity);
        editAction= (ImageView) this.findViewById(R.id.editAction);
        //progressBar.setVisibility(View.VISIBLE);
        Intent intent=getIntent();

        System.out.println("flag comm::"+intent.getStringExtra("flag"));

        if(intent.getStringExtra("flag")!=null) {
            System.out.println("flag:::::::" + intent.getStringExtra("flag").toString());
        }

        Bundle bundle = new Bundle();
        if(getIntent().getStringExtra("flag") != null) {
            System.out.println("flagchecked:::::::::");
            bundle.putString("flag", (getIntent().getStringExtra("flag")));
        }

        System.out.println("idchecked 1::::"+getIntent().getStringExtra("id"));
        bundle.putString("id", getIntent().getStringExtra("id"));

        bundle.putString("noMember", getIntent().getStringExtra("noMember"));
        bundle.putString("commName", getIntent().getStringExtra("commName"));
        bundle.putString("icon", getIntent().getStringExtra("icon"));
        bundle.putBoolean("isM", getIntent().getBooleanExtra("isM", true));
        CommFragment fragment = new CommFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.children_layout, fragment).commit();

        backImage.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //finish();
                Intent intent=new Intent(CommunityActivity.this,ActivityMain.class);
                startActivity(intent);
            }
        });

        editAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("id",getIntent().getStringExtra("id"));
                bundle.putString("noMember", getIntent().getStringExtra("noMember"));
                bundle.putString("commName", getIntent().getStringExtra("commName"));
                bundle.putString("icon", getIntent().getStringExtra("icon"));
                bundle.putBoolean("isM", getIntent().getBooleanExtra("isM", true));

                PostFragment fragment = new PostFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment.setArguments(bundle);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.children_layout, fragment).commit();

            }
        });

    }

    @Override
    public void onBackPressed() {
        this.finish();
        Fragment fragment=getVisibleFragment();
        if(fragment instanceof CommFragment)
        {
            System.out.println("Problem solved:::::::");
            Intent intent=new Intent(CommunityActivity.this,ActivityMain.class);
            startActivity(intent);
        }
    }
    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = CommunityActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }
}
