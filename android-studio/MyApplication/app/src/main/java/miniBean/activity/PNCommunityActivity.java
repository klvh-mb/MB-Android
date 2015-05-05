package miniBean.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.fragment.PNCommunityFragment;
import miniBean.util.SharingUtil;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PNCommunityActivity extends FragmentActivity {
    private ImageView whatsappAction,bookmarkAction,editAction,backAction;
    private Boolean isBookmarked;
    private PreNurseryVM schoolVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.community_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.pn_community_actionbar);

        whatsappAction = (ImageView) findViewById(R.id.whatsappAction);
        bookmarkAction = (ImageView) findViewById(R.id.bookmarkAction);
        editAction = (ImageView) findViewById(R.id.newPostIcon);
        backAction = (ImageView) findViewById(R.id.backImage);

        getSchoolInfo(getIntent().getLongExtra("id", 0L));

        // actionbar actions...
        whatsappAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingUtil.shareToWhatapp(schoolVM, PNCommunityActivity.this);
            }
        });

        bookmarkAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBookmarked) {
                    setBookmark(schoolVM.getId());
                    bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
                    isBookmarked=true;
                }else {
                    setUnBookmark(schoolVM.getId());
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

        editAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PNCommunityActivity.this,NewPostActivity.class);
                intent.putExtra("id",schoolVM.getCommId());
                intent.putExtra("flag","FromPN");
                startActivity(intent);
            }
        });
    }

    private void setBookmark(Long id) {
        AppController.getApi().setPNBookmark(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
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

    private void setUnBookmark(Long id) {
        AppController.getApi().setPNUnBookmark(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
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
        AppController.getApi().getPNInfo(id, AppController.getInstance().getSessionId(), new Callback<PreNurseryVM>() {
            @Override
            public void success(PreNurseryVM vm, Response response) {
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

        PNCommunityFragment fragment = new PNCommunityFragment();
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