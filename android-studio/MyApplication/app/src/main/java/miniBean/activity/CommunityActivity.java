package miniBean.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.FeedListAdapter;
import miniBean.app.AppController;
import miniBean.app.LocalCache;
import miniBean.app.MyApi;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class CommunityActivity extends FragmentActivity {
    public SharedPreferences session = null;
    public MyApi api;
    TextView noPost, noMember, commName;
    ListView listView;
    FeedListAdapter feedListAdapter;
    List<CommunityPostVM> feedItems;
    ProgressBar progressBar, spinner;
    List<CommunityCategoryMapVM> item;
    List<CommunitiesWidgetChildVM> commItem;
    ImageView imageView, backImage;
    CommunitiesWidgetChildVM currentCommunity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();
        session = getSharedPreferences("prefs", 0);
        setContentView(R.layout.community_activity);
        api = restAdapter.create(MyApi.class);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.community_actionbar);
        backImage = (ImageView) findViewById(R.id.backImage);
        spinner = (ProgressBar) findViewById(R.id.loadCover);
        progressBar = (ProgressBar) findViewById(R.id.progressCommunity);
        progressBar.setVisibility(View.VISIBLE);
        noPost = (TextView) findViewById(R.id.noPostComm);
        noMember = (TextView) findViewById(R.id.noMemberComm);
        commName = (TextView) findViewById(R.id.commNameText);
        imageView = (ImageView) findViewById(R.id.join_community);
        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new FeedListAdapter(this, feedItems);
        listView = (ListView) findViewById(R.id.listCommunityFeed);
        listView.setAdapter(feedListAdapter);

        for (CommunityCategoryMapVM categoryMapVM : LocalCache.categoryMapList) {
            if (categoryMapVM.communities != null)
                for (CommunitiesWidgetChildVM vm : categoryMapVM.communities) {
                    if (vm.getId() == Long.parseLong(getIntent().getStringExtra("id"))) {
                        currentCommunity = vm;
                    }
                }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                CommunityPostVM post = feedListAdapter.getItem(position);
                intent.putExtra("postId", post.getId());
                intent.putExtra("commId", post.getCid());
                startActivity(intent);
            }
        });
        getNewsFeedByCommuityId(Long.parseLong(getIntent().getStringExtra("id")));
        System.out.println("::::::::::::::::::::::::::::::::::::: boolean  " + getIntent().getBooleanExtra("isM", false));
        if (getIntent().getBooleanExtra("isM", false))
            imageView.setImageResource(R.drawable.add);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new FeedListAdapter(this, feedItems);
        listView = (ListView) findViewById(R.id.listCommunityFeed);
        listView.setAdapter(feedListAdapter);

        getNewsFeedByCommuityId(Long.parseLong(getIntent().getStringExtra("id")));
        if (!getIntent().getBooleanExtra("isM", false)) {
            imageView.setImageResource(R.drawable.check);
        } else {
            imageView.setImageResource(R.drawable.add);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!getIntent().getBooleanExtra("isM", false)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CommunityActivity.this);
                    alertDialogBuilder.setMessage("Are You Want Join This Community?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendJoinRequest(Long.parseLong(getIntent().getStringExtra("id")));
                            Toast.makeText(getApplicationContext(), "Community Joined", Toast.LENGTH_LONG).show();
                            ImageView image = (ImageView) findViewById(R.id.join_community);
                            image.setImageResource(R.drawable.add);
                            currentCommunity.setIsM(true);

                        }
                    });
                    alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(CommunityActivity.this, "CANCEL", Toast.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CommunityActivity.this);
                    alertDialogBuilder.setMessage("Are You Want To Leave This Community?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            leaveCommunity(Long.parseLong(getIntent().getStringExtra("id")));
                            Toast.makeText(getApplicationContext(), "Community left", Toast.LENGTH_LONG).show();
                            ImageView image = (ImageView) findViewById(R.id.join_community);
                            image.setImageResource(R.drawable.check);
                            currentCommunity.setIsM(false);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(CommunityActivity.this, "CANCEL", Toast.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
    }

    private void getNewsFeedByCommuityId(long id) {
        api.getCommNewsfeed(id, session.getString("sessionID", null), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, retrofit.client.Response response) {
                feedItems.addAll(array.getPosts());
                feedListAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                commName.setText(getIntent().getStringExtra("commName"));
                noMember.setText(getIntent().getStringExtra("noMember"));
                noPost.setText(getIntent().getStringExtra("noPost"));
                ImageView communityCoverPic = (ImageView) findViewById(R.id.communityPic);
                ImageView communityIcon = (ImageView) findViewById(R.id.commIconView);
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(getResources().getString(R.string.base_url) + "/image/get-cover-community-image-by-id/" + getIntent().getStringExtra("id"), communityCoverPic, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        spinner.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        spinner.setVisibility(View.GONE);
                    }
                });
                AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + getIntent().getStringExtra("icon"), communityIcon);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void sendJoinRequest(Long id) {
        AppController.api.sendJoinRequest(id, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void leaveCommunity(Long id) {
        AppController.api.sendLeaveRequest(id, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}