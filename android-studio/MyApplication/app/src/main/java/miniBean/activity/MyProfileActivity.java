package miniBean.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private ImageView settings, back;
    private ViewGroup request, notification;
    private TextView requestCount,notificationCount;
    Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_profile_fragement);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getActionBar().setCustomView(R.layout.my_profile_actionbar);

        request = (ViewGroup) findViewById(R.id.requestLayout);
        notification = (ViewGroup) findViewById(R.id.notificationLayout);
        settings = (ImageView) findViewById(R.id.setting);
        back = (ImageView) findViewById(R.id.backAction);
        requestCount = (TextView) findViewById(R.id.requestCount);
        notificationCount = (TextView) findViewById(R.id.notificationCount);

        AppController.api.getHeaderBarData(AppController.getInstance().getSessionId(), new Callback<HeaderDataVM>() {
            @Override
            public void success(HeaderDataVM headerDataVM, Response response) {
                setHeaderBarData(headerDataVM);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace(); //to see if you have errors
            }
        });
    }

    private void setHeaderBarData(HeaderDataVM headerDataVM) {
        Log.d(MyProfileActivity.this.getClass().getSimpleName(), "getHeaderBarData.success: user="+headerDataVM.getName()+" request="+headerDataVM.getRequestCounts()+" notif="+headerDataVM.getNotifyCounts());

        requestNotif = headerDataVM.getRequestNotif();
        notifAll = headerDataVM.getAllNotif();
        requestCount.setText(headerDataVM.getRequestCounts()+"");
        notificationCount.setText(headerDataVM.getNotifyCounts()+"");

        Fragment profileFragment = new ProfileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, profileFragment).commit();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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


