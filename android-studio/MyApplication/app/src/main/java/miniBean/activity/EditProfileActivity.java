package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.TrackedFragmentActivity;
import miniBean.app.UserInfoCache;
import miniBean.util.ActivityUtil;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.LocationVM;
import miniBean.viewmodel.UserProfileDataVM;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditProfileActivity extends TrackedFragmentActivity {

    private Spinner locationSpinner;
    private Button finishButton;
    private EditText displayName,aboutmeEdit;
    private TextView displayEmailText;
    private EditText lastNameEdit,firstNameEdit;

    private UserProfileDataVM profileDataVM;

    private int locationId = 1;

    private int pos;

    public List<String> locations;
    private List<LocationVM> locationVMList;

    protected ActivityUtil activityUtil;

    ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityUtil = new ActivityUtil(this);

        setContentView(R.layout.edit_profile_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.my_profile_action_actionbar);

        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(getString(R.string.edit_user_info));

        lastNameEdit = (EditText) findViewById(R.id.lastNameEditText);
        firstNameEdit = (EditText) findViewById(R.id.firstNameEditText);

        displayEmailText = (TextView) findViewById(R.id.displayEmailText);
        aboutmeEdit = (EditText) findViewById(R.id.aboutmeEdit);

        profileDataVM = new UserProfileDataVM();

        displayName = (EditText) findViewById(R.id.displaynameEdit);
        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);

        finishButton = (Button) findViewById(R.id.finishButton);

        locationVMList = new ArrayList<LocationVM>();

        setLocation();

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                locationId = -1;
                String loc = locationSpinner.getSelectedItem().toString();
                for (LocationVM vm : locationVMList) {
                    if (vm.getDisplayName().equals(loc)) {
                        locationId = Integer.parseInt(vm.getId().toString());
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getUserInfo();

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileDataVM.setParent_aboutme(aboutmeEdit.getText().toString());
                profileDataVM.setParent_displayname(displayName.getText().toString());
                profileDataVM.setParent_firstname(firstNameEdit.getText().toString());
                profileDataVM.setParent_lastname(lastNameEdit.getText().toString());
                profileDataVM.setParent_birth_year(String.valueOf(DefaultValues.DEFAULT_PARENT_BIRTH_YEAR));
                profileDataVM.setParent_location(locationId);
                setUserProfileData(profileDataVM);
            }
        });

        ImageView backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setLocation() {
        AppController.getApi().getAllDistricts(AppController.getInstance().getSessionId(), new Callback<List<LocationVM>>() {
            @Override
            public void success(List<LocationVM> locationVMs, Response response) {
                locations = new ArrayList<String>();

                locations.add(getString(R.string.signup_details_location));
                for (int i = 0; i < locationVMs.size(); i++) {
                    locations.add(locationVMs.get(i).getDisplayName());
                    locationVMList.addAll(locationVMs);
                }

                ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(EditProfileActivity.this, android.R.layout.simple_spinner_item, locations);
                locationSpinner.setAdapter(locationAdapter);

                pos = locationAdapter.getPosition(AppController.getUserLocation().getDisplayName());
                locationSpinner.setSelection(pos);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getUserInfo() {
        UserVM user = UserInfoCache.getUser();
        displayEmailText.setText(user.getEmail());
        displayName.setText(user.getDisplayName());
        firstNameEdit.setText(user.getFirstName());
        lastNameEdit.setText(user.getLastName());
    }

    private void setUserProfileData(UserProfileDataVM userProfileDataVM){
        AppController.getApi().updateUserProfileData(userProfileDataVM, AppController.getInstance().getSessionId(), new Callback<UserVM>() {
            @Override
            public void success(UserVM userVM, Response response) {
                UserInfoCache.refresh(new Callback<UserVM>() {
                    @Override
                    public void success(UserVM userVM, Response response) {
                        getUserInfo();
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                String errorMsg = EditProfileActivity.this.activityUtil.getResponseBody(error.getResponse());
                if (error.getResponse().getStatus() == 500 &&
                        error.getResponse() != null &&
                        !StringUtils.isEmpty(errorMsg)) {
                    ActivityUtil.alert(EditProfileActivity.this, errorMsg);
                } else {
                    ActivityUtil.alert(EditProfileActivity.this, getString(R.string.signup_details_error_info));
                }
                error.printStackTrace();
            }
        });
    }
}