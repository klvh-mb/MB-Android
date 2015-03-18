package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import miniBean.R;
import miniBean.fragement.UserProfileFragment;

public class ProfileActivity extends FragmentActivity {

    private ImageView request,notification,settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_profile_view);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.profile_actionbar);

        request = (ImageView) findViewById(R.id.bookmarkedtAction);
        notification = (ImageView) findViewById(R.id.moreAction);
        settings = (ImageView) findViewById(R.id.setting);


//        String id = getIntent().getStringExtra("oid");
        String name = getIntent().getStringExtra("name");
        System.out.println("profileActivity::::::"+getIntent().getLongExtra("oid",0l));
        Bundle bundle = new Bundle();
        bundle.putLong("oid", getIntent().getLongExtra("oid",0l));
        bundle.putString("name", name);
        //set Fragmentclass Arguments
        //Fragmentclass fragobj=new Fragmentclass();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        UserProfileFragment profileFragement = new UserProfileFragment();
        profileFragement.setArguments(bundle);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.placeHolder, profileFragement).commit();

        request.setVisibility(View.INVISIBLE);
        notification.setVisibility(View.INVISIBLE);
        settings.setVisibility(View.INVISIBLE);
    }

}


