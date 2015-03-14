package miniBean.activity;

/**
 * Created by MNT on 09-Feb-15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
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

    public SharedPreferences session = null;

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

        session = getSharedPreferences("prefs", 0);
        LocalCache.addCommunityCategoryMapToList(new CommunityCategoryMapVM(getString(R.string.my_community_tab)));
        if (session.getString("sessionID", null) != null) {
            Log.d("sessionID", session.getString("sessionID", null));
            AppController.api.getSocialCommunityCategoriesMap(false, session.getString("sessionID", null), new Callback<List<CommunityCategoryMapVM>>() {
                @Override
                public void success(List<CommunityCategoryMapVM> array, retrofit.client.Response response) {
                    for (CommunityCategoryMapVM vm : array) {
                        LocalCache.addCommunityCategoryMapToList(vm);
                    }
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            startActivity(new Intent(SplashActivity.this, ActivityMain.class));
                            finish();
                        }
                    }, DefaultValues.DEFAULT_CONNECTION_TIMEOUT * 1000);
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
