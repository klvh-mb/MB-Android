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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
import miniBean.adapter.EmoticonListAdapter;
import miniBean.adapter.PopupMyCommunityListAdapter;
import miniBean.app.AppController;
import miniBean.app.EmoticonCache;
import miniBean.app.LocalCommunityTabCache;
import miniBean.app.TrackedFragmentActivity;
import miniBean.util.CommunityIconUtil;
import miniBean.util.DefaultValues;
import miniBean.util.EmoticonUtil;
import miniBean.util.ImageUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.EmoticonVM;
import miniBean.viewmodel.NewPost;
import miniBean.viewmodel.PostResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class NewPostActivity extends TrackedFragmentActivity {

    protected RelativeLayout communityLayout;
    protected LinearLayout selectCommunityLayout;
    protected TextView selectCommunityText;
    protected ImageView selectCommunityIcon;
    protected TextView communityName;
    protected ImageView communityIcon;
    protected ImageView backImage, browseImage, emoImage;
    protected TextView postTitle, postContent, postAction, editTextInFocus;

    protected String selectedImagePath = null;
    protected Uri selectedImageUri = null;

    protected List<File> photos = new ArrayList<>();
    protected List<ImageView> postImages = new ArrayList<>();

    protected List<EmoticonVM> emoticonVMList = new ArrayList<>();
    protected EmoticonListAdapter emoticonListAdapter;

    protected Long communityId;
    protected PopupWindow myCommunityPopup, emoPopup;
    protected PopupMyCommunityListAdapter adapter;

    protected boolean postSuccess = false;
    protected int imageUploadSuccessCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_post_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.new_post_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_purple));

        backImage = (ImageView) findViewById(R.id.backImage);
        postAction = (TextView) findViewById(R.id.postAction);
        communityLayout = (RelativeLayout) findViewById(R.id.commLayout1);
        selectCommunityLayout = (LinearLayout) findViewById(R.id.selectCommunityLayout);
        selectCommunityText = (TextView) findViewById(R.id.selectCommunityText);
        selectCommunityIcon = (ImageView) findViewById(R.id.selectCommunityIcon);
        communityIcon = (ImageView) findViewById(R.id.commIcon);
        communityName = (TextView) findViewById(R.id.communityName);
        browseImage = (ImageView) findViewById(R.id.browseImage);
        emoImage = (ImageView) findViewById(R.id.emoImage);
        postTitle = (TextView) findViewById(R.id.postTitle);
        postContent = (TextView) findViewById(R.id.postContent);
        editTextInFocus = postContent;

        postTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextInFocus = postTitle;
            }
        });
        postContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextInFocus = postContent;
            }
        });

        Long commId = getIntent().getLongExtra("id",0L);
        if (commId == 0L) {
            communityId = null;
            communityLayout.setVisibility(View.VISIBLE);
        } else {
            communityId = commId;
            communityLayout.setVisibility(View.GONE);
        }
        Log.d(this.getClass().getSimpleName(), "onCreate: communityId=" + commId);

        updateSelectCommunityLayout();
        selectCommunityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMyCommunityPopup();
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

        emoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initEmoticonPopup();
            }
        });

        if (emoticonVMList.isEmpty() && EmoticonCache.getEmoticons().isEmpty()) {
            EmoticonCache.refresh();
        }

        postAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPost();
            }
        });
    }

    protected void updateSelectCommunityLayout() {
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
        ViewUtil.popupInputMethodWindow(this);
    }

    protected void setPostImage(Bitmap bp){
        ImageView postImage = postImages.get(photos.size());
        postImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
        postImage.setVisibility(View.VISIBLE);
        File photo = new File(ImageUtil.getRealPathFromUri(this, selectedImageUri));
        photos.add(photo);
    }

    protected void removePostImage(){
        if (photos.size() > 0) {
            int toRemove = photos.size()-1;
            postImages.get(toRemove).setImageDrawable(null);
            photos.remove(toRemove);
        }
    }

    /**
     * To be overriden by child new post activity.
     * @return
     */
    protected List<CommunitiesWidgetChildVM> getMyCommunities() {
        return LocalCommunityTabCache.getMyCommunities().communities;
    }

    protected void doPost() {
        String title = postTitle.getText().toString().trim();
        String content = postContent.getText().toString().trim();

        if (StringUtils.isEmpty(title)) {
            Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.invalid_post_title_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtils.isEmpty(content)) {
            Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.invalid_post_body_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (communityId == null) {
            initMyCommunityPopup();
            return;
        }

        ViewUtil.showSpinner(this);

        final boolean withPhotos = photos.size() > 0;

        Log.d(this.getClass().getSimpleName(), "doPost: communityId=" + communityId + " title=" + title);
        AppController.getApi().newCommunityPost(new NewPost(communityId, title, content, withPhotos), AppController.getInstance().getSessionId(), new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                postSuccess = true;

                if (withPhotos) {
                    uploadPhotos(postResponse.getId());
                } else {
                    complete();
                }

                /*Intent intent = new Intent(PostActivity.this,CommunityActivity.class);
                intent.putExtra("id",getIntent().getLongExtra("id",0l));
                intent.putExtra("flag","FromPostFragment");
                startActivity(intent);*/
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(NewPostActivity.this);
                Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.new_post_failed), Toast.LENGTH_SHORT).show();
                Log.e(NewPostActivity.class.getSimpleName(), "doPost: failure", error);
            }
        });
    }

    protected void uploadPhotos(String postId) {
        for (File photo : photos) {
            photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
            TypedFile typedFile = new TypedFile("application/octet-stream", photo);
            AppController.getApi().uploadPostPhoto(postId, typedFile, new Callback<Response>() {
                @Override
                public void success(Response array, retrofit.client.Response response) {
                    imageUploadSuccessCount++;
                    if (imageUploadSuccessCount >= photos.size()) {
                        imageUploadSuccessCount = 0;
                        complete();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    ViewUtil.stopSpinner(NewPostActivity.this);
                    Log.e(NewPostActivity.class.getSimpleName(), "uploadPhotos: failure", error);
                }
            });
        }
    }

    protected void complete() {
        ViewUtil.stopSpinner(this);
        onBackPressed();
        finish();
        Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.new_post_success), Toast.LENGTH_LONG).show();
    }

    protected void initMyCommunityPopup() {
        try {
            LayoutInflater inflater = (LayoutInflater) NewPostActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.my_community_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            if (myCommunityPopup == null) {
                myCommunityPopup = new PopupWindow(
                        layout,
                        ViewUtil.getRealDimension(DefaultValues.MY_COMMUNITY_POPUP_WIDTH, this.getResources()),
                        ViewUtil.getRealDimension(DefaultValues.MY_COMMUNITY_POPUP_HEIGHT, this.getResources()),
                        true);
            }

            myCommunityPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            myCommunityPopup.setOutsideTouchable(false);
            myCommunityPopup.setFocusable(true);
            myCommunityPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ListView listView = (ListView) layout.findViewById(R.id.communityList);
            adapter = new PopupMyCommunityListAdapter(this, getMyCommunities());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CommunitiesWidgetChildVM community = adapter.getItem(position);
                    communityId = community.getId();

                    communityName.setText(community.getDn());
                    int iconMapped = CommunityIconUtil.map(community.getGi());
                    if (iconMapped != -1) {
                        //Log.d(this.getClass().getSimpleName(), "initMyCommunityPopup: replace source with local comm icon - " + commIcon);
                        communityIcon.setImageDrawable(getResources().getDrawable(iconMapped));
                    } else {
                        Log.d(this.getClass().getSimpleName(), "initMyCommunityPopup: load comm icon from background - " + community.getGi());
                        ImageUtil.displayRoundedCornersImage(community.getGi(), communityIcon);
                    }

                    updateSelectCommunityLayout();
                    myCommunityPopup.dismiss();
                    myCommunityPopup = null;
                    Log.d(this.getClass().getSimpleName(), "initMyCommunityPopup: listView.onItemClick: community="+community.getId()+"|"+community.getDn());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initEmoticonPopup() {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) NewPostActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.emoticon_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            // hide soft keyboard when select emoticon
            ViewUtil.hideInputMethodWindow(this, layout);

            if (emoPopup == null) {
                emoPopup = new PopupWindow(layout,
                        ViewUtil.getRealDimension(DefaultValues.EMOTICON_POPUP_WIDTH, this.getResources()),
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true);
            }

            emoPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            emoPopup.setOutsideTouchable(false);
            emoPopup.setFocusable(true);
            emoPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            if (emoticonVMList.isEmpty()) {
                emoticonVMList = EmoticonCache.getEmoticons();
            }
            emoticonListAdapter = new EmoticonListAdapter(this,emoticonVMList);

            GridView gridView = (GridView) layout.findViewById(R.id.emoGrid);
            gridView.setAdapter(emoticonListAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    EmoticonUtil.insertEmoticon(emoticonVMList.get(i), editTextInFocus);
                    emoPopup.dismiss();
                    emoPopup = null;
                    ViewUtil.popupInputMethodWindow(NewPostActivity.this);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        String title = postTitle.getText().toString();
        String content = postContent.getText().toString();

        if (postSuccess ||
                (StringUtils.isEmpty(title) && StringUtils.isEmpty(content))) {
            super.onBackPressed();
            if (myCommunityPopup != null) {
                myCommunityPopup.dismiss();
                myCommunityPopup = null;
            }
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
    }
}
