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
import miniBean.fragment.SchoolCommunityFragment;
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
        editAction = (ImageView) findViewById(R.id.editAction);
        backAction = (ImageView) findViewById(R.id.backImage);

        Bundle bundle = new Bundle();

        bundle.putLong("commId", getIntent().getLongExtra("commId", 0l));
        bundle.putLong("id", getIntent().getLongExtra("id", 0l));
        bundle.putString("flag",getIntent().getStringExtra("flag"));

        SchoolCommunityFragment fragment = new SchoolCommunityFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.children_layout, fragment).commit();

        getPNInfo(getIntent().getLongExtra("id", 0l));

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
                intent.putExtra("id",schoolVM.getCommId().toString());
                intent.putExtra("flag","FromSchool");
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

    private void getPNInfo(Long id) {
        AppController.getApi().getPNInfo(id, AppController.getInstance().getSessionId(), new Callback<PreNurseryVM>() {
            @Override
            public void success(PreNurseryVM vm, Response response) {
                schoolVM = vm;
                isBookmarked = schoolVM.isBookmarked();
                if(isBookmarked){
                    bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
                }else{
                    bookmarkAction.setImageResource(R.drawable.ic_bookmark);
                }
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