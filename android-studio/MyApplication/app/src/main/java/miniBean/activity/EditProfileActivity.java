package miniBean.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import miniBean.app.DistrictCache;
import miniBean.app.TrackedFragmentActivity;
import miniBean.app.UserInfoCache;
import miniBean.util.DefaultValues;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.LocationVM;
import miniBean.viewmodel.UserProfileDataVM;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditProfileActivity extends TrackedFragmentActivity {

    private Spinner locationSpinner;
    private Button finishButton;
    private ImageView fbLoginIcon, mbLoginIcon;
    private EditText displayName,aboutmeEdit;
    private TextView displayEmailText;
    private EditText lastNameEdit,firstNameEdit;
    private ImageView backImage;

    private UserProfileDataVM profileDataVM;

    private int locationId = 1;

    private int pos;

    private List<String> districtNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_profile_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.my_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        TextView titleText = (TextView) findViewById(R.id.actionbarTitle);
        titleText.setText(getString(R.string.edit_user_info));

        fbLoginIcon = (ImageView) findViewById(R.id.fbLoginIcon);
        mbLoginIcon = (ImageView) findViewById(R.id.mbLoginIcon);

        lastNameEdit = (EditText) findViewById(R.id.lastNameEditText);
        firstNameEdit = (EditText) findViewById(R.id.firstNameEditText);

        displayEmailText = (TextView) findViewById(R.id.displayEmailText);
        aboutmeEdit = (EditText) findViewById(R.id.aboutmeEdit);

        profileDataVM = new UserProfileDataVM();

        displayName = (EditText) findViewById(R.id.displaynameEdit);
        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);

        finishButton = (Button) findViewById(R.id.finishButton);

        setDistricts();

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                locationId = -1;
                String loc = locationSpinner.getSelectedItem().toString();
                List<LocationVM> districts = DistrictCache.getDistricts();
                for (LocationVM vm : districts) {
                    if (vm.getDisplayName().equals(loc)) {
                        locationId = vm.getId().intValue();
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

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setDistricts(){
        List<LocationVM> districts = DistrictCache.getDistricts();
        districtNames = new ArrayList<String>();
        districtNames.add(getString(R.string.signup_details_location));
        for (int i = 0; i < districts.size(); i++) {
            districtNames.add(districts.get(i).getDisplayName());
        }

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(
                EditProfileActivity.this,
                android.R.layout.simple_spinner_item,
                districtNames);
        locationSpinner.setAdapter(locationAdapter);

        // set previous value
        pos = locationAdapter.getPosition(AppController.getUserLocation().getDisplayName());
        locationSpinner.setSelection(pos);
    }

    private void getUserInfo() {
        UserVM user = UserInfoCache.getUser();
        fbLoginIcon.setVisibility(user.isFbLogin()? View.VISIBLE : View.GONE);
        mbLoginIcon.setVisibility(user.isFbLogin()? View.GONE : View.VISIBLE);
        displayEmailText.setText(user.getEmail());
        displayName.setText(user.getDisplayName());
        aboutmeEdit.setText(user.getAboutMe());
        firstNameEdit.setText(user.getFirstName());
        lastNameEdit.setText(user.getLastName());

        //(new ActivityUtil(this)).hideInputMethodWindow(this.finishButton);
    }

    private void setUserProfileData(UserProfileDataVM userProfileDataVM){
        ViewUtil.showSpinner(this);
        AppController.getApi().updateUserProfileData(userProfileDataVM, AppController.getInstance().getSessionId(), new Callback<UserVM>() {
            @Override
            public void success(UserVM userVM, Response response) {
                UserInfoCache.refresh(new Callback<UserVM>() {
                    @Override
                    public void success(UserVM userVM, Response response) {
                        // refresh parent activity
                        Intent intent = new Intent();
                        intent.putExtra(ViewUtil.INTENT_VALUE_REFRESH, true);
                        setResult(RESULT_OK, intent);

                        ViewUtil.stopSpinner(EditProfileActivity.this);

                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(EditProfileActivity.class.getSimpleName(), "updateUserProfileData: failure", error);
                    }
                }, null);
            }

            @Override
            public void failure(RetrofitError error) {
                String errorMsg = ViewUtil.getResponseBody(error.getResponse());
                if (error.getResponse().getStatus() == 500 &&
                        error.getResponse() != null &&
                        !StringUtils.isEmpty(errorMsg)) {
                    ViewUtil.alert(EditProfileActivity.this, errorMsg);
                } else {
                    ViewUtil.alert(EditProfileActivity.this, getString(R.string.signup_details_error_info));
                }
                ViewUtil.stopSpinner(EditProfileActivity.this);
                Log.e(EditProfileActivity.class.getSimpleName(), "setUserProfileData: failure", error);
            }
        });
    }
}