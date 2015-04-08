
package miniBean.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.MyApi;
import miniBean.viewmodel.LocationVM;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        AppController.api = restAdapter.create(MyApi.class);

        setContentView(R.layout.signup_detail);


        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.signup_actionbar);

        babynumberLayout= (LinearLayout) findViewById(R.id.babynumberLayout);

        titleText= (TextView) findViewById(R.id.titleText);

        detailLayout= (RelativeLayout) findViewById(R.id.babyDetaiLayout);
        detailLayout1= (RelativeLayout) findViewById(R.id.babyDetaiLayout1);
        detailLayout2= (RelativeLayout) findViewById(R.id.babyDetaiLayout2);

        parentType= (RadioGroup) findViewById(R.id.parentRadio);
        babyGender1= (RadioGroup) findViewById(R.id.babyRadio1);
        babyGender2= (RadioGroup) findViewById(R.id.babyRadio2);
        babyGender3= (RadioGroup) findViewById(R.id.babyRadio3);

        displayName= (EditText) findViewById(R.id.displaynameEdit);
        locationSpinner= (Spinner) findViewById(R.id.locationSpinner);

        babySpinner= (Spinner) findViewById(R.id.babySpinner);

        daySpinner1= (Spinner) findViewById(R.id.daySpinner1);
        monthSpinner1= (Spinner) findViewById(R.id.monthSpinner1);
        yearSpinner1= (Spinner) findViewById(R.id.yearSpinner1);

        daySpinner2= (Spinner) findViewById(R.id.daySpinner2);
        monthSpinner2= (Spinner) findViewById(R.id.monthSpinner2);
        yearSpinner2= (Spinner) findViewById(R.id.yearSpinner2);

        daySpinner3= (Spinner) findViewById(R.id.daySpinner3);
        monthSpinner3= (Spinner) findViewById(R.id.monthSpinner3);
        yearSpinner3= (Spinner) findViewById(R.id.yearSpinner3);

        finishButton= (Button) findViewById(R.id.finishButton);

        this.babyArray=new String[]{"1","2","3"};

        locationVMList = new ArrayList<LocationVM>();


        this.day=new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
        this.month=new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};
        this.year=new String[]{"1990","2000","2001","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015"};

        setLocation();

        ArrayAdapter<String> babyAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,babyArray);
        babySpinner.setAdapter(babyAdapter);


        ArrayAdapter<String> dayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,day);
        daySpinner1.setAdapter(dayAdapter);
        daySpinner2.setAdapter(dayAdapter);
        daySpinner3.setAdapter(dayAdapter);

        ArrayAdapter<String> monthAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,month);
        monthSpinner1.setAdapter(monthAdapter);
        monthSpinner2.setAdapter(monthAdapter);
        monthSpinner3.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,year);
        yearSpinner1.setAdapter(yearAdapter);
        yearSpinner2.setAdapter(yearAdapter);
        yearSpinner3.setAdapter(yearAdapter);

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

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String displayname="",parenttype="",babynum="",babygen1="",babygen2="",babygen3="",year1="",month1="",day1="",year2="",month2="",day2="",year3="",month3="",day3="";

                displayname=displayName.getText().toString();


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
                        year1 = yearSpinner1.getSelectedItem().toString();
                        month1 = monthSpinner1.getSelectedItem().toString();
                        day1 = daySpinner1.getSelectedItem().toString();
                    }else if(babynum.equals("2")){

                        babygen1 = baby1.getText().toString();
                        year1 = yearSpinner1.getSelectedItem().toString();
                        month1 = monthSpinner1.getSelectedItem().toString();
                        day1 = daySpinner1.getSelectedItem().toString();

                        babygen2 = baby2.getText().toString();
                        year2 = yearSpinner2.getSelectedItem().toString();
                        month2 = monthSpinner2.getSelectedItem().toString();
                        day2 = daySpinner2.getSelectedItem().toString();

                    }else if(babynum.equals("3")){
                        babygen1 = baby1.getText().toString();
                        year1 = yearSpinner1.getSelectedItem().toString();
                        month1 = monthSpinner1.getSelectedItem().toString();
                        day1 = daySpinner1.getSelectedItem().toString();

                        babygen2 = baby2.getText().toString();
                        year2 = yearSpinner2.getSelectedItem().toString();
                        month2 = monthSpinner2.getSelectedItem().toString();
                        day2 = daySpinner2.getSelectedItem().toString();


                        babygen3 = baby3.getText().toString();
                        year3 = yearSpinner3.getSelectedItem().toString();
                        month3 = monthSpinner3.getSelectedItem().toString();
                        day3 = daySpinner3.getSelectedItem().toString();

                    }


                }


               AppController.api.signUpInfo(displayname, 1992, locationId, parenttype, babynum, babygen1, babygen2, babygen3, year1,
                       month1, day1, year2, month2, day2, year3, month3, day3, AppController.getInstance().getSessionId(), new Callback<Response>() {
                   @Override
                   public void success(Response response, Response response2) {

                       Intent intent=new Intent(SignupDetailActivity.this,MainActivity.class);
                       startActivity(intent);

                   }

                   @Override
                   public void failure(RetrofitError error) {
                            error.printStackTrace();
                   }
               });
            };

        });
    }

    public void setVisible(int id){

        RadioButton type= (RadioButton) parentType.findViewById(id);
        if(!type.getText().toString().equals("Not-mom-or-dad")){
            babynumberLayout.setVisibility(View.VISIBLE);
            setDetailVisible();
        }else{
            babynumberLayout.setVisibility(View.GONE);
            detailLayout.setVisibility(View.GONE);
            detailLayout1.setVisibility(View.GONE);
            detailLayout2.setVisibility(View.GONE);
        }
    }

    public void setDetailVisible(){
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
        AppController.api.getAllDistricts(AppController.getInstance().getSessionId(),new Callback< List < LocationVM>>(){

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
        if (isTaskRoot()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_app)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AppController.getInstance().clearPreferences();
                            AppController.getInstance().clearAll();
                            SignupDetailActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }
    }
}