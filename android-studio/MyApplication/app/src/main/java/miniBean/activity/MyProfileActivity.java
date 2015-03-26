package miniBean.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.fragement.ProfileFragment;
import miniBean.viewmodel.HeaderDataVM;
import miniBean.viewmodel.NotificationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyProfileActivity extends FragmentActivity {

    private List<NotificationVM> requestNotif, notifAll;
    private ImageView request, notification, settings,back;
    private TextView requestCount,notificationCount;
     Gson gson = new Gson();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_profile_fragement);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.profile_actionbar);

        request = (ImageView) findViewById(R.id.bookmarkedtAction);
        notification = (ImageView) findViewById(R.id.moreAction);
        settings = (ImageView) findViewById(R.id.setting);
        back= (ImageView) findViewById(R.id.backAction);
        requestCount= (TextView) findViewById(R.id.requestCount);
        notificationCount= (TextView) findViewById(R.id.notificationCount);

        System.out.println("IN myprofile activity...");

        AppController.api.getHeaderBarData(AppController.getInstance().getSessionId(), new Callback<HeaderDataVM>() {
            @Override
            public void success(HeaderDataVM headerDataVM, Response response) {
                System.out.println("headerdata" + headerDataVM.getName());
                System.out.println("notifi" + headerDataVM.getNotifyCounts());
                System.out.println("request" + headerDataVM.getRequestCounts());
                requestNotif = headerDataVM.getRequestNotif();
                notifAll = headerDataVM.getAllNotif();

                requestCount.setText(headerDataVM.getRequestCounts()+"");
                notificationCount.setText(headerDataVM.getNotifyCounts()+"");

                getHeaderBarData();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace(); //to see if you have errors

            }
        });

    }

    void getHeaderBarData() {

        Fragment profileFragment = new ProfileFragment();
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, profileFragment).commit();




        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Fragment requestFragment = new RequestFragment();
                Bundle bundle = new Bundle();
                bundle.putString("key","request");
                bundle.putString("requestNotif", gson.toJson(requestNotif));
                requestFragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.children_fragement, requestFragment).commit();*/

                Intent intent=new Intent(MyProfileActivity.this,NotificationActivity.class);
                intent.putExtra("key","request");
                intent.putExtra("requestNotif", gson.toJson(requestNotif));
                startActivity(intent);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*back.setVisibility(View.INVISIBLE);
                settings.setVisibility(View.INVISIBLE);
                Fragment notificactionFragment = new NotificationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("notifAll", gson.toJson(notifAll));
                notificactionFragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.children_fragement, notificactionFragment).commit();*/
                Intent intent=new Intent(MyProfileActivity.this,NotificationActivity.class);
                intent.putExtra("key","notification");
                intent.putExtra("notifAll", gson.toJson(notifAll));
                startActivity(intent);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*request.setVisibility(View.INVISIBLE);
                notification.setVisibility(View.INVISIBLE);
                settings.setVisibility(View.INVISIBLE);
                Fragment settingFragment = new LogoutFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.children_fragement, settingFragment).commit();*/
                Intent intent=new Intent(MyProfileActivity.this,NotificationActivity.class);
                intent.putExtra("key","logout");
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}


