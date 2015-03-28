package miniBean.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;

import miniBean.R;
import miniBean.adapter.PopupMyCommunityListAdapter;
import miniBean.app.AppController;
import miniBean.app.LocalCache;
import miniBean.util.ActivityUtil;
import miniBean.util.DefaultValues;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.NewPost;
import miniBean.viewmodel.PostResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class NewPostActivity extends FragmentActivity {
    private RelativeLayout communityLayout;
    private TextView communityName;
    private ImageView communityIcon;
    private ImageView backImage, postImage, browseImage;
    private Boolean isPhoto = false;
    private final Integer SELECT_PICTURE = 1;
    private TextView postTitle, postContent, post;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;

    private Long communityId;
    private PopupWindow myCommunityPopup;
    private PopupMyCommunityListAdapter adapter;

    private boolean postSuccess = false;

    private ActivityUtil activityUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_post);

        activityUtil = new ActivityUtil(this);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.new_post_actionbar);

        // init my comms if not yet...
        if (LocalCache.getMyCommunitiesParentVM() == null)
            LocalCache.refreshMyCommunities();

        backImage = (ImageView) findViewById(R.id.backImage);
        post = (TextView) findViewById(R.id.titlePost);
        communityLayout = (RelativeLayout) findViewById(R.id.communityLayout);
        communityIcon = (ImageView) findViewById(R.id.commIcon);
        communityName = (TextView) findViewById(R.id.communityName);
        browseImage = (ImageView) findViewById(R.id.browseImage);
        postImage = (ImageView) findViewById(R.id.postImage);
        postTitle = (TextView) findViewById(R.id.postTitle);
        postContent = (TextView) findViewById(R.id.postContent);

        if (StringUtils.isEmpty(getIntent().getStringExtra("id"))) {
            communityId = null;
            communityLayout.setVisibility(View.VISIBLE);
        } else {
            communityId = Long.parseLong(getIntent().getStringExtra("id"));
            communityLayout.setVisibility(View.GONE);
        }
        Log.d(this.getClass().getSimpleName(), "onCreate: communityId="+communityId);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        browseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                isPhoto = true;
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communityId == null) {
                    initiateMyCommunityPopup();
                } else {
                    doPost();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE) {
            if (data == null)
                return;

            selectedImageUri = data.getData();
            selectedImagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);

            String path = selectedImageUri.getPath();
            Log.d(this.getClass().getSimpleName(), "onActivityResult: selectedImageUri="+path+" selectedImagePath="+selectedImagePath);
            Bitmap bp = ImageUtil.resizeAsPreviewThumbnail(selectedImagePath);
            if (bp != null) {
                postImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
                postImage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initiateMyCommunityPopup() {
        // should never be the case...
        if (LocalCache.getMyCommunitiesParentVM() == null)
            return;

        try {
            LayoutInflater inflater = (LayoutInflater) NewPostActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.my_community_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            myCommunityPopup = new PopupWindow(
                    layout,
                    activityUtil.getRealDimension(DefaultValues.MY_COMMUNITY_POPUP_WIDTH),
                    activityUtil.getRealDimension(DefaultValues.MY_COMMUNITY_POPUP_HEIGHT),
                    true);
            myCommunityPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            myCommunityPopup.setOutsideTouchable(false);
            myCommunityPopup.setFocusable(true);
            myCommunityPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ListView listView = (ListView) layout.findViewById(R.id.communityList);
            adapter = new PopupMyCommunityListAdapter(this, LocalCache.getMyCommunitiesParentVM().communities);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CommunitiesWidgetChildVM community = adapter.getItem(position);
                    communityId = community.getId();
                    Log.d(this.getClass().getSimpleName(), "listView.onItemClick: community="+community.getId()+"|"+community.getDn());
                    doPost();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPost() {
        String title = postTitle.getText().toString();
        String content = postContent.getText().toString();

        if (StringUtils.isEmpty(title) || StringUtils.isEmpty(content))
            return;

        // should never be the case...
        if (communityId == null)
            return;

        Log.d(this.getClass().getSimpleName(), "doPost: communityId=" + communityId + " title=" + title);
        AppController.api.setQuestion(new NewPost(communityId, title, content, isPhoto), AppController.getInstance().getSessionId(), new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                postSuccess = true;

                if (isPhoto) {
                    uploadPhoto(postResponse.getId());
                }
                onBackPressed();
                Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.new_post_success), Toast.LENGTH_SHORT).show();

                /*  Intent intent = new Intent(PostActivity.this,CommunityActivity.class);
                intent.putExtra("id",getIntent().getLongExtra("id",0l));
                intent.putExtra("commName",getIntent().getStringExtra("commName"));
                intent.putExtra("flag","FromPostFragment");
                startActivity(intent);*/
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.new_post_failed), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    private void uploadPhoto(String postId) {
        File photo = new File(ImageUtil.getRealPathFromUri(this, selectedImageUri));
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);
        AppController.api.uploadPostPhoto(postId, typedFile, new Callback<Response>() {
            @Override
            public void success(Response array, retrofit.client.Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    @Override
    public void onBackPressed() {
        String title = postTitle.getText().toString();
        String content = postContent.getText().toString();

        if (postSuccess ||
                (StringUtils.isEmpty(title) || StringUtils.isEmpty(content))) {
            super.onBackPressed();
            if (myCommunityPopup != null)
                myCommunityPopup.dismiss();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
        builder.setMessage(getString(R.string.cancel_new_post))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewPostActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        return;
    }
}
