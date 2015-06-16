package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;

import miniBean.R;
import miniBean.app.TrackedFragmentActivity;
import miniBean.fragment.UserProfileFragment;

public class UserProfileActivity extends TrackedFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.user_profile_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        Long userId = getIntent().getLongExtra("oid", 0l);
        String name = getIntent().getStringExtra("name");

        Bundle bundle = new Bundle();
        bundle.putLong("oid", userId);
        bundle.putString("name", name);

        //set Fragmentclass Arguments
        //Fragmentclass fragobj=new Fragmentclass();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        UserProfileFragment profileFragement = new UserProfileFragment();
        profileFragement.setArguments(bundle);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.placeHolder, profileFragement).commit();
    }
}


