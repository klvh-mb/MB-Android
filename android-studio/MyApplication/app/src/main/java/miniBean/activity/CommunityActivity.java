package miniBean.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.FeedListAdapter;
import miniBean.app.AppController;
import miniBean.app.LocalCache;
import miniBean.app.MyApi;
import miniBean.fragement.CommFragment;
import miniBean.fragement.PostFragment;
import miniBean.fragement.UserProfileFragment;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class CommunityActivity extends FragmentActivity {
    public SharedPreferences session = null;
    public MyApi api;
    ProgressBar progressBar, spinner;
    ImageView backImage;
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
        //progressBar.setVisibility(View.VISIBLE);

        Bundle bundle=new Bundle();
        bundle.putString("id",getIntent().getStringExtra("id"));
        bundle.putString("noMember",getIntent().getStringExtra("noMember"));
        bundle.putString("noPost",getIntent().getStringExtra("noPost"));
        bundle.putString("commName",getIntent().getStringExtra("commName"));
        bundle.putString("icon",getIntent().getStringExtra("icon"));
        bundle.putBoolean("isM",getIntent().getBooleanExtra("isM",true));
        CommFragment fragement = new CommFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragement.setArguments(bundle);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.children_layout, fragement).commit();

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });

    }
}
