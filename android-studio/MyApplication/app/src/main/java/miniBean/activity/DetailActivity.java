package miniBean.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import miniBean.app.MyApi;
import miniBean.R;
import miniBean.adapter.DetailListAdapter;
import miniBean.viewmodel.Comment;
import miniBean.viewmodel.CommentPost;
import miniBean.viewmodel.Post;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class DetailActivity extends FragmentActivity {

    public SharedPreferences session = null;
    public MyApi api;
    ImageView backImage;
    EditText commentEdit;
    private ListView listView;
    private DetailListAdapter listAdapter;
    private List<Comment> communityItems;
    private TextView questionText, userText, postedOnText, postText, locationText, timeText;
    private PopupWindow pw;

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
        commentEdit = (EditText) findViewById(R.id.commentBody);

        //userText= (TextView) findViewById(R.id.userText);
        //postedOnText= (TextView) findViewById(R.id.postedOn);
        //postText= (TextView) findViewById(R.id.post);
        //locationText= (TextView) findViewById(R.id.location);
        //timeText= (TextView) findViewById(R.id.timeText);

        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FrameLayout layout_MainMenu;
                // layout_MainMenu= (FrameLayout) findViewById(R.id.mainMenu);
                // layout_MainMenu.getForeground().setAlpha( 220);
                //initiatePopupWindow();

            }
        });

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.detail_actionbar);

        communityItems = new ArrayList<>();

        listAdapter = new DetailListAdapter(this, communityItems);
        listView.setAdapter(listAdapter);
        backImage = (ImageView) findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        System.out.println("Before getCommunity");
        getQnaDetail();
        System.out.println("After getCommunity");
    }

    private void getQnaDetail() {
        System.out.println("In getCommunity");
        Intent intent = getIntent();
        Long postID = intent.getLongExtra("postId", 0L);
        Long commID = intent.getLongExtra("commId", 0L);
        api.qnaLanding(postID, commID, new Callback<PostArray>() {
            @Override
            public void success(PostArray array, retrofit.client.Response response) {
                if (array.getPosts().size() != 0) {

                    Post post = array.getPosts().get(0);
                    questionText.setText(post.getPtl());

                    Comment comment = new Comment();
                    comment.setOn(post.getP());
                    comment.setCd(post.getT());
                    comment.setD(post.getPt());
                    comment.setOid(post.getOid());
                    communityItems.add(comment);
                    communityItems.addAll(post.getCs());
                }

                listAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors

            }
        });
    }

    private void answerQuestion(String commentString) {
        api.answerOnQuestion(new CommentPost(getIntent().getLongExtra("postId", 0L), commentString), session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response array, retrofit.client.Response response) {
                System.out.println("Response:::::::" + array);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                //retrofitError.printStackTrace(); //to see if you have errors

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
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, 170, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

            final EditText commentPost = (EditText) layout.findViewById(R.id.comment);
            Button postButton = (Button) layout.findViewById(R.id.postButton);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String commentString = commentPost.getText().toString();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

