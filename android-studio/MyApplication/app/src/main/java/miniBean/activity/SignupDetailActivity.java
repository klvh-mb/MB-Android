
package miniBean.activity;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.LocationVM;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignupDetailActivity extends Activity {
    private String[] babyNumberArray;
    private Spinner locationSpinner,babySpinner;
    private Button finishButton;
    private EditText displayName;
    private RadioGroup parentType,babyGender1,babyGender2,babyGender3;
    private RadioButton parent,baby1,baby2,baby3;
    private LinearLayout babynumberLayout;
    private LinearLayout babyDetailsLayout1,babyDetailsLayout2,babyDetailsLayout3;
    private TextView titleText;

    public List<String> locations;
    private List<LocationVM> locationVMList;

    private int locationId = -1;
    private Calendar calendar;
    private ImageView birthday1,birthday2,birthday3;
    private TextView birthdayLabel1,birthdayLabel2,birthdayLabel3;

    private String year1,month1,day1,year2,month2,day2,year3,month3,day3;

    private boolean birthdayClick1 = false, birthdayClick2 = false, birthdayClick3 = false;

    private DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            setBirthdays();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_detail_activity);

        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getActionBar().setCustomView(R.layout.signup_actionbar);
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
                setMoreDetailsVisible(id);
            }
        });

        babyGender1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id=babyGender1.getCheckedRadioButtonId();
                baby1=(RadioButton)findViewById(id);
            }
        });

        babyGender2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id=babyGender2.getCheckedRadioButtonId();
                baby2=(RadioButton)findViewById(id);
            }
        });

        babyGender3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id=babyGender1.getCheckedRadioButtonId();
                baby3=(RadioButton)findViewById(id);
            }
        });

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                locationId = -1;
                String loc = locationSpinner.getSelectedItem().toString();
                for(LocationVM vm: locationVMList) {
                    if(vm.getDisplayName().equals(loc)){
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
                birthdayClick1=true;
                birthdayClick2=false;
                birthdayClick3=false;
                showDatePicker();
            }
        };
        birthday1.setOnClickListener(onClickListener);
        birthdayLabel1.setOnClickListener(onClickListener);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1=false;
                birthdayClick2=true;
                birthdayClick3=false;
                showDatePicker();
            }
        };
        birthday2.setOnClickListener(onClickListener);
        birthdayLabel2.setOnClickListener(onClickListener);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1=false;
                birthdayClick2=false;
                birthdayClick3=true;
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
        Log.d(SignupDetailActivity.this.getClass().getSimpleName(), "finshButton.onClick: year1="+year1+" day1="+day1+" month1="+month1);
        Log.d(SignupDetailActivity.this.getClass().getSimpleName(), "finshButton.onClick: year2="+year2+" day2="+day2+" month2="+month2);
        Log.d(SignupDetailActivity.this.getClass().getSimpleName(), "finshButton.onClick: year3="+year3+" day3="+day3+" month3="+month3);

        String displayname = "", parenttype = "", babynum = "", babygen1 = "", babygen2 = "", babygen3 = "";

        displayname = displayName.getText().toString();

        if (parent.getText().toString().equals(getString(R.string.signup_details_mom))) {
            parenttype = "MOM";
        } else if(parent.getText().toString().equals(getString(R.string.signup_details_dad))) {
            parenttype = "DAD";
        } else if(parent.getText().toString().equals(getString(R.string.signup_details_soon_mom))) {
            parenttype = "SOON_MOM";
        } else if (parent.getText().toString().equals(getString(R.string.signup_details_soon_dad))) {
            parenttype = "SOON_DAD";
        } else if(parent.getText().toString().equals(getString(R.string.signup_details_not_parent))) {
            parenttype = "NA";
        }

        if (parenttype.equals("NA")) {
            babynum = "0";
        } else {
            babynum = babySpinner.getSelectedItem().toString();
            if (babynum.equals("1")) {
                babygen1 = baby1.getText().toString();
            } else if(babynum.equals("2")) {
                babygen1 = baby1.getText().toString();
                babygen2 = baby2.getText().toString();
            } else if(babynum.equals("3")) {
                babygen1 = baby1.getText().toString();
                babygen2 = baby2.getText().toString();
                babygen3 = baby3.getText().toString();
            }
        }

        int defaultParentBirthYear = 9999;
        AppController.getApi().signUpInfo(displayname, defaultParentBirthYear, locationId, parenttype, babynum,
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
                        error.printStackTrace();
                    }
                });
    }

    private void initNewUser() {
        Log.d(this.getClass().getSimpleName(), "initNewUser");
        AppController.getApi().initNewUser(AppController.getInstance().getSessionId(), new Callback<UserVM>() {
            @Override
            public void success(UserVM userVM, Response response) {
                Log.d(SignupDetailActivity.class.getSimpleName(), "initNewUser.success");
                startActivity(new Intent(SignupDetailActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void setMoreDetailsVisible(int id){
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

    private void setBabyDetailsVisible(){
        if (babySpinner.getSelectedItem()=="1"){
            babyDetailsLayout1.setVisibility(View.VISIBLE);
            babyDetailsLayout2.setVisibility(View.GONE);
            babyDetailsLayout3.setVisibility(View.GONE);
        }else if(babySpinner.getSelectedItem()=="2"){
            babyDetailsLayout1.setVisibility(View.VISIBLE);
            babyDetailsLayout2.setVisibility(View.VISIBLE);
            babyDetailsLayout3.setVisibility(View.GONE);
        }else if(babySpinner.getSelectedItem()=="3"){
            babyDetailsLayout1.setVisibility(View.VISIBLE);
            babyDetailsLayout2.setVisibility(View.VISIBLE);
            babyDetailsLayout3.setVisibility(View.VISIBLE);
        }

        babySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (babySpinner.getSelectedItem()=="1"){
                    babyDetailsLayout1.setVisibility(View.VISIBLE);
                    babyDetailsLayout2.setVisibility(View.GONE);
                    babyDetailsLayout3.setVisibility(View.GONE);
                }else if(babySpinner.getSelectedItem()=="2"){
                    babyDetailsLayout1.setVisibility(View.VISIBLE);
                    babyDetailsLayout2.setVisibility(View.VISIBLE);
                    babyDetailsLayout3.setVisibility(View.GONE);
                }else if(babySpinner.getSelectedItem()=="3"){
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

    private void setLocation(){
        AppController.getApi().getAllDistricts(AppController.getInstance().getSessionId(),new Callback< List < LocationVM>>(){
            @Override
            public void success(List<LocationVM> locationVMs, Response response) {
                locations = new ArrayList<String>();

                locations.add(getString(R.string.signup_details_location));
                for(int i=0; i<locationVMs.size(); i++){
                    locations.add(locationVMs.get(i).getDisplayName());
                    locationVMList.addAll(locationVMs);
                }

                ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(SignupDetailActivity.this,android.R.layout.simple_spinner_item,locations);
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
            year1 = calendar.get(Calendar.YEAR)+"";
            month1 = getMonth(calendar.get(Calendar.MONTH))+"";
            day1 = calendar.get(Calendar.DAY_OF_MONTH)+"";
        } else if(birthdayClick2) {
            birthdayLabel2.setText(calendar.get(Calendar.YEAR) + "-" + getMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year2 = calendar.get(Calendar.YEAR)+"";
            month2 = getMonth(calendar.get(Calendar.MONTH))+"";
            day2 = calendar.get(Calendar.DAY_OF_MONTH)+"";
        } else if(birthdayClick3) {
            birthdayLabel3.setText(calendar.get(Calendar.YEAR) + "-" + getMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year3 = calendar.get(Calendar.YEAR)+"";
            month3 = getMonth(calendar.get(Calendar.MONTH))+"";
            day3 = calendar.get(Calendar.DAY_OF_MONTH)+"";
        }
    }

    private void showDatePicker(){
        new DatePickerDialog(SignupDetailActivity.this,datePicker,calendar.get(calendar.YEAR),calendar.get(calendar.MONTH),calendar.get(calendar.DAY_OF_MONTH)).show();
    }

    private int getMonth(int month) {
        return month + 1;
    }
}