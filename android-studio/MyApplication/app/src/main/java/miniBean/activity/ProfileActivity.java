package miniBean.activity;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.MyApi;
import retrofit.RestAdapter;
import retrofit.client.OkClient;


public class ProfileActivity extends FragmentActivity {

    public SharedPreferences session = null;
    public MyApi api;
    ImageView userCoverPic,userImage;
    TextView name;
    ImageView image;
    String selectedImagePath = null;
    Uri selectedImageUri = null;
    private ListView listView;
    ProgressBar spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();
        setContentView(R.layout.friend_profile_view);
        session = getSharedPreferences("prefs", 0);
        api = restAdapter.create(MyApi.class);

        name= (TextView) findViewById(R.id.usernameText);
        userCoverPic= (ImageView) findViewById(R.id.userCoverPic);
        userImage= (ImageView) findViewById(R.id.userImage);
        spinner= (ProgressBar) findViewById(R.id.imageLoader);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.friend_profile_actionbar);

        AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + "/image/get-cover-image-by-id/" + getIntent().getStringExtra("id"), userCoverPic,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                spinner.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                spinner.setVisibility(View.GONE);
            }
        });

        AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + "/image/get-profile-image-by-id/" + getIntent().getStringExtra("id"), userImage);
        name.setText(getIntent().getStringExtra("name"));



    }
}

