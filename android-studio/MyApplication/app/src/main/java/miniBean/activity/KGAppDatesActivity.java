package miniBean.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.adapter.KGBookmarkListAdapter;
import miniBean.app.AppController;
import miniBean.app.TrackedFragmentActivity;
import miniBean.viewmodel.KindergartenVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class KGAppDatesActivity extends TrackedFragmentActivity {

    private static final String TAG = KGAppDatesActivity.class.getName();

    private TextView allSchoolsText, hkSchoolsText, klSchoolsText, ntSchoolsText;
    private ListView appDateList;
    private KGBookmarkListAdapter appDateListAdapter;
    private List<KindergartenVM> allSchoolVMList;
    private List<KindergartenVM> hkSchoolVMList;
    private List<KindergartenVM> klSchoolVMList;
    private List<KindergartenVM> ntSchoolVMList;

    private int selected = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.schools_app_dates_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.schools_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_maroon));

        TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitle);
        actionbarTitle.setText(getString(R.string.schools_application_date));

        allSchoolsText = (TextView) findViewById(R.id.allSchoolsText);
        hkSchoolsText = (TextView) findViewById(R.id.hkSchoolsText);
        klSchoolsText = (TextView) findViewById(R.id.klSchoolsText);
        ntSchoolsText = (TextView) findViewById(R.id.ntSchoolsText);
        appDateList = (ListView) findViewById(R.id.appDateList);

        appDateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KindergartenVM vm = appDateListAdapter.getItem(i);
                Intent intent = new Intent(KGAppDatesActivity.this, KGCommunityActivity.class);
                intent.putExtra("commId",vm.getCommId());
                intent.putExtra("id", vm.getId());
                startActivity(intent);
            }
        });

        ImageView backImage = (ImageView) findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSchoolAppDates();
    }

    private void getSchoolAppDates(){
        AppController.getApi().getKGAppDates(AppController.getInstance().getSessionId(), new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> vms, Response response) {
                Log.d(KGAppDatesActivity.this.getClass().getSimpleName(), "getSchoolAppDates.api.getKGAppDates: " + vms.size());
                allSchoolVMList = vms;
                appDateListAdapter = new KGBookmarkListAdapter(KGAppDatesActivity.this, allSchoolVMList);
                appDateList.setAdapter(appDateListAdapter);
                appDateListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(KGAppDatesActivity.class.getSimpleName(), "getSchoolAppDates: failure", error);
            }
        });
    }
}

