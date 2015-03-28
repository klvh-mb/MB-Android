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

import miniBean.R;
import miniBean.app.AppController;
import miniBean.fragement.MainFragement;
import miniBean.fragement.MyProfileFragment;

public class MainActivity extends FragmentActivity {

    Button community, profile, schools;
    private boolean commClicked = false, profileClicked = false;

    private int realTabIconWidth, realTabIconHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        community = (Button) findViewById(R.id.comms);
        profile = (Button) findViewById(R.id.profiles);
        schools = (Button) findViewById(R.id.schools);

        Rect rect = community.getCompoundDrawables()[0].getBounds();
        realTabIconWidth = rect.width();
        realTabIconHeight = rect.height();
        Log.d(this.getClass().getSimpleName(), "onCreate: realDimension - " + rect.width() + ":" + rect.height());

        pressCommunityTab();

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

    private void pressCommunityTab() {
        if (!commClicked) {
            getActionBar().hide();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            MainFragement mainFragement = new MainFragement();
            fragmentTransaction.replace(R.id.placeHolder, mainFragement).commit();
            commClicked = true;
        }

        Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.profile);
        icon.setBounds(0, 0, realTabIconWidth, realTabIconHeight);
        profile.setCompoundDrawables(icon, null, null, null);
        profile.setTextColor(getResources().getColor(R.color.dark_gray3));

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
            getActionBar().show();
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
        community.setTextColor(getResources().getColor(R.color.dark_gray3));

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

        AppController.getInstance().clearAll();
    }
    // In your activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}

