package miniBean.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.LocalCommunityTabCache;
import miniBean.fragement.MainFragement;
import miniBean.fragement.MyProfileFragment;
import miniBean.util.AnimationUtil;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class MainActivity extends FragmentActivity {

    private Button community, profile, schools;
    private boolean commClicked = false, profileClicked = false;

    private int realTabIconWidth, realTabIconHeight;

    private boolean topicCommunityTabLoaded = false;
    private boolean yearCommunityTabLoaded = false;

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        getActionBar().hide();

        community = (Button) findViewById(R.id.comms);
        profile = (Button) findViewById(R.id.profiles);
        schools = (Button) findViewById(R.id.schools);

        spinner = (ProgressBar) findViewById(R.id.spinner);

        Rect rect = community.getCompoundDrawables()[0].getBounds();
        realTabIconWidth = rect.width();
        realTabIconHeight = rect.height();
        Log.d(this.getClass().getSimpleName(), "onCreate: realDimension - " + rect.width() + ":" + rect.height());

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Community tab clicked");
                pressCommunityTab();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Profile tab clicked");
                pressProfileTab();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        init();
    }

    private void pressCommunityTab() {
        if (!commClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            MainFragement mainFragement = new MainFragement();
            fragmentTransaction.replace(R.id.placeHolder, mainFragement).commit();
            commClicked = true;
        }

        Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.profile);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        profile.setCompoundDrawables(icon, null, null, null);
        profile.setTextColor(getResources().getColor(R.color.dark_gray_3));

        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.comm_sel);
        img.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        community.setCompoundDrawables(img, null, null, null);
        community.setTextColor(getResources().getColor(R.color.sharp_pink));

        profileClicked = false;
    }

    private void pressSchoolsTab() {
    }

    private void pressProfileTab() {
        if (!profileClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            MyProfileFragment profileFragement = new MyProfileFragment();
            fragmentTransaction.replace(R.id.placeHolder, profileFragement).commit();
            profileClicked = true;
        }

        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.profile_sel);
        img.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        profile.setCompoundDrawables(img, null, null, null);
        profile.setTextColor(getResources().getColor(R.color.sharp_pink));

        Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.comm);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        community.setCompoundDrawables(icon, null, null, null);
        community.setTextColor(getResources().getColor(R.color.dark_gray_3));

        commClicked = false;
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_app)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AppController.getInstance().clearAll();
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(this.getClass().getSimpleName(), "onDestroy: clear all");

        //AppController.getInstance().clearAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void init() {
        if (LocalCommunityTabCache.isCommunityCategoryMapListEmpty()) {
            AnimationUtil.show(spinner);

            topicCommunityTabLoaded = false;
            yearCommunityTabLoaded = false;

            AppController.api.getTopicCommunityCategoriesMap(false, AppController.getInstance().getSessionId(),
                    new Callback<List<CommunityCategoryMapVM>>() {
                        @Override
                        public void success(List<CommunityCategoryMapVM> array, retrofit.client.Response response) {
                            Log.d("MainActivity", "api.getTopicCommunityCategoriesMap.success: CommunityCategoryMapVM list size - " + array.size());

                            LocalCommunityTabCache.addToCommunityCategoryMapList(LocalCommunityTabCache.CommunityTabType.TOPIC_COMMUNITY, array);

                            topicCommunityTabLoaded = true;
                            if (topicCommunityTabLoaded && yearCommunityTabLoaded) {
                                AnimationUtil.cancel(spinner);
                                pressCommunityTab();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            error.printStackTrace();
                        }
                    });

            AppController.api.getZodiacYearCommunities(AppController.getInstance().getSessionId(),
                    new Callback<CommunitiesParentVM>() {
                        @Override
                        public void success(CommunitiesParentVM communitiesParent, retrofit.client.Response response) {
                            Log.d("MainActivity", "api.getZodiacYearCommunities.success: CommunitiesParentVM list size - " + communitiesParent.communities.size());

                            LocalCommunityTabCache.addToCommunityCategoryMapList(LocalCommunityTabCache.CommunityTabType.ZODIAC_YEAR_COMMUNITY, communitiesParent);

                            yearCommunityTabLoaded = true;
                            if (topicCommunityTabLoaded && yearCommunityTabLoaded) {
                                AnimationUtil.cancel(spinner);
                                pressCommunityTab();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            error.printStackTrace();
                        }
                    });
        }
    }
}

