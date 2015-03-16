package miniBean.fragement;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import miniBean.R;
import miniBean.activity.CommunityActivity;
import miniBean.app.AppController;
import miniBean.viewmodel.NewPost;
import miniBean.viewmodel.PostResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class PostFragment extends Fragment {

    String selectedImagePath = null;
    Uri selectedImageUri = null;
    private static final String TAG = PostFragment.class.getName();
    View actionBarView;
    public SharedPreferences session = null;
    final Integer SELECT_PICTURE = 1;
    TextView postTitle,postContent,post;
    ImageView postImage,image;
    public Boolean isPhoto = false;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.post, container, false);

        session = getActivity().getSharedPreferences("prefs", 0);

        /*getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActivity().getActionBar().setCustomView(R.layout.post_actionbar);
        */

        actionBarView = inflater.inflate(R.layout.post_actionbar, null);
        //((CommunityActivity)getActivity()).getActionBar().setCustomView(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        ((CommunityActivity) getActivity()).getActionBar().setCustomView(actionBarView, lp);
        post= (TextView) actionBarView.findViewById(R.id.titlePost);

        postContent= (TextView) view.findViewById(R.id.postContent);
        postTitle= (TextView) view.findViewById(R.id.postTitle);
        postImage= (ImageView) view.findViewById(R.id.browseImage);

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
                System.out.println("posted:::::::::");
                String title = postTitle.getText().toString();
                String content=postContent.getText().toString();

                if (!title.equals("")&&!content.equals(""))
                    setPost(title,content);

            }
        });
        image = (ImageView) view.findViewById(R.id.image);

        return view;
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
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void setPost(String postString,String postContent) {
        System.out.println(" :::::::::::: "+Long.parseLong(getArguments().getString("id")));
        System.out.println(" :::::::::::: "+postString);
        System.out.println(" :::::::::::: "+postContent);
        System.out.println(" :::::::::::: "+isPhoto);
        AppController.api.setQuestion(new NewPost(Long.parseLong(getArguments().getString("id")),postString,postContent,isPhoto),session.getString("sessionID",null), new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                if (isPhoto)
                    uploadPhoto(postResponse.getId());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void uploadPhoto(String postId) {
        File photo = new File(getRealPathFromUri(getActivity(), selectedImageUri));
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
}
