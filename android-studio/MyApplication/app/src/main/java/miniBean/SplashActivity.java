package miniBean;

/**
 * Created by MNT on 09-Feb-15.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import java.util.List;

import miniBean.app.AppController;
import miniBean.app.LocalCache;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import retrofit.Callback;
import retrofit.RetrofitError;


public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    public SharedPreferences session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        session = getSharedPreferences("prefs", 0);
        LocalCache.categoryMapList.add(new CommunityCategoryMapVM("My Comm"));

        AppController.api.getSocialCommunityCategoriesMap(new Callback<List<CommunityCategoryMapVM>>() {

            @Override
            public void success(List<CommunityCategoryMapVM> array, retrofit.client.Response response) {
                int secondsDelayed = 1;
                LocalCache.categoryMapList.addAll(array);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (session.getString("sessionID", null) != null) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        }
                        finish();
                    }
                }, secondsDelayed * 1000);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors

            }
        });



    }


}
