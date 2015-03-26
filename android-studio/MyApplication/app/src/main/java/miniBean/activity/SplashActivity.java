package miniBean.activity;

/**
 * Created by MNT on 09-Feb-15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);

        /*
        if( getIntent().getBooleanExtra("EXIT", false)){
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }
        */

        if (AppController.getInstance().getSessionId() != null) {
            Log.d(this.getClass().getSimpleName(), "onCreate: sessionID - " + AppController.getInstance().getSessionId());
            AppController.api.getSocialCommunityCategoriesMap(false, AppController.getInstance().getSessionId(),
                    new Callback<List<CommunityCategoryMapVM>>() {
                        @Override
                        public void success(List<CommunityCategoryMapVM> array, retrofit.client.Response response) {
                            init(array);

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                }
                            }, DefaultValues.SPLASH_DISPLAY_MILLIS);
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            //retrofitError.printStackTrace();

                            showNetworkProblemAlert();

                            /*
                            if (RetrofitError.Kind.NETWORK.equals(retrofitError.getKind().name()) ||
                                    RetrofitError.Kind.HTTP.equals(retrofitError.getKind().name())) {

                            } else {

                            }

                            if (!isOnline()) {
                                SplashActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                            }
                            */
                        }
                    });
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
    }

    public static void init(List<CommunityCategoryMapVM> array) {
        cacheCommunityCategoryMapList(array);

        // set user info to check for role specific actions, and others
        AppController.getInstance().setUserInfo();
    }

    private static void cacheCommunityCategoryMapList(List<CommunityCategoryMapVM> array) {
        Log.d("SplashActivity", "cacheCommunityCategoryMapList: CommunityCategoryMapVM list size - "+array.size());
        LocalCache.clearCommunityCategoryMapList();
        LocalCache.addCommunityCategoryMapToList(new CommunityCategoryMapVM(
                AppController.getInstance().getString(R.string.community_tab_my)));
        for (CommunityCategoryMapVM vm : array) {
            LocalCache.addCommunityCategoryMapToList(vm);
        }
    }

    private void showNetworkProblemAlert() {
        new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog)
                .setTitle(getString(R.string.connection_timeout_title))
                .setMessage(getString(R.string.connection_timeout_message))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SplashActivity.this.finish();
                    }
                })
                .show();
    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), getString(R.string.connection_timeout_message), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
