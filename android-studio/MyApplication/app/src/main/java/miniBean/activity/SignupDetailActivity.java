
package miniBean.activity;

import android.app.ActionBar;
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
import android.widget.RelativeLayout;
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
    private String[] babyArray,day,month,year;
    private Spinner locationSpinner,babySpinner,daySpinner1,daySpinner2,daySpinner3,monthSpinner1,monthSpinner2,monthSpinner3,yearSpinner1,yearSpinner2,yearSpinner3;
    private Button finishButton;
    private EditText displayName;
    private RadioGroup parentType,babyGender1,babyGender2,babyGender3;
    private RadioButton parent,baby1,baby2,baby3;
    private LinearLayout babynumberLayout;
    private RelativeLayout detailLayout,detailLayout1,detailLayout2;
    private TextView titleText;

    public List<String> locations;
    private List<LocationVM> locationVMList;

    private int locationId;
    private Calendar calendar;
    private ImageView birthday1,birthday2,birthday3;
    private TextView birthdayLabel1,birthdayLabel2,birthdayLabel3;

    private String year1,month1,day1,year2,month2,day2,year3,month3,day3;


    private boolean birthdayClick1=false,birthdayClick2=false,birthdayClick3=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_detail_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.signup_actionbar);

        babynumberLayout= (LinearLayout) findViewById(R.id.babynumberLayout);

        titleText= (TextView) findViewById(R.id.titleText);

        birthday1= (ImageView) findViewById(R.id.birthday1);
        birthday2= (ImageView) findViewById(R.id.birthday2);
        birthday3= (ImageView) findViewById(R.id.birthday3);


        birthdayLabel1= (TextView) findViewById(R.id.birthdayLabel1);
        birthdayLabel2= (TextView) findViewById(R.id.birthdayLabel2);
        birthdayLabel3= (TextView) findViewById(R.id.birthdayLabel3);

        detailLayout= (RelativeLayout) findViewById(R.id.babyDetaiLayout);
        detailLayout1= (RelativeLayout) findViewById(R.id.babyDetaiLayout1);
        detailLayout2= (RelativeLayout) findViewById(R.id.babyDetaiLayout2);

        parentType= (RadioGroup) findViewById(R.id.parentRadio);
        babyGender1= (RadioGroup) findViewById(R.id.babyRadio1);
        babyGender2= (RadioGroup) findViewById(R.id.babyRadio2);
        babyGender3= (RadioGroup) findViewById(R.id.babyRadio3);

        calendar = Calendar.getInstance();

        displayName= (EditText) findViewById(R.id.displaynameEdit);
        locationSpinner= (Spinner) findViewById(R.id.locationSpinner);

        babySpinner= (Spinner) findViewById(R.id.babySpinner);

        finishButton= (Button) findViewById(R.id.finishButton);

        this.babyArray=new String[]{"1","2","3"};

        locationVMList = new ArrayList<LocationVM>();
        setLocation();

        ArrayAdapter<String> babyAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,babyArray);
        babySpinner.setAdapter(babyAdapter);


        titleText.setText("Hi " + getIntent().getStringExtra("first_name"));

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

        parentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = parentType.getCheckedRadioButtonId();
                parent = (RadioButton) findViewById(id);
                setVisible(id);
            }
        });

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String loc=locationSpinner.getSelectedItem().toString();

                for(LocationVM vm: locationVMList)
                {
                    if(vm.getDisplayName().equals(loc)){
                        locationId=Integer.parseInt(vm.getId().toString());
                        System.out.println("locationid:::::"+vm.getId());
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        birthday1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1=true;
                birthdayClick2=false;
                birthdayClick3=false;
                setDate();
            }
        });

        birthday2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1=false;
                birthdayClick2=true;
                birthdayClick3=false;
                setDate();
            }
        });

        birthday3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1=false;
                birthdayClick2=false;
                birthdayClick3=true;
                setDate();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String displayname="",parenttype="",babynum="",babygen1="",babygen2="",babygen3="";
                //year1="";month1="";day1="";year2="";month2="";day2="";year3="";month3="";day3="";

                displayname=displayName.getText().toString();

                System.out.println("day1:::"+day1);
                System.out.println("month1::"+month1);
                System.out.println("year1::"+year1);

                System.out.println("day2:::"+day2);
                System.out.println("month2::"+month2);
                System.out.println("year2::"+year2);

                System.out.println("day3:::"+day3);
                System.out.println("month3::"+month3);
                System.out.println("year3::"+year3);

                if(parent.getText().toString().equals("Soon-tobe-Dad")){
                    parenttype="SOON_DAD";
                }else if(parent.getText().toString().equals("Soon-tobe-Mom")){
                    parenttype="SOON_MOM";
                }else if(parent.getText().toString().equals("Not-mom-or-dad")){
                    parenttype="NA";
                }else if(parent.getText().toString().equals("MOM")){
                    parenttype="MOM";
                }else if(parent.getText().toString().equals("DAD")){
                    parenttype="DAD";
                }

                if(!parenttype.equals("NA")){
                    babynum=babySpinner.getSelectedItem().toString();
                    if(babynum.equals("1")){
                        babygen1 = baby1.getText().toString();
                    }else if(babynum.equals("2")){
                        babygen1 = baby1.getText().toString();
                        babygen2 = baby2.getText().toString();
                    }else if(babynum.equals("3")){
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
                                System.out.println("signup info success:::::::::::");
                                initNewUser();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                error.printStackTrace();
                            }
                        });
            };
        });
    }

    private void initNewUser() {
        Log.d(this.getClass().getSimpleName(), "iniNewUser");
        AppController.getApi().initNewUser(AppController.getInstance().getSessionId(), new Callback<UserVM>() {
            @Override
            public void success(UserVM userVM, Response response) {
                Log.d(SignupDetailActivity.class.getSimpleName(), "iniNewUser.success");
                startActivity(new Intent(SignupDetailActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void setVisible(int id){
        RadioButton type= (RadioButton) parentType.findViewById(id);
        if (!type.getText().toString().equals("Not-mom-or-dad")) {
            babynumberLayout.setVisibility(View.VISIBLE);
            setDetailVisible();
        } else {
            babynumberLayout.setVisibility(View.GONE);
            detailLayout.setVisibility(View.GONE);
            detailLayout1.setVisibility(View.GONE);
            detailLayout2.setVisibility(View.GONE);
        }
    }

    private void setDetailVisible(){
        if (babySpinner.getSelectedItem()=="1"){
            detailLayout.setVisibility(View.VISIBLE);
            detailLayout1.setVisibility(View.GONE);
            detailLayout2.setVisibility(View.GONE);
        }else if(babySpinner.getSelectedItem()=="2"){
            detailLayout1.setVisibility(View.VISIBLE);
            detailLayout.setVisibility(View.VISIBLE);
            detailLayout2.setVisibility(View.GONE);
        }else if(babySpinner.getSelectedItem()=="3"){
            detailLayout2.setVisibility(View.VISIBLE);
            detailLayout.setVisibility(View.VISIBLE);
            detailLayout1.setVisibility(View.VISIBLE);
        }

        babySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (babySpinner.getSelectedItem()=="1"){
                    detailLayout.setVisibility(View.VISIBLE);
                    detailLayout1.setVisibility(View.GONE);
                    detailLayout2.setVisibility(View.GONE);
                }else if(babySpinner.getSelectedItem()=="2"){
                    detailLayout1.setVisibility(View.VISIBLE);
                    detailLayout.setVisibility(View.VISIBLE);
                    detailLayout2.setVisibility(View.GONE);
                }else if(babySpinner.getSelectedItem()=="3"){
                    detailLayout2.setVisibility(View.VISIBLE);
                    detailLayout.setVisibility(View.VISIBLE);
                    detailLayout1.setVisibility(View.VISIBLE);
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

    public void updatedate() {
        if (birthdayClick1) {
            birthdayLabel1.setText(calendar.get(Calendar.YEAR) + "-" + showMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year1=calendar.get(Calendar.YEAR)+"";
            month1=showMonth(calendar.get(Calendar.MONTH))+"";
            day1=calendar.get(Calendar.DAY_OF_MONTH)+"";

            day2=month2=year2="";
            day3=month3=year3="";
        }else if(birthdayClick2){
            birthdayLabel2.setText(calendar.get(Calendar.YEAR) + "-" + showMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year2=calendar.get(Calendar.YEAR)+"";
            month2=showMonth(calendar.get(Calendar.MONTH))+"";
            day2=calendar.get(Calendar.DAY_OF_MONTH)+"";

            day3=month3=year3="";
        }else if(birthdayClick3){
            birthdayLabel3.setText(calendar.get(Calendar.YEAR) + "-" + showMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year3=calendar.get(Calendar.YEAR)+"";
            month3=showMonth(calendar.get(Calendar.MONTH))+"";
            day3=calendar.get(Calendar.DAY_OF_MONTH)+"";
        }
    }
    public void setDate(){

        new DatePickerDialog(SignupDetailActivity.this,d,calendar.get(calendar.YEAR),calendar.get(calendar.MONTH),calendar.get(calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updatedate();
        }

    };

    public int showMonth(int month)
    {
        int showMonth = month;
        switch(showMonth)
        {
            case 0:
                showMonth = showMonth + 1;
                break;
            case 1:
                showMonth = showMonth + 1;
                break;
            case 2:
                showMonth = showMonth + 1;
                break;
            case 3:
                showMonth = showMonth + 1;
                break;
            case 4:
                showMonth = showMonth + 1;
                break;
            case 5:
                showMonth = showMonth + 1;
                break;
            case 6:
                showMonth = showMonth + 1;
                break;
            case 7:
                showMonth = showMonth + 1;
                break;
            case 8:
                showMonth = showMonth + 1;
                break;
            case 9:
                showMonth = showMonth + 1;
                break;
            case 10:
                showMonth = showMonth + 1;
                break;
            case 11:
                showMonth = showMonth + 1;
                break;

        }
        return showMonth;
    }
}