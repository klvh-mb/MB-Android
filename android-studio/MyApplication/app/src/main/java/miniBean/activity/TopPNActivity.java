package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.TopBookmarkKGListAdapter;
import miniBean.adapter.TopBookmarkPNListAdapter;
import miniBean.adapter.TopPNListAdapter;
import miniBean.adapter.TopViewKGsListAdapter;
import miniBean.app.AppController;
import miniBean.viewmodel.KindergartenVM;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopPNActivity extends FragmentActivity {
    private ListView topPNList,topBookmarkList;
    private TopPNListAdapter topPNListAdapter;
    private TopBookmarkPNListAdapter topBookmarkPNListAdapter;
    private TopViewKGsListAdapter topViewKGsListAdapter;
    private TopBookmarkKGListAdapter topBookmarkKGListAdapter;
    private List<PreNurseryVM> preNurseryVMList,nurseryVMs;
    private List<KindergartenVM> kindergartenVMList,kindergartenVM;
    private ImageView backAction,scrollButton;
    private ScrollView scrollView;
    private boolean scrollUp=true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.top_pn_activity);


        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        if(getIntent().getStringExtra("flag").equals("SchoolsPNFragment")){
            getActionBar().setCustomView(R.layout.top_pn_actionbar);
        }else {
            getActionBar().setCustomView(R.layout.top_kg_actionbar);
        }

        preNurseryVMList=new ArrayList<PreNurseryVM>();
        nurseryVMs=new ArrayList<PreNurseryVM>();
        kindergartenVMList=new ArrayList<KindergartenVM>();
        kindergartenVM=new ArrayList<KindergartenVM>();

        topPNList= (ListView) findViewById(R.id.listTopPN);
        topBookmarkList= (ListView) findViewById(R.id.listTopBookmarkedPN);
        backAction= (ImageView) findViewById(R.id.backImage);
        scrollButton= (ImageView) findViewById(R.id.scrollButton);
        scrollView= (ScrollView) findViewById(R.id.scrollview);

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
            public void success(List<PreNurseryVM> preNurseryVMs, Response response) {
                System.out.println("size:::::::1::"+preNurseryVMs.size());
                preNurseryVMList.addAll(preNurseryVMs);
                topPNListAdapter=new TopPNListAdapter(TopPNActivity.this,preNurseryVMList);
                topPNList.setAdapter(topPNListAdapter);
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
            public void success(List<PreNurseryVM> preNurseryVMs, Response response) {
                System.out.println("size:::::::2::"+preNurseryVMs.size());
                nurseryVMs.addAll(preNurseryVMs);
                topBookmarkPNListAdapter=new TopBookmarkPNListAdapter(TopPNActivity.this,nurseryVMs);
                topBookmarkList.setAdapter(topBookmarkPNListAdapter);
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
            public void success(List<KindergartenVM> kindergartenVMs, Response response) {
                System.out.println("size:::::::1::" + kindergartenVMs.size());
                kindergartenVMList.addAll(kindergartenVMs);
                topViewKGsListAdapter=new TopViewKGsListAdapter(TopPNActivity.this,kindergartenVMList);
                topPNList.setAdapter(topViewKGsListAdapter);
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
            public void success(List<KindergartenVM> kindergartenVMs, Response response) {
               kindergartenVM.addAll(kindergartenVMs);
                topBookmarkKGListAdapter=new TopBookmarkKGListAdapter(TopPNActivity.this,kindergartenVM);
                topBookmarkList.setAdapter(topBookmarkKGListAdapter);
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




