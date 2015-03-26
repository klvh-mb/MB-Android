package miniBean.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.NewPost;
import miniBean.viewmodel.PostResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class NewPostActivity extends FragmentActivity {
    private ImageView backImage, image, postImage;
    private Boolean isPhoto = false;
    private final Integer SELECT_PICTURE = 1;
    private TextView postTitle,postContent,post;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_post);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.new_post_actionbar);

        backImage= (ImageView) findViewById(R.id.backImage);
        post= (TextView) findViewById(R.id.titlePost);
        postImage= (ImageView) findViewById(R.id.browseImage);
        image= (ImageView) findViewById(R.id.image);
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

        postImage.setOnClickListener(new View.OnClickListener() {
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
                String content=postContent.getText().toString();

                if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(content)) {
                    doPost(title, content);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE) {
            selectedImageUri = data.getData();
            selectedImagePath = getPath(selectedImageUri);
            selectedImageUri.getPath();
            image.setImageURI(selectedImageUri);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void doPost(String postString,String postContent) {
        System.out.println("dopost id::"+Long.parseLong(getIntent().getStringExtra("id")));

        AppController.api.setQuestion(new NewPost(Long.parseLong(getIntent().getStringExtra("id")),postString,postContent,isPhoto),AppController.getInstance().getSessionId(), new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                if (isPhoto) {
                    uploadPhoto(postResponse.getId());
                }
                onBackPressed();
                Toast.makeText(NewPostActivity.this, "Post Successful...!!!", Toast.LENGTH_SHORT).show();
              /*  Intent intent = new Intent(PostActivity.this,CommunityActivity.class);
                intent.putExtra("id",getIntent().getLongExtra("id",0l));
                intent.putExtra("commName",getIntent().getStringExtra("commName"));
                intent.putExtra("flag","FromPostFragment");
                startActivity(intent);*/

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(NewPostActivity.this, "Post Failure...!!!", Toast.LENGTH_SHORT).show();

                error.printStackTrace();
            }
        });
    }

    public void uploadPhoto(String postId) {
        File photo = new File(getRealPathFromUri(NewPostActivity.this, selectedImageUri));
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
