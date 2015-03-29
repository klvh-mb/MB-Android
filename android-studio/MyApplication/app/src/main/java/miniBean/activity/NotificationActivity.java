package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.TextView;

import miniBean.R;
import miniBean.fragement.SettingsFragment;
import miniBean.fragement.NotificationListFragment;
import miniBean.fragement.RequestListFragment;

public class NotificationActivity extends FragmentActivity {

    private ImageView request,notification,settings;
    private TextView titleText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.notification_actionbar);

        request = (ImageView) findViewById(R.id.request);
        notification = (ImageView) findViewById(R.id.notification);
        settings = (ImageView) findViewById(R.id.setting);
        titleText= (TextView) findViewById(R.id.title);

                if(getIntent().getStringExtra("key").equals("request")) {
                    titleText.setText("Request");
                    Fragment requestFragment = new RequestListFragment();
                    Bundle bundle1 = new Bundle();
                    String jstring=getIntent().getStringExtra("requestNotif");
                    bundle1.putString("requestNotif", getIntent().getStringExtra("requestNotif"));
                    requestFragment.setArguments(bundle1);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.children_layout, requestFragment).commit();
                }else if(getIntent().getStringExtra("key").equals("notification"))
                {
                    titleText.setText("Notification");
                    Fragment notificationFragment = new NotificationListFragment();
                    Bundle bundle1 = new Bundle();
                    String jstring=getIntent().getStringExtra("notifAll");
                    bundle1.putString("notifAll",getIntent().getStringExtra("notifAll"));
                    notificationFragment.setArguments(bundle1);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.children_layout, notificationFragment).commit();
                }else if(getIntent().getStringExtra("key").equals("logout"))
                {
                    titleText.setText("Logout");
                    Fragment logoutFragment = new SettingsFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.children_layout, logoutFragment).commit();
                }
            }

}


