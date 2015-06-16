package miniBean.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.TrackedFragmentActivity;
import miniBean.util.ActivityUtil;
import miniBean.util.DefaultValues;
import miniBean.util.Validation;
import miniBean.viewmodel.LocationVM;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignupDetailActivity extends TrackedFragmentActivity {
    private String[] babyNumberArray;
    private Spinner locationSpinner, babySpinner;
    private Button finishButton;
    private EditText displayName;
    private RadioGroup parentType, babyGender1, babyGender2, babyGender3;
    private RadioButton parent, baby1, baby2, baby3;
    private LinearLayout babynumberLayout;
    private LinearLayout babyDetailsLayout1, babyDetailsLayout2, babyDetailsLayout3;
    private TextView titleText;

    public List<String> locations;
    private List<LocationVM> locationVMList;

    String parenttype = "",babynum = "",babygen1 = "", babygen2 = "", babygen3 = "";

    private int locationId = -1;
    private Calendar calendar;
    private ImageView birthday1,birthday2,birthday3;
    private TextView birthdayLabel1,birthdayLabel2,birthdayLabel3;

    protected ActivityUtil activityUtil;

    private String year1,month1,day1,year2,month2,day2,year3,month3,day3;

    private boolean birthdayClick1 = false, birthdayClick2 = false, birthdayClick3 = false,parentClicked=false,babySelected=false;

    private DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setBirthdays();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_detail_activity);

        /*
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.signup_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );
        */
        getActionBar().hide();

        titleText = (TextView) findViewById(R.id.titleText);

        parentType = (RadioGroup) findViewById(R.id.parentRadio);

        babynumberLayout = (LinearLayout) findViewById(R.id.babyNumberLayout);
        babynumberLayout.setVisibility(View.GONE);

        babyDetailsLayout1 = (LinearLayout) findViewById(R.id.babyDetailsLayout1);
        babyDetailsLayout1.setVisibility(View.GONE);
        babyDetailsLayout2 = (LinearLayout) findViewById(R.id.babyDetailsLayout2);
        babyDetailsLayout2.setVisibility(View.GONE);
        babyDetailsLayout3 = (LinearLayout) findViewById(R.id.babyDetailsLayout3);
        babyDetailsLayout3.setVisibility(View.GONE);

        babyGender1 = (RadioGroup) findViewById(R.id.babyRadio1);
        babyGender2 = (RadioGroup) findViewById(R.id.babyRadio2);
        babyGender3 = (RadioGroup) findViewById(R.id.babyRadio3);

        birthday1 = (ImageView) findViewById(R.id.birthday1);
        birthday2 = (ImageView) findViewById(R.id.birthday2);
        birthday3 = (ImageView) findViewById(R.id.birthday3);

        birthdayLabel1 = (TextView) findViewById(R.id.birthdayLabel1);
        birthdayLabel2 = (TextView) findViewById(R.id.birthdayLabel2);
        birthdayLabel3 = (TextView) findViewById(R.id.birthdayLabel3);

        calendar = Calendar.getInstance();

        activityUtil = new ActivityUtil(this);

        displayName = (EditText) findViewById(R.id.displaynameEdit);
        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);

        finishButton = (Button) findViewById(R.id.finishButton);

        locationVMList = new ArrayList<LocationVM>();
        setLocation();

        babyNumberArray = new String[]{"1","2","3"};
        ArrayAdapter<String> babyAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,babyNumberArray);

        babySpinner = (Spinner) findViewById(R.id.babySpinner);
        babySpinner.setAdapter(babyAdapter);

        titleText.setText(getIntent().getStringExtra("first_name") + " " + getString(R.string.signup_details_greeting));

        parentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = parentType.getCheckedRadioButtonId();
                parent = (RadioButton) findViewById(id);
                parentClicked=true;
                setMoreDetailsVisible(id);
            }
        });

        babyGender1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = babyGender1.getCheckedRadioButtonId();
                babySelected=true;
                baby1 = (RadioButton) findViewById(id);
            }
        });

        babyGender2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = babyGender2.getCheckedRadioButtonId();
                baby2 = (RadioButton) findViewById(id);
            }
        });

        babyGender3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = babyGender1.getCheckedRadioButtonId();
                baby3 = (RadioButton) findViewById(id);
            }
        });

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

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1 = true;
                birthdayClick2 = false;
                birthdayClick3 = false;
                showDatePicker();
            }
        };
        birthday1.setOnClickListener(onClickListener);
        birthdayLabel1.setOnClickListener(onClickListener);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1 = false;
                birthdayClick2 = true;
                birthdayClick3 = false;
                showDatePicker();
            }
        };
        birthday2.setOnClickListener(onClickListener);
        birthdayLabel2.setOnClickListener(onClickListener);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1 = false;
                birthdayClick2 = false;
                birthdayClick3 = true;
                showDatePicker();
            }
        };
        birthday3.setOnClickListener(onClickListener);
        birthdayLabel3.setOnClickListener(onClickListener);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitDetails();
            };
        });
    }

    private void submitDetails() {
        final String displayname = displayName.getText().toString().trim();

        if(parentClicked) {
            parenttype = getParentType(parent);

            if (parenttype.equals("NA")) {
                babynum = "0";
            } else {
                babynum = babySpinner.getSelectedItem().toString();
                if (babynum.equals("1")) {
                    babygen1 = getGender(baby1);
                } else if (babynum.equals("2")) {
                    babygen1 = getGender(baby1);
                    babygen2 = getGender(baby2);
                } else if (babynum.equals("3")) {
                    babygen1 = getGender(baby1);
                    babygen2 = getGender(baby2);
                    babygen3 = getGender(baby3);
                }
            }
        }

        if (isValid()) {
            Log.d(this.getClass().getSimpleName(),
                    "signupInfo: \n displayname="+displayname+"\n locationId="+locationId+"\n parentType="+parenttype+"\n numBaby="+babynum+
                            "\n babyGen1="+babygen1+"\n babyBirthday1="+year1+"-"+month1+"-"+day1+
                            "\n babyGen2="+babygen2+"\n babyBirthday2="+year2+"-"+month2+"-"+day2+
                            "\n babyGen3="+babygen3+"\n babyBirthday3="+year3+"-"+month3+"-"+day3);

            AppController.getApi().signUpInfo(displayname, DefaultValues.DEFAULT_PARENT_BIRTH_YEAR, locationId, parenttype, babynum,
                    babygen1, babygen2, babygen3,
                    year1, month1, day1, year2, month2, day2, year3, month3, day3,
                    AppController.getInstance().getSessionId(),
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            Log.d(SignupDetailActivity.class.getSimpleName(), "submitDetails: api.signUpInfo.success");
                            initNewUser();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            String errorMsg = SignupDetailActivity.this.activityUtil.getResponseBody(error.getResponse());
                            if (error.getResponse().getStatus() == 500 &&
                                    error.getResponse() != null &&
                                    !StringUtils.isEmpty(errorMsg)) {
                                ActivityUtil.alert(SignupDetailActivity.this, errorMsg);
                            } else {
                                //ActivityUtil.alert(SignupDetailActivity.this, getString(R.string.signup_details_error_info));
                                ActivityUtil.alert(SignupDetailActivity.this,
                                        "\""+displayname+"\" "+getString(R.string.signup_details_error_displayname_already_exists));

                            }
                            error.printStackTrace();
                        }
                    });
        }
    }

    private void initNewUser() {
        Log.d(this.getClass().getSimpleName(), "initNewUser");
        AppController.getApi().initNewUser(AppController.getInstance().getSessionId(), new Callback<UserVM>() {
            @Override
            public void success(UserVM userVM, Response response) {
                Log.d(SignupDetailActivity.class.getSimpleName(), "initNewUser.success");
                startActivity(new Intent(SignupDetailActivity.this, SplashActivity.class));
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private String getParentType(RadioButton radioButton) {
        if (radioButton == null)
            return "";

        if (radioButton.getText().toString().equals(getString(R.string.signup_details_mom))) {
            return "MOM";
        } else if (radioButton.getText().toString().equals(getString(R.string.signup_details_dad))) {
            return "DAD";
        } else if (radioButton.getText().toString().equals(getString(R.string.signup_details_soon_mom))) {
            return "SOON_MOM";
        } else if (radioButton.getText().toString().equals(getString(R.string.signup_details_soon_dad))) {
            return "SOON_DAD";
        } else if (radioButton.getText().toString().equals(getString(R.string.signup_details_not_parent))) {
            return "NA";
        }
        return "";
    }

    private String getGender(RadioButton radioButton) {
        if (radioButton == null)
            return "";

        if (radioButton.getText().toString().equals(getString(R.string.signup_details_boy))) {
            return "Male";
        } else if (radioButton.getText().toString().equals(getString(R.string.signup_details_girl))) {
            return "Female";
        }
        return "";
    }

    private void setMoreDetailsVisible(int id) {
        RadioButton type = (RadioButton) parentType.findViewById(id);
        if (!type.getText().toString().equals(getString(R.string.signup_details_not_parent))) {
            babynumberLayout.setVisibility(View.VISIBLE);
            setBabyDetailsVisible();
        } else {
            babynumberLayout.setVisibility(View.GONE);
            babyDetailsLayout1.setVisibility(View.GONE);
            babyDetailsLayout2.setVisibility(View.GONE);
            babyDetailsLayout3.setVisibility(View.GONE);
        }
    }

    private void setBabyDetailsVisible() {
        if (babySpinner.getSelectedItem() == "1") {
            babyDetailsLayout1.setVisibility(View.VISIBLE);
            babyDetailsLayout2.setVisibility(View.GONE);
            babyDetailsLayout3.setVisibility(View.GONE);
        } else if (babySpinner.getSelectedItem() == "2") {
            babyDetailsLayout1.setVisibility(View.VISIBLE);
            babyDetailsLayout2.setVisibility(View.VISIBLE);
            babyDetailsLayout3.setVisibility(View.GONE);
        } else if (babySpinner.getSelectedItem() == "3") {
            babyDetailsLayout1.setVisibility(View.VISIBLE);
            babyDetailsLayout2.setVisibility(View.VISIBLE);
            babyDetailsLayout3.setVisibility(View.VISIBLE);
        }

        babySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (babySpinner.getSelectedItem() == "1") {
                    babyDetailsLayout1.setVisibility(View.VISIBLE);
                    babyDetailsLayout2.setVisibility(View.GONE);
                    babyDetailsLayout3.setVisibility(View.GONE);
                } else if (babySpinner.getSelectedItem() == "2") {
                    babyDetailsLayout1.setVisibility(View.VISIBLE);
                    babyDetailsLayout2.setVisibility(View.VISIBLE);
                    babyDetailsLayout3.setVisibility(View.GONE);
                } else if (babySpinner.getSelectedItem() == "3") {
                    babyDetailsLayout1.setVisibility(View.VISIBLE);
                    babyDetailsLayout2.setVisibility(View.VISIBLE);
                    babyDetailsLayout3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

                ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(SignupDetailActivity.this, android.R.layout.simple_spinner_item, locations);
                locationSpinner.setAdapter(locationAdapter);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        LoginActivity.startLoginActivity(SignupDetailActivity.this);
    }

    private void setBirthdays() {
        if (birthdayClick1) {
            birthdayLabel1.setText(calendar.get(Calendar.YEAR) + "-" + getMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year1 = calendar.get(Calendar.YEAR) + "";
            month1 = getMonth(calendar.get(Calendar.MONTH)) + "";
            day1 = calendar.get(Calendar.DAY_OF_MONTH) + "";
        } else if (birthdayClick2) {
            birthdayLabel2.setText(calendar.get(Calendar.YEAR) + "-" + getMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year2 = calendar.get(Calendar.YEAR) + "";
            month2 = getMonth(calendar.get(Calendar.MONTH)) + "";
            day2 = calendar.get(Calendar.DAY_OF_MONTH) + "";
        } else if (birthdayClick3) {
            birthdayLabel3.setText(calendar.get(Calendar.YEAR) + "-" + getMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year3 = calendar.get(Calendar.YEAR) + "";
            month3 = getMonth(calendar.get(Calendar.MONTH)) + "";
            day3 = calendar.get(Calendar.DAY_OF_MONTH) + "";
        }
    }

    private void showDatePicker() {
        new DatePickerDialog(SignupDetailActivity.this, datePicker, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
    }

    private int getMonth(int month) {
        return month + 1;
    }

    private boolean isValid(){
        boolean valid = true;
        String error = "";
        if (!Validation.hasText(displayName)) {
            error = appendError(error, getString(R.string.signup_details_error_displayname_not_entered));
            valid = false;
        }
        if (locationId == -1) {
            error = appendError(error, getString(R.string.signup_details_error_location_not_entered));
            valid = false;
        }
        if (!parentClicked) {
            error = appendError(error, getString(R.string.signup_details_error_status_not_entered));
            valid = false;
        }
        if (!parenttype.equals("NA")) {
            int num = StringUtils.isEmpty(babynum)? 0 : Integer.parseInt(babynum);
            if (num >= 1) {
                if (StringUtils.isEmpty(day1) || StringUtils.isEmpty(babygen1)) {
                    error = appendError(error, getString(R.string.signup_details_error_baby_not_entered));
                    valid = false;
                }
            }
            if (num >= 2) {
                if (StringUtils.isEmpty(day2) || StringUtils.isEmpty(babygen2)) {
                    error = appendError(error, getString(R.string.signup_details_error_baby_not_entered));
                    valid = false;
                }
            }
            if (num >= 3) {
                if (StringUtils.isEmpty(day3) || StringUtils.isEmpty(babygen3)) {
                    error = appendError(error, getString(R.string.signup_details_error_baby_not_entered));
                    valid = false;
                }
            }
        }

        if (!valid)
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        return valid;
    }

    private String appendError(String error, String newError) {
        if (!StringUtils.isEmpty(error))
            error += "\n";
        return error + newError;
    }
}