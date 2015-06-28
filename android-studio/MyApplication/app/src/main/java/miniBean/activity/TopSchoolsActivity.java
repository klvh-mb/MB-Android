package miniBean.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import miniBean.app.TrackedFragmentActivity;
import miniBean.fragment.SchoolsKGFragment;
import miniBean.fragment.SchoolsPNFragment;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.KindergartenVM;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopSchoolsActivity extends TrackedFragmentActivity {
    private ListView topViewedList,topBookmarkedList;
    private TopViewedPNListAdapter topViewedPNListAdapter;
    private TopBookmarkedPNListAdapter topBookmarkedPNListAdapter;
    private TopViewedKGListAdapter topViewedKGListAdapter;
    private TopBookmarkedKGListAdapter topBookmarkedKGListAdapter;
    private List<PreNurseryVM> topViewedPNs,topBookmarkedPNs;
    private List<KindergartenVM> topViewedKGs,topBookmarkedKGs;

    private ImageView backAction, scrollButton;
    private ScrollView scrollView;
    private boolean scrollUp = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.top_schools_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.top_schools_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        TextView actionbarTitle = (TextView) findViewById(R.id.title);
        RelativeLayout topViewedLayout = (RelativeLayout) findViewById(R.id.topViewedLayout);
        RelativeLayout topBookmarkedLayout = (RelativeLayout) findViewById(R.id.topBookmarkedLayout);

        if (getIntent().getStringExtra("flag").equals(SchoolsPNFragment.INTENT_FLAG)) {
            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_green));
            actionbarTitle.setText(getString(R.string.schools_top_pn_ranking));
            topViewedLayout.setBackgroundResource(R.drawable.schools_pn_edit_text_round);
            topBookmarkedLayout.setBackgroundResource(R.drawable.schools_pn_edit_text_round);
        } else if (getIntent().getStringExtra("flag").equals(SchoolsKGFragment.INTENT_FLAG)) {
            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_maroon));
            actionbarTitle.setText(getString(R.string.schools_top_kg_ranking));
            topViewedLayout.setBackgroundResource(R.drawable.schools_kg_edit_text_round);
            topBookmarkedLayout.setBackgroundResource(R.drawable.schools_kg_edit_text_round);
        }

        //topViewedLayout.setPadding(10,10,10,10);
        //topBookmarkedLayout.setPadding(10,10,10,10);

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

        if (getIntent().getStringExtra("flag").equals(SchoolsPNFragment.INTENT_FLAG)) {
            getTopViewPNs();
            getTopBookmarkPNs();
        } else if (getIntent().getStringExtra("flag").equals(SchoolsKGFragment.INTENT_FLAG)) {
            getTopViewsKGs();
            getTopBookmarkKGs();
        }

        topViewedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (getIntent().getStringExtra("flag").equals(SchoolsPNFragment.INTENT_FLAG)) {
                    PreNurseryVM vm = topViewedPNListAdapter.getItem(i);
                    startSchoolActivity(vm.getId(), vm.getCommId(), PNCommunityActivity.class);
                } else if (getIntent().getStringExtra("flag").equals(SchoolsKGFragment.INTENT_FLAG)) {
                    KindergartenVM vm = topViewedKGListAdapter.getItem(i);
                    startSchoolActivity(vm.getId(), vm.getCommId(), KGCommunityActivity.class);
                }
            }
        });

        topBookmarkedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (getIntent().getStringExtra("flag").equals(SchoolsPNFragment.INTENT_FLAG)) {
                    PreNurseryVM vm = topBookmarkedPNListAdapter.getItem(i);
                    startSchoolActivity(vm.getId(), vm.getCommId(), PNCommunityActivity.class);
                } else if (getIntent().getStringExtra("flag").equals(SchoolsKGFragment.INTENT_FLAG)) {
                    KindergartenVM vm = topBookmarkedKGListAdapter.getItem(i);
                    startSchoolActivity(vm.getId(), vm.getCommId(), KGCommunityActivity.class);
                }
            }
        });

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

    private void startSchoolActivity(Long id, Long commId, Class activityClass) {
        Intent intent = new Intent(TopSchoolsActivity.this, activityClass);
        intent.putExtra("commId", commId);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void getTopViewPNs(){
        AppController.getApi().getTopViewedPNs(AppController.getInstance().getSessionId(), new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> vms, Response response) {
                topViewedPNs.addAll(vms);
                topViewedPNListAdapter = new TopViewedPNListAdapter(TopSchoolsActivity.this, topViewedPNs);
                topViewedList.setAdapter(topViewedPNListAdapter);
                ViewUtil.setHeightBasedOnChildren(topViewedList);
                scrollView.fullScroll(View.FOCUS_UP);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getTopBookmarkPNs(){
        AppController.getApi().getTopBookmarkedPNs(AppController.getInstance().getSessionId(), new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> vms, Response response) {
                topBookmarkedPNs.addAll(vms);
                topBookmarkedPNListAdapter = new TopBookmarkedPNListAdapter(TopSchoolsActivity.this, topBookmarkedPNs);
                topBookmarkedList.setAdapter(topBookmarkedPNListAdapter);
                ViewUtil.setHeightBasedOnChildren(topBookmarkedList);
                scrollView.fullScroll(View.FOCUS_UP);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getTopViewsKGs(){
        AppController.getApi().getTopViewedKGs(AppController.getInstance().getSessionId(), new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> vms, Response response) {
                topViewedKGs.addAll(vms);
                topViewedKGListAdapter = new TopViewedKGListAdapter(TopSchoolsActivity.this, topViewedKGs);
                topViewedList.setAdapter(topViewedKGListAdapter);
                ViewUtil.setHeightBasedOnChildren(topViewedList);
                scrollView.fullScroll(View.FOCUS_UP);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getTopBookmarkKGs(){
        AppController.getApi().getTopBookmarkedKGs(AppController.getInstance().getSessionId(), new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> vms, Response response) {
                topBookmarkedKGs.addAll(vms);
                topBookmarkedKGListAdapter = new TopBookmarkedKGListAdapter(TopSchoolsActivity.this, topBookmarkedKGs);
                topBookmarkedList.setAdapter(topBookmarkedKGListAdapter);
                ViewUtil.setHeightBasedOnChildren(topBookmarkedList);
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




