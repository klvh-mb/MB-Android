package miniBean.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import miniBean.R;
import miniBean.fragement.MainFragement;
import miniBean.fragement.MyProfileFragment;


public class ActivityMain extends FragmentActivity {

    Button community, profile, article;
    private android.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Configuration config = getResources().getConfiguration();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MainFragement mainFragement = new MainFragement();
        fragmentTransaction.replace(R.id.placeHolder, mainFragement).commit();


        actionBar = getActionBar();
        actionBar.hide();

        community = (Button) findViewById(R.id.comms);
        profile = (Button) findViewById(R.id.profiles);
        article = (Button) findViewById(R.id.articles);

        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.comm_sel);
        img.setBounds(0, 0, 60, 60);
        community.setCompoundDrawables(img, null, null, null);
        community.setTextColor(Color.parseColor("#E63E7C"));

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.show();
                System.out.println("Profile clicked.......");
                Configuration config = getResources().getConfiguration();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                MyProfileFragment profileFragement = new MyProfileFragment();
                fragmentTransaction.replace(R.id.placeHolder, profileFragement).commit();


                Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.profile_sel);
                img.setBounds(0, 0, 60, 60);
                profile.setCompoundDrawables(img, null, null, null);
                profile.setTextColor(Color.parseColor("#E63E7C"));

                Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.comm);
                icon.setBounds(0, 0, 60, 60);
                community.setCompoundDrawables(icon, null, null, null);
                community.setTextColor(Color.BLACK);


            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.hide();
                System.out.println("Community clicked.......");
                Configuration config = getResources().getConfiguration();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                MainFragement mainFragement = new MainFragement();
                fragmentTransaction.replace(R.id.placeHolder, mainFragement).commit();

                Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.profile);
                icon.setBounds(0, 0, 60, 60);
                profile.setCompoundDrawables(icon, null, null, null);
                profile.setTextColor(Color.BLACK);

                Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.comm_sel);
                img.setBounds(0, 0, 60, 60);
                community.setCompoundDrawables(img, null, null, null);
                community.setTextColor(Color.parseColor("#E63E7C"));

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
}

