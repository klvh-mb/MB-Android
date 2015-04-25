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
import miniBean.fragement.SchoolCommunityFragment;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class KindyCommunityActivity extends FragmentActivity {
    private ImageView bookmarkAction,editAction;
    private Boolean isBookmarked;
    private PreNurseryVM nurseryVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.community_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.pn_community_actionbar);

        bookmarkAction = (ImageView) findViewById(R.id.bookmarkAction);
        editAction= (ImageView) findViewById(R.id.editAction);


        nurseryVM=new PreNurseryVM();


        Bundle bundle = new Bundle();


        bundle.putLong("commid", getIntent().getLongExtra("commid", 0l));
        bundle.putLong("id", getIntent().getLongExtra("id", 0l));


        SchoolCommunityFragment fragment = new SchoolCommunityFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.children_layout, fragment).commit();

        getPnInfo(getIntent().getLongExtra("id", 0l));

        bookmarkAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBookmarked) {
                    setBookmark(nurseryVM.getId());
                    bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
                    isBookmarked=true;
                }else {
                    setUnBookmark(nurseryVM.getId());
                    bookmarkAction.setImageResource(R.drawable.ic_bookmark);
                    isBookmarked=false;;
                }
            }
        });


        editAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KindyCommunityActivity.this,NewPostActivity.class);
                intent.putExtra("id",nurseryVM.getCommId().toString());
                intent.putExtra("flag","fromschool");
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
                System.out.println("unbook url::::"+response2.getUrl());
                bookmarkAction.setImageResource(R.drawable.ic_bookmark);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getPnInfo(Long id) {
        System.out.println("iddd::::::"+id);
        AppController.getApi().getPnInfo(id, AppController.getInstance().getSessionId(), new Callback<PreNurseryVM>() {

            @Override
            public void success(PreNurseryVM preNurseryVM, Response response) {
                nurseryVM=preNurseryVM;
                isBookmarked=nurseryVM.isBookmarked();
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
}