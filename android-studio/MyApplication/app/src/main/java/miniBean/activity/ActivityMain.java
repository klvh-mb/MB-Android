package miniBean.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.fragement.MainFragement;
import miniBean.fragement.MyProfileFragment;
import miniBean.util.ActivityUtil;

public class ActivityMain extends FragmentActivity {

    Button community, profile, article;
    private android.app.ActionBar actionBar;
    private boolean commClicked = false,profileClicked = false;
    private ActivityUtil activityUtil;

    private int realTabIconWidth, realTabIconHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        activityUtil = new ActivityUtil(this);

        /*
        if(getIntent().getBooleanExtra("EXIT", false)){
            AppController.getInstance().clearAll();
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }
        */

        Configuration config = getResources().getConfiguration();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MainFragement mainFragement = new MainFragement();
        fragmentTransaction.replace(R.id.placeHolder, mainFragement).commit();
        commClicked=true;

        actionBar = getActionBar();
        actionBar.hide();

        community = (Button) findViewById(R.id.comms);
        profile = (Button) findViewById(R.id.profiles);
        article = (Button) findViewById(R.id.articles);

        Rect rect = community.getCompoundDrawables()[0].getBounds();
        realTabIconWidth = rect.width();
        realTabIconHeight = rect.height();
        Log.d(this.getClass().getSimpleName(), "onCreate: realDimension - " + rect.width() + ":" + rect.height());

        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.comm_sel);
        img.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        community.setCompoundDrawables(img, null, null, null);
        community.setTextColor(getResources().getColor(R.color.sharp_pink));

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.show();
                System.out.println("Profile clicked.......");
                if(!profileClicked) {
                    Configuration config = getResources().getConfiguration();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    MyProfileFragment profileFragement = new MyProfileFragment();
                    fragmentTransaction.replace(R.id.placeHolder, profileFragement).commit();
                    profileClicked=true;
                }

                Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.profile_sel);
                img.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
                profile.setCompoundDrawables(img, null, null, null);
                profile.setTextColor(getResources().getColor(R.color.sharp_pink));

                Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.comm);
                icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
                community.setCompoundDrawables(icon, null, null, null);
                community.setTextColor(getResources().getColor(R.color.dark_gray3));

                commClicked=false;

            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.hide();
                System.out.println("Community clicked.......");
                if(!commClicked) {
                    Configuration config = getResources().getConfiguration();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    MainFragement mainFragement = new MainFragement();
                    fragmentTransaction.replace(R.id.placeHolder, mainFragement).commit();
                    commClicked=true;
                }

                Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.profile);
                icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
                profile.setCompoundDrawables(icon, null, null, null);
                profile.setTextColor(getResources().getColor(R.color.dark_gray3));

                Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.comm_sel);
                img.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
                community.setCompoundDrawables(img, null, null, null);
                community.setTextColor(getResources().getColor(R.color.sharp_pink));

                profileClicked=false;
            }
        });
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
                            ActivityMain.super.onBackPressed();
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

        AppController.getInstance().clearAll();
    }
}

