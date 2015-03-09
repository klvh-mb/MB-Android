package miniBean.activity;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.MyApi;
import miniBean.fragement.MyProfileFragment;
import miniBean.fragement.UserProfileFragment;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class ProfileActivity extends FragmentActivity {

    public SharedPreferences session = null;
    public MyApi api;
    //String id ,name;
    ImageView request,notification,settings;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();
        setContentView(R.layout.friend_profile_view);
        session = getSharedPreferences("prefs", 0);
        api = restAdapter.create(MyApi.class);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.profile_actionbar);

        request= (ImageView) findViewById(R.id.bookmarkedtAction);
        notification= (ImageView) findViewById(R.id.moreAction);
        settings= (ImageView) findViewById(R.id.setting);

        System.out.println("naaam"+getIntent().getStringExtra("name"));
        System.out.println("iiiiiid"+getIntent().getStringExtra("id"));
        String id=getIntent().getStringExtra("id");
        String name=getIntent().getStringExtra("name");
        Bundle bundle=new Bundle();
        bundle.putString("id",id);
        bundle.putString("name", name);
        //set Fragmentclass Arguments
        //Fragmentclass fragobj=new Fragmentclass();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        UserProfileFragment profileFragement = new UserProfileFragment();
        profileFragement.setArguments(bundle);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.placeHolder, profileFragement).commit();


        request.setVisibility(View.INVISIBLE);
        notification.setVisibility(View.INVISIBLE);
        settings.setVisibility(View.INVISIBLE);

    }

}


