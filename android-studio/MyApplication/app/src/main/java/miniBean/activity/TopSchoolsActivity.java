package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.TopBookmarkedKGListAdapter;
import miniBean.adapter.TopBookmarkedPNListAdapter;
import miniBean.adapter.TopViewedPNListAdapter;
import miniBean.adapter.TopViewedKGListAdapter;
import miniBean.app.AppController;
import miniBean.viewmodel.KindergartenVM;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopSchoolsActivity extends FragmentActivity {
    private ListView topViewedList,topBookmarkedList;
    private TopViewedPNListAdapter topViewedPNListAdapter;
    private TopBookmarkedPNListAdapter topBookmarkedPNListAdapter;
    private TopViewedKGListAdapter topViewedKGListAdapter;
    private TopBookmarkedKGListAdapter topBookmarkedKGListAdapter;
    private List<PreNurseryVM> topViewedPNs,topBookmarkedPNs;
    private List<KindergartenVM> topViewedKGs,topBookmarkedKGs;

    private ImageView backAction,scrollButton;
    private ScrollView scrollView;
    private boolean scrollUp=true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.top_schools_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.top_schools_actionbar);

        TextView actionbarTitle = (TextView) findViewById(R.id.title);

        if (getIntent().getStringExtra("flag").equals("SchoolsPNFragment")) {
            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_green));
            actionbarTitle.setText(getString(R.string.schools_top_pn_ranking));
        } else {
            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_maroon));
            actionbarTitle.setText(getString(R.string.schools_top_kg_ranking));
        }

        topViewedPNs = new ArrayList<>();
        topBookmarkedPNs = new ArrayList<>();
        topViewedKGs = new ArrayList<>();
        topBookmarkedKGs = new ArrayList<>();

        topViewedList = (ListView) findViewById(R.id.topViewedList);
        topBookmarkedList = (ListView) findViewById(R.id.topBookmarkedList);
        backAction = (ImageView) findViewById(R.id.backImage);
        scrollButton = (ImageView) findViewById(R.id.scrollButton);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        backAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(getIntent().getStringExtra("flag").equals("SchoolsPNFragment")){
            getTopViewPNs();
            getTopBookmarkPNs();
        }else{
            getTopViewsKGs();
            getTopBookmarkKGs();
        }

        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scrollUp) {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                    scrollUp=false;
                }else {
                    scrollView.fullScroll(View.FOCUS_UP);
                    scrollUp=true;
                }
            }
        });
    }

    private void getTopViewPNs(){
        AppController.getApi().getTopViewedPNs(AppController.getInstance().getSessionId(),new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> vms, Response response) {
                topViewedPNs.addAll(vms);
                topViewedPNListAdapter = new TopViewedPNListAdapter(TopSchoolsActivity.this,topViewedPNs);
                topViewedList.setAdapter(topViewedPNListAdapter);
                scrollView.fullScroll(View.FOCUS_UP);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getTopBookmarkPNs(){
        AppController.getApi().getTopBookmarkedPNs(AppController.getInstance().getSessionId(),new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> vms, Response response) {
                topBookmarkedPNs.addAll(vms);
                topBookmarkedPNListAdapter = new TopBookmarkedPNListAdapter(TopSchoolsActivity.this,topBookmarkedPNs);
                topBookmarkedList.setAdapter(topBookmarkedPNListAdapter);
                scrollView.fullScroll(View.FOCUS_UP);
            }

            @Override
            public void failure(RetrofitError error) {
                   error.printStackTrace();
            }
        });
    }

    private void getTopViewsKGs(){
        AppController.getApi().getTopViewedKGs(AppController.getInstance().getSessionId(),new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> vms, Response response) {
                topViewedKGs.addAll(vms);
                topViewedKGListAdapter = new TopViewedKGListAdapter(TopSchoolsActivity.this,topViewedKGs);
                topViewedList.setAdapter(topViewedKGListAdapter);
                scrollView.fullScroll(View.FOCUS_UP);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getTopBookmarkKGs(){
        AppController.getApi().getTopBookmarkedKGs(AppController.getInstance().getSessionId(),new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> vms, Response response) {
                topBookmarkedKGs.addAll(vms);
                topBookmarkedKGListAdapter = new TopBookmarkedKGListAdapter(TopSchoolsActivity.this,topBookmarkedKGs);
                topBookmarkedList.setAdapter(topBookmarkedKGListAdapter);
                scrollView.fullScroll(View.FOCUS_UP);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}




