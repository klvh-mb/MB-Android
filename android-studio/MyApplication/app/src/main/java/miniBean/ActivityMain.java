package miniBean;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.astuetz.PagerSlidingTabStrip;

import miniBean.fragement.MainFragement;
import miniBean.fragement.MyCommunityFragment;


public class ActivityMain extends FragmentActivity {

    Button community,profile,article;
    private android.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Configuration config = getResources().getConfiguration();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MainFragement mainFragement=new MainFragement();
        fragmentTransaction.replace(R.id.placeHolder, mainFragement).commit();

        actionBar = getActionBar();
        actionBar.hide();

        community= (Button) findViewById(R.id.comms);
        profile= (Button) findViewById(R.id.profiles);
        article= (Button) findViewById(R.id.articles);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Profile clicked.......");

            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Community clicked.......");
                Configuration config = getResources().getConfiguration();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                MainFragement mainFragement=new MainFragement();
                fragmentTransaction.replace(R.id.placeHolder, mainFragement).commit();



            }
        });
    }

}

