package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import miniBean.R;
import miniBean.fragement.SettingsFragment;
import miniBean.fragement.NotificationListFragment;
import miniBean.fragement.RequestListFragment;

public class MyProfileActionActivity extends FragmentActivity {

    private TextView titleText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_profile_action_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.my_profile_action_actionbar);

        titleText= (TextView) findViewById(R.id.title);

        Bundle bundle = new Bundle();
        Fragment fragment = null;
        switch (getIntent().getStringExtra("key")) {
            case "requests":
                titleText.setText(getString(R.string.request_actionbar_title));
                bundle.putString("requestNotif", getIntent().getStringExtra("requestNotif"));
                fragment = new RequestListFragment();
                fragment.setArguments(bundle);
                break;
            case "notifications":
                titleText.setText(getString(R.string.notification_actionbar_title));
                bundle.putString("notifAll", getIntent().getStringExtra("notifAll"));
                fragment = new NotificationListFragment();
                fragment.setArguments(bundle);
                break;
            case "settings":
                titleText.setText(getString(R.string.settings_actionbar_title));
                fragment = new SettingsFragment();
                break;
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.children_layout, fragment).commit();
        }

        /*
        if(getIntent().getStringExtra("key").equals("request")) {
            titleText.setText("Request");
            Fragment requestFragment = new RequestListFragment();
            Bundle bundle1 = new Bundle();
            String jstring = getIntent().getStringExtra("requestNotif");
            bundle1.putString("requestNotif", getIntent().getStringExtra("requestNotif"));
            requestFragment.setArguments(bundle1);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.children_layout, requestFragment).commit();
        } else if(getIntent().getStringExtra("key").equals("notification")) {
            titleText.setText("Notification");
            Fragment notificationFragment = new NotificationListFragment();
            Bundle bundle1 = new Bundle();
            String jstring = getIntent().getStringExtra("notifAll");
            bundle1.putString("notifAll",getIntent().getStringExtra("notifAll"));
            notificationFragment.setArguments(bundle1);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.children_layout, notificationFragment).commit();
        } else if(getIntent().getStringExtra("key").equals("logout")) {
            titleText.setText("Logout");
            Fragment logoutFragment = new SettingsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.children_layout, logoutFragment).commit();
        }
        */
    }
}


