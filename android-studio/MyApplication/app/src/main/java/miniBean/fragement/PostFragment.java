package miniBean.fragement;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;

import miniBean.R;
import miniBean.activity.CommunityActivity;
import miniBean.app.AppController;
import miniBean.viewmodel.NewPost;
import miniBean.viewmodel.PostResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class PostFragment extends Fragment {

    String selectedImagePath = null;
    Uri selectedImageUri = null;
    private static final String TAG = PostFragment.class.getName();
    View actionBarView;
    final Integer SELECT_PICTURE = 1;
    TextView postTitle,postContent,post;
    ImageView postImage,image,backImage;
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

        /*getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActivity().getActionBar().setCustomView(R.layout.post_actionbar);
        */

        actionBarView = inflater.inflate(R.layout.post_actionbar, null);
        //((CommunityActivity)getActivity()).getActionBar().setCustomView(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        ((CommunityActivity) getActivity()).getActionBar().setCustomView(actionBarView, lp);
        post= (TextView) actionBarView.findViewById(R.id.titlePost);
        backImage= (ImageView) actionBarView.findViewById(R.id.backImage);

        postContent= (TextView) view.findViewById(R.id.postContent);
        postTitle= (TextView) view.findViewById(R.id.postTitle);
        postImage= (ImageView) view.findViewById(R.id.browseImage);
        image = (ImageView) view.findViewById(R.id.image);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you Want Leave The Post Window..?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PostFragment.this.getActivity().onBackPressed();
                                /*
                                Intent intent=new Intent(getActivity(),CommunityActivity.class);
                                intent.putExtra("id",getArguments().getString("id"));
                                intent.putExtra("commName",getArguments().getString("commName"));
                                intent.putExtra("flag","FromPostFragment");
                                startActivity(intent);
                                */
                                /*FragmentManager fm=getFragmentManager();
                                fm.popBackStack();*/
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
                String content = postContent.getText().toString();

                if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(content)) {
                    doPost(title, content);
                }
            }
        });

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

    public void doPost(String postString,String postContent) {
        System.out.println(" :::::::::::: "+Long.parseLong(getArguments().getString("id")));
        System.out.println(" :::::::::::: "+postString);
        System.out.println(" :::::::::::: "+postContent);
        System.out.println(" :::::::::::: "+isPhoto);
        AppController.api.setQuestion(new NewPost(Long.parseLong(getArguments().getString("id")),postString,postContent,isPhoto),AppController.getInstance().getSessionId(), new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                if (isPhoto) {
                    uploadPhoto(postResponse.getId());
                }
                Toast.makeText(getActivity(), "Post Successful", Toast.LENGTH_SHORT).show();

                /*
                Intent intent = new Intent(getActivity(), CommunityActivity.class);
                intent.putExtra("id",getArguments().getString("id"));
                intent.putExtra("commName",getArguments().getString("commName"));
                intent.putExtra("flag","FromPostFragment");
                startActivity(intent);
                */
                PostFragment.this.getActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Post Failure", Toast.LENGTH_SHORT).show();

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
