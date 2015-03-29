package miniBean.activity;

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
import miniBean.app.LocalCommunityTabCache;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class SplashActivity extends Activity {

    private boolean topicCommunityTabLoaded = false;
    private boolean yearCommunityTabLoaded = false;

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

        init();

        topicCommunityTabLoaded = false;
        yearCommunityTabLoaded = false;

        if (AppController.getInstance().getSessionId() != null) {
            Log.d(this.getClass().getSimpleName(), "onCreate: sessionID - " + AppController.getInstance().getSessionId());

            AppController.api.getTopicCommunityCategoriesMap(false, AppController.getInstance().getSessionId(),
                    new Callback<List<CommunityCategoryMapVM>>() {
                        @Override
                        public void success(List<CommunityCategoryMapVM> array, retrofit.client.Response response) {
                            Log.d("SplashActivity", "api.getTopicCommunityCategoriesMap.success: CommunityCategoryMapVM list size - "+array.size());

                            LocalCommunityTabCache.addToCommunityCategoryMapList(LocalCommunityTabCache.CommunityTabType.TOPIC_COMMUNITY, array);

                            topicCommunityTabLoaded = true;
                            if (topicCommunityTabLoaded && yearCommunityTabLoaded) {
                                startMainActivity();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            showNetworkProblemAlert();

                            error.printStackTrace();

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

            AppController.api.getZodiacYearCommunities(AppController.getInstance().getSessionId(),
                    new Callback<CommunitiesParentVM>() {
                        @Override
                        public void success(CommunitiesParentVM communitiesParent, retrofit.client.Response response) {
                            Log.d("SplashActivity", "api.getZodiacYearCommunities.success: CommunitiesParentVM list size - "+communitiesParent.communities.size());

                            LocalCommunityTabCache.addToCommunityCategoryMapList(LocalCommunityTabCache.CommunityTabType.ZODIAC_YEAR_COMMUNITY, communitiesParent);

                            yearCommunityTabLoaded = true;
                            if (topicCommunityTabLoaded && yearCommunityTabLoaded) {
                                startMainActivity();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            showNetworkProblemAlert();

                            error.printStackTrace();
                        }
                    });
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
    }

    public static void init() {
        LocalCommunityTabCache.clear();

        // set user info to check for role specific actions, and others
        AppController.getInstance().setUserInfo();
    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, DefaultValues.SPLASH_DISPLAY_MILLIS);
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
