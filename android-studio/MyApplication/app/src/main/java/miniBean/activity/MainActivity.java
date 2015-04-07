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
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.LocalCommunityTabCache;
import miniBean.fragement.MainFragement;
import miniBean.fragement.MyProfileFragment;
import miniBean.util.AnimationUtil;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.HeaderDataVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends FragmentActivity {

    private Button community, profile, schools;
    private boolean commClicked = false, schoolsClicked = false, profileClicked = false;

    private int realTabIconWidth, realTabIconHeight;

    private boolean topicCommunityTabLoaded = false;
    private boolean yearCommunityTabLoaded = false;

    private ProgressBar spinner;
    private TextView notificationCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        getActionBar().hide();

        community = (Button) findViewById(R.id.comms);
        profile = (Button) findViewById(R.id.profiles);
        schools = (Button) findViewById(R.id.schools);
        notificationCount = (TextView) findViewById(R.id.notificationCount);
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

        schools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Schools tab clicked");
                pressSchoolsTab();
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

        checkUnreadNotifications();
    }

    private void pressCommunityTab() {
        getActionBar().hide();

        if (!commClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            MainFragement fragement = new MainFragement();
            fragmentTransaction.replace(R.id.placeHolder, fragement).commit();
        }

        Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.comm_sel);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        community.setCompoundDrawables(icon, null, null, null);
        community.setTextColor(getResources().getColor(R.color.sharp_pink));
        commClicked = true;

        icon = getApplicationContext().getResources().getDrawable(R.drawable.tag);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        schools.setCompoundDrawables(icon, null, null, null);
        schools.setTextColor(getResources().getColor(R.color.dark_gray_3));
        schoolsClicked = false;

        icon = getApplicationContext().getResources().getDrawable(R.drawable.profile);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        profile.setCompoundDrawables(icon, null, null, null);
        profile.setTextColor(getResources().getColor(R.color.dark_gray_3));
        profileClicked = false;
    }

    private void pressSchoolsTab() {
        getActionBar().show();

        if (!schoolsClicked) {
            /*FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            SchoolsFragment fragement = new SchoolsFragment();
            fragmentTransaction.replace(R.id.placeHolder, fragement).commit();*/
        }

        Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.comm);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        community.setCompoundDrawables(icon, null, null, null);
        community.setTextColor(getResources().getColor(R.color.dark_gray_3));
        commClicked = false;

        icon = getApplicationContext().getResources().getDrawable(R.drawable.tag_sel);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        schools.setCompoundDrawables(icon, null, null, null);
        schools.setTextColor(getResources().getColor(R.color.sharp_pink));
        schoolsClicked = true;

        icon = getApplicationContext().getResources().getDrawable(R.drawable.profile);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        profile.setCompoundDrawables(icon, null, null, null);
        profile.setTextColor(getResources().getColor(R.color.dark_gray_3));
        profileClicked = false;
    }

    private void pressProfileTab() {
        getActionBar().show();

        if (!profileClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            MyProfileFragment fragement = new MyProfileFragment();
            fragmentTransaction.replace(R.id.placeHolder, fragement).commit();
            notificationCount.setVisibility(View.INVISIBLE);
        }

        Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.comm);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        community.setCompoundDrawables(icon, null, null, null);
        community.setTextColor(getResources().getColor(R.color.dark_gray_3));
        commClicked = false;

        icon = getApplicationContext().getResources().getDrawable(R.drawable.tag);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        schools.setCompoundDrawables(icon, null, null, null);
        schools.setTextColor(getResources().getColor(R.color.dark_gray_3));
        schoolsClicked = false;

        icon = getApplicationContext().getResources().getDrawable(R.drawable.profile_sel);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        profile.setCompoundDrawables(icon, null, null, null);
        profile.setTextColor(getResources().getColor(R.color.sharp_pink));
        profileClicked = true;
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
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

    private void checkUnreadNotifications() {
        AppController.api.getHeaderBarData(AppController.getInstance().getSessionId(), new Callback<HeaderDataVM>() {
            @Override
            public void success(HeaderDataVM headerDataVM, Response response) {
                int totalNotif;
                totalNotif = Integer.valueOf(headerDataVM.getRequestCounts().toString()) + Integer.valueOf(headerDataVM.getNotifyCounts().toString());

                if(totalNotif==0){
                    notificationCount.setVisibility(View.INVISIBLE);
                }else {
                    notificationCount.setText("" + totalNotif);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
