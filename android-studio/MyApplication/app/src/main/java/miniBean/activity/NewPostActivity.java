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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.PopupMyCommunityListAdapter;
import miniBean.app.AppController;
import miniBean.app.LocalCommunityTabCache;
import miniBean.util.ActivityUtil;
import miniBean.util.CommunityIconUtil;
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
    private LinearLayout selectCommunityLayout;
    private TextView selectCommunityText;
    private ImageView selectCommunityIcon;
    private TextView communityName;
    private ImageView communityIcon;
    private ImageView backImage, postImage, browseImage;
    private TextView postTitle, postContent, post;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;

    private List<File> photos = new ArrayList<>();
    private List<ImageView> postImages = new ArrayList<>();

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

        backImage = (ImageView) findViewById(R.id.backImage);
        post = (TextView) findViewById(R.id.titlePost);
        communityLayout = (RelativeLayout) findViewById(R.id.communityLayout);
        selectCommunityLayout = (LinearLayout) findViewById(R.id.selectCommunityLayout);
        selectCommunityText = (TextView) findViewById(R.id.selectCommunityText);
        selectCommunityIcon = (ImageView) findViewById(R.id.selectCommunityIcon);
        communityIcon = (ImageView) findViewById(R.id.commIcon);
        communityName = (TextView) findViewById(R.id.communityName);
        browseImage = (ImageView) findViewById(R.id.browseImage);
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

        updateSelectCommunityLayout();
        selectCommunityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateMyCommunityPopup();
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        browseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtil.openPhotoPicker(NewPostActivity.this);
            }
        });

        if (postImages.size() == 0) {
            postImages.add((ImageView) findViewById(R.id.postImage1));
            postImages.add((ImageView) findViewById(R.id.postImage2));
            postImages.add((ImageView) findViewById(R.id.postImage3));

            for (ImageView postImage : postImages) {
                postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removePostImage();
                    }
                });
            }
        }

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPost();
            }
        });
    }

    private void updateSelectCommunityLayout() {
        if (communityId == null) {
            selectCommunityText.setVisibility(View.VISIBLE);
            selectCommunityIcon.setVisibility(View.VISIBLE);
            communityIcon.setVisibility(View.GONE);
            communityName.setVisibility(View.GONE);
        } else {
            selectCommunityText.setVisibility(View.GONE);
            selectCommunityIcon.setVisibility(View.GONE);
            communityIcon.setVisibility(View.VISIBLE);
            communityName.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageUtil.SELECT_PICTURE && resultCode == RESULT_OK &&
                data != null && photos.size() < DefaultValues.MAX_POST_IMAGES) {

            selectedImageUri = data.getData();
            selectedImagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);

            String path = selectedImageUri.getPath();
            Log.d(this.getClass().getSimpleName(), "onActivityResult: selectedImageUri=" + path + " selectedImagePath=" + selectedImagePath);

            Bitmap bitmap = ImageUtil.resizeAsPreviewThumbnail(selectedImagePath);
            if (bitmap != null) {
                setPostImage(bitmap);
            } else {
                Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.photo_not_found), Toast.LENGTH_SHORT).show();
        }

        // pop back soft keyboard
        activityUtil.popupInputMethodWindow();
    }

    private void setPostImage(Bitmap bp){
        ImageView postImage = postImages.get(photos.size());
        postImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
        postImage.setVisibility(View.VISIBLE);
        File photo = new File(ImageUtil.getRealPathFromUri(this, selectedImageUri));
        photos.add(photo);
    }

    private void removePostImage(){
        if (photos.size() > 0) {
            int toRemove = photos.size()-1;
            postImages.get(toRemove).setImageDrawable(null);
            photos.remove(toRemove);
        }
    }

    private void initiateMyCommunityPopup() {
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
            adapter = new PopupMyCommunityListAdapter(this, LocalCommunityTabCache.getMyCommunities().communities);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CommunitiesWidgetChildVM community = adapter.getItem(position);
                    communityId = community.getId();

                    communityName.setText(community.getDn());
                    int iconMapped = CommunityIconUtil.map(community.getGi());
                    if (iconMapped != -1) {
                        //Log.d(this.getClass().getSimpleName(), "getQnaDetail: replace source with local comm icon - " + commIcon);
                        communityIcon.setImageDrawable(getResources().getDrawable(iconMapped));
                    } else {
                        Log.d(this.getClass().getSimpleName(), "getQnaDetail: load comm icon from background - " + community.getGi());
                        ImageUtil.displayRoundedCornersImage(community.getGi(), communityIcon);
                    }

                    updateSelectCommunityLayout();
                    myCommunityPopup.dismiss();
                    Log.d(this.getClass().getSimpleName(), "listView.onItemClick: community="+community.getId()+"|"+community.getDn());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPost() {
        String title = postTitle.getText().toString();
        String content = postContent.getText().toString();

        if (StringUtils.isEmpty(title)) {
            Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.invalid_post_title_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtils.isEmpty(content)) {
            Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.invalid_post_body_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (communityId == null) {
            initiateMyCommunityPopup();
            return;
        }

        final boolean withPhotos = photos.size() > 0;

        Log.d(this.getClass().getSimpleName(), "doPost: communityId=" + communityId + " title=" + title);
        AppController.getApi().setQuestion(new NewPost(communityId, title, content, withPhotos), AppController.getInstance().getSessionId(), new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                postSuccess = true;

                if (withPhotos) {
                    uploadPhotos(postResponse.getId());
                }

                onBackPressed();
                finish();
                Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.new_post_success), Toast.LENGTH_LONG).show();

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

    private void uploadPhotos(String postId) {
        for (File photo : photos) {
            TypedFile typedFile = new TypedFile("application/octet-stream", photo);
            AppController.getApi().uploadPostPhoto(postId, typedFile, new Callback<Response>() {
                @Override
                public void success(Response array, retrofit.client.Response response) {

                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    retrofitError.printStackTrace(); //to see if you have errors
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        String title = postTitle.getText().toString();
        String content = postContent.getText().toString();

        if (postSuccess ||
                (StringUtils.isEmpty(title) && StringUtils.isEmpty(content))) {
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
