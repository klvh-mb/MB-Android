package miniBean.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.DetailListAdapter;
import miniBean.app.AppController;
import miniBean.app.MyApi;
import miniBean.viewmodel.CommentPost;
import miniBean.viewmodel.CommentResponse;
import miniBean.viewmodel.CommunityPostCommentVM;
import miniBean.viewmodel.CommunityPostVM;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class DetailActivity extends FragmentActivity {

    final Integer SELECT_PICTURE = 1;
    public SharedPreferences session = null;
    public MyApi api;
    public SeekBar seekBar;
    ImageView backImage, bookmark, moreAction;
    TextView commentEdit;
    ImageView image;
    String selectedImagePath = null;
    Uri selectedImageUri = null;
    private ListView listView;
    private DetailListAdapter listAdapter;
    private List<CommunityPostCommentVM> communityItems;
    private TextView questionText, userText, postedOnText, postText, locationText, timeText;
    private PopupWindow pw;
    Boolean isBookMarked = false;
    Spinner dropSpinner;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();
        setContentView(R.layout.detail_activity);
        session = getSharedPreferences("prefs", 0);
        api = restAdapter.create(MyApi.class);


        listView = (ListView) findViewById(R.id.detail_list);
        questionText = (TextView) findViewById(R.id.questionText);
        commentEdit = (TextView) findViewById(R.id.commentBody);


        final int color = 0xFFFF0000;
        final Drawable drawable = new ColorDrawable(color);


        final FrameLayout layout_MainMenu;
        layout_MainMenu = (FrameLayout) findViewById(R.id.mainMenu);

        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_MainMenu.getForeground().setAlpha(220);
                initiatePopupWindow();

            }
        });
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.detail_actionbar);
        bookmark = (ImageView) findViewById(R.id.bookmarkedtAction);
        moreAction = (ImageView) findViewById(R.id.moreAction);
        dropSpinner = (Spinner) findViewById(R.id.spinner);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                //String.valueOf(progress);
                //Toast.makeText(getApplicationContext(), String.valueOf(progress), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        final Long postID = getIntent().getLongExtra("postId", 0L);

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBookMarked) {
                    getBookmark(postID);
                    bookmark.setImageResource(R.drawable.bookmarked);
                    isBookMarked = true;
                } else {
                    unGetBookmark(postID);
                    bookmark.setImageResource(R.drawable.bookmark);
                    isBookMarked = false;
                }
            }
        });

        moreAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dropdown();
            }
        });

        communityItems = new ArrayList<>();
        backImage = (ImageView) findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getQnaDetail();
    }

    private void getQnaDetail() {
        System.out.println("In getCommunity");
        Intent intent = getIntent();
        Long postID = intent.getLongExtra("postId", 0L);
        Long commID = intent.getLongExtra("commId", 0L);
        api.qnaLanding(postID, commID, new Callback<CommunityPostVM>() {
            @Override
            public void success(CommunityPostVM post, retrofit.client.Response response) {

                questionText.setText(post.getPtl());

                CommunityPostCommentVM comment = new CommunityPostCommentVM();
                comment.setPost(true);
                comment.setNol(post.getNol());
                comment.setId(post.getId());
                comment.setOn(post.getP());
                comment.setCd(post.getT());
                comment.setD(post.getPt());
                comment.setOid(post.getOid());
                communityItems.add(comment);
                communityItems.addAll(post.getCs());
                listAdapter = new DetailListAdapter(DetailActivity.this, communityItems);
                listView.setAdapter(listAdapter);

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors

            }
        });
    }

    private void initiatePopupWindow() {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) DetailActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.comment_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));
            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, 300, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER_VERTICAL, 0, 0);

            pw.setOutsideTouchable(true);
            pw.setFocusable(false);

            final EditText commentPost = (EditText) layout.findViewById(R.id.comment);
            Button postButton = (Button) layout.findViewById(R.id.postButton);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String commentString = commentPost.getText().toString();

                    if (!commentString.equals(""))
                        answerQuestion(commentString);
                }
            });
            ImageView cancelButton = (ImageView) layout.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("cancelbutton");
                    pw.dismiss();
                }
            });
            ImageView imageButton = (ImageView) layout.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                    isPhoto = true;

                }
            });

            image = (ImageView) layout.findViewById(R.id.postImage);
            System.out.println("Image Path : " + selectedImagePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void answerQuestion(String commentString) {
        api.answerOnQuestion(new CommentPost(getIntent().getLongExtra("postId", 0L), commentString, true), session.getString("sessionID", null), new Callback<CommentResponse>() {
            @Override
            public void success(CommentResponse array, retrofit.client.Response response) {
                if (isPhoto)
                    uploadPhoto(array.getId());
                pw.dismiss();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors

            }
        });
    }

    public void uploadPhoto(String commentId) {
        File photo = new File(getRealPathFromUri(getApplicationContext(), selectedImageUri));
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);
        AppController.api.uploadCommentPhoto(commentId, typedFile, new Callback<Response>() {
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

    public void getBookmark(Long postId) {
        AppController.api.getBookmark(postId, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bookmark.setImageResource(R.drawable.bookmarked);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void unGetBookmark(Long postId) {
        AppController.api.getUnBookmark(postId, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bookmark.setImageResource(R.drawable.bookmark);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace(); //to see if you have errors

            }
        });
    }

}

