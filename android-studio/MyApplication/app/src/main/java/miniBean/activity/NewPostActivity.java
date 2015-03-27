package miniBean.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.ActivityUtil;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.NewPost;
import miniBean.viewmodel.PostResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class NewPostActivity extends FragmentActivity {
    private ImageView backImage, postImage, browseImage;
    private Boolean isPhoto = false;
    private final Integer SELECT_PICTURE = 1;
    private TextView postTitle,postContent,post;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_post);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.new_post_actionbar);

        backImage= (ImageView) findViewById(R.id.backImage);
        post = (TextView) findViewById(R.id.titlePost);
        browseImage = (ImageView) findViewById(R.id.browseImage);
        postImage = (ImageView) findViewById(R.id.postImage);
        postTitle= (TextView) findViewById(R.id.postTitle);
        postContent= (TextView) findViewById(R.id.postContent);

        backImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
                builder.setMessage("Do you Want Leave The Post Window..?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                    onBackPressed();
                             /*   Intent intent=new Intent(PostActivity.this,CommunityActivity.class);
                                intent.putExtra("id",getArguments().getString("id"));
                                intent.putExtra("commName",getArguments().getString("commName"));
                                intent.putExtra("flag","FromPostFragment");
                                startActivity(intent);*/

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

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
                String title = postTitle.getText().toString();
                String content = postContent.getText().toString();

                if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(content)) {
                    doPost(title, content);
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

    public void doPost(String title, String content) {
        Log.d(this.getClass().getSimpleName(), "doPost: userId=" + AppController.getUser().getId() + " title=" + title);
        AppController.api.setQuestion(new NewPost(Long.parseLong(getIntent().getStringExtra("id")), title, content, isPhoto), AppController.getInstance().getSessionId(), new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
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

    public void uploadPhoto(String postId) {
        File photo = new File(ImageUtil.getRealPathFromUri(this, selectedImageUri));
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);
        AppController.api.uploadPostPhoto(postId, typedFile, new Callback<Response>() {
            @Override
            public void success(Response array, retrofit.client.Response response) {
                System.out.println("Response:::::::" + array);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}
