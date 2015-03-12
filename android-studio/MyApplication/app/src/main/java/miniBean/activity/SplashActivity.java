package miniBean.activity;

/**
 * Created by MNT on 09-Feb-15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.LocalCache;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunityCategoryMapVM;
import retrofit.Callback;
import retrofit.RetrofitError;


public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    public SharedPreferences session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);

        session = getSharedPreferences("prefs", 0);
        LocalCache.categoryMapList.add(new CommunityCategoryMapVM(getString(R.string.my_community_tab)));
        if (session.getString("sessionID", null) != null) {
            AppController.api.getSocialCommunityCategoriesMap(false, session.getString("sessionID", null), new Callback<List<CommunityCategoryMapVM>>() {
                @Override
                public void success(List<CommunityCategoryMapVM> array, retrofit.client.Response response) {
                    LocalCache.categoryMapList.addAll(array);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            startActivity(new Intent(SplashActivity.this, ActivityMain.class));
                            finish();
                        }
                    }, DefaultValues.DEFAULT_CONNECTION_TIMEOUT * 1000);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    retrofitError.printStackTrace(); //to see if you have errors

                    if (!isOnline()) {
                        //SplashActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }
            });
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), getString(R.string.connection_timeout), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
