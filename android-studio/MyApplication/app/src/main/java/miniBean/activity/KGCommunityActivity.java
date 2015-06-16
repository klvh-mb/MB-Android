package miniBean.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.TrackedFragmentActivity;
import miniBean.fragment.KGCommunityFragment;
import miniBean.util.SharingUtil;
import miniBean.viewmodel.KindergartenVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class KGCommunityActivity extends TrackedFragmentActivity {
    private ImageView whatsappAction,bookmarkAction,newPostAction,backAction;
    private Boolean isBookmarked;
    private KindergartenVM schoolVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.community_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.kg_community_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        whatsappAction = (ImageView) findViewById(R.id.whatsappAction);
        bookmarkAction = (ImageView) findViewById(R.id.bookmarkAction);
        newPostAction = (ImageView) findViewById(R.id.newPostIcon);
        backAction = (ImageView) findViewById(R.id.backImage);

        getSchoolInfo(getIntent().getLongExtra("id", 0l));

        // actionbar actions...
        whatsappAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingUtil.shareToWhatapp(schoolVM, KGCommunityActivity.this);
            }
        });

        bookmarkAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBookmarked) {
                    bookmark(schoolVM.getId());
                    bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
                    isBookmarked=true;
                }else {
                    unbookmark(schoolVM.getId());
                    bookmarkAction.setImageResource(R.drawable.ic_bookmark);
                    isBookmarked=false;
                }
            }
        });

        backAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        newPostAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KGCommunityActivity.this,NewKGPostActivity.class);
                intent.putExtra("id",schoolVM.getCommId());
                intent.putExtra("flag","FromKG");
                startActivity(intent);
            }
        });
    }

    private void bookmark(Long id) {
        AppController.getApi().bookmarkKG(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void unbookmark(Long id) {
        AppController.getApi().unbookmarkKG(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bookmarkAction.setImageResource(R.drawable.ic_bookmark);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getSchoolInfo(Long id) {
        AppController.getApi().getKGInfo(id, AppController.getInstance().getSessionId(), new Callback<KindergartenVM>() {
            @Override
            public void success(KindergartenVM vm, Response response) {
                schoolVM = vm;
                isBookmarked = schoolVM.isBookmarked();
                if (isBookmarked) {
                    bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
                } else {
                    bookmarkAction.setImageResource(R.drawable.ic_bookmark);
                }

                initFragment();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void initFragment() {
        Bundle bundle = new Bundle();
        bundle.putLong("commId", getIntent().getLongExtra("commId", 0L));
        bundle.putLong("id", getIntent().getLongExtra("id", 0L));
        bundle.putString("flag", getIntent().getStringExtra("flag"));

        KGCommunityFragment fragment = new KGCommunityFragment();
        fragment.setSchool(schoolVM);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.children_layout, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}