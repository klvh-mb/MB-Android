package miniBean.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.LocalCommunityTabCache;
import miniBean.app.NotificationCache;
import miniBean.app.TrackedFragmentActivity;
import miniBean.fragment.CommunityMainFragment;
import miniBean.app.TrackedFragment;
import miniBean.fragment.MyProfileFragment;
import miniBean.fragment.SchoolsMainFragment;
import miniBean.util.AnimationUtil;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.NotificationsParentVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends TrackedFragmentActivity {

    private LinearLayout commsLayout;
    private ImageView commsImage;
    private TextView commsText;

    private LinearLayout schoolsLayout;
    private ImageView schoolsImage;
    private TextView schoolsText;

    private LinearLayout profileLayout;
    private ImageView profileImage;
    private TextView profileText;

    private boolean commsClicked = false, schoolsClicked = false, profileClicked = false;

    private boolean topicCommunityTabLoaded = false;
    private boolean yearCommunityTabLoaded = false;

    private ProgressBar spinner;
    private TextView notificationCount;

    private TrackedFragment selectedFragment;

    private static MainActivity mInstance;

    public static synchronized MainActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        mInstance = this;

        getActionBar().hide();

        commsLayout = (LinearLayout) findViewById(R.id.commsLayout);
        commsImage = (ImageView) findViewById(R.id.commsImage);
        commsText = (TextView) findViewById(R.id.commsText);

        schoolsLayout = (LinearLayout) findViewById(R.id.schoolsLayout);
        schoolsImage = (ImageView) findViewById(R.id.schoolsImage);
        schoolsText = (TextView) findViewById(R.id.schoolsText);

        profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        profileText = (TextView) findViewById(R.id.profileText);
        notificationCount = (TextView) findViewById(R.id.notificationCount);

        spinner = (ProgressBar) findViewById(R.id.spinner);

        commsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Community tab clicked");
                pressCommunityTab();
            }
        });

        schoolsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Schools tab clicked");
                pressSchoolsTab();
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Profile tab clicked");
                pressProfileTab();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        init();

        AnimationUtil.show(spinner);
        NotificationCache.refresh(new Callback<NotificationsParentVM>() {
            @Override
            public void success(NotificationsParentVM notificationsParentVM, Response response) {
                AnimationUtil.cancel(spinner);
                setUnreadNotificationsCount();
            }

            @Override
            public void failure(RetrofitError error) {
                AnimationUtil.cancel(spinner);
                error.printStackTrace();
            }
        });
    }

    public void pressCommunityTab() {
        getActionBar().hide();

        if (!commsClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new CommunityMainFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
        }

        setMenuButton(commsImage, commsText, R.drawable.mn_comm_sel, R.color.sharp_pink);
        commsClicked = true;

        setMenuButton(schoolsImage, schoolsText, R.drawable.mn_tag, R.color.dark_gray_2);
        schoolsClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;

        setUnreadNotificationsCount();
    }

    public void pressSchoolsTab() {
        getActionBar().hide();

        if (!schoolsClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new SchoolsMainFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
        }

        setMenuButton(commsImage, commsText, R.drawable.mn_comm, R.color.dark_gray_2);
        commsClicked = false;

        setMenuButton(schoolsImage, schoolsText, R.drawable.mn_tag_sel, R.color.sharp_pink);
        schoolsClicked = true;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;

        setUnreadNotificationsCount();
    }

    public void pressProfileTab() {
        getActionBar().show();

        if (!profileClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new MyProfileFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
            notificationCount.setVisibility(View.INVISIBLE);
        }

        setMenuButton(commsImage, commsText, R.drawable.mn_comm, R.color.dark_gray_2);
        commsClicked = false;

        setMenuButton(schoolsImage, schoolsText, R.drawable.mn_tag, R.color.dark_gray_2);
        schoolsClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile_sel, R.color.sharp_pink);
        profileClicked = true;

        setUnreadNotificationsCount();
    }

    private void setMenuButton(ImageView imageView, TextView textView, int image, int textColor) {
        imageView.setImageDrawable(this.getResources().getDrawable(image));
        textView.setTextColor(this.getResources().getColor(textColor));
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment != null && !selectedFragment.allowBackPressed()) {
            return;
        }

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void init() {
        if (LocalCommunityTabCache.getMyCommunities() == null) {
            LocalCommunityTabCache.refreshMyCommunities();
        }

        if (LocalCommunityTabCache.isCommunityCategoryMapListEmpty()) {
            AnimationUtil.show(spinner);

            topicCommunityTabLoaded = false;
            yearCommunityTabLoaded = false;

            AppController.getApi().getTopicCommunityCategoriesMap(false, AppController.getInstance().getSessionId(),
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

            AppController.getApi().getZodiacYearCommunities(AppController.getInstance().getSessionId(),
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

    private void setUnreadNotificationsCount() {
        NotificationsParentVM notificationsParentVM = NotificationCache.getNotifications();
        if (notificationsParentVM == null) {
            return;
        }

        long count = notificationsParentVM.getRequestCounts() + notificationsParentVM.getNotifyCounts()+notificationsParentVM.getMessageCount();

        Log.d(this.getClass().getSimpleName(), "setUnreadNotificationsCount: requestCount="+notificationsParentVM.getRequestCounts()+" notifCount="+notificationsParentVM.getNotifyCounts());

        if(count == 0) {
            notificationCount.setVisibility(View.INVISIBLE);
        } else {
            notificationCount.setVisibility(View.VISIBLE);
            notificationCount.setText(count+"");
        }
    }
}

