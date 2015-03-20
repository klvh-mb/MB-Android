package miniBean.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.DetailListAdapter;
import miniBean.adapter.PageListAdapter;
import miniBean.app.AppController;
import miniBean.util.CommunityIconUtil;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommentPost;
import miniBean.viewmodel.CommentResponse;
import miniBean.viewmodel.CommunityPostCommentVM;
import miniBean.viewmodel.CommunityPostVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class DetailActivity extends FragmentActivity {

    final Integer SELECT_PICTURE = 1;
    public Button pageButton;
    ImageView backImage, bookmark, moreAction;
    TextView commentEdit;
    ImageView image;
    String selectedImagePath = null;
    Uri selectedImageUri = null;
    private ListView listView;
    private DetailListAdapter listAdapter;
    private PageListAdapter pageAdapter;
    private List<CommunityPostCommentVM> communityItems;
    private TextView questionText, userText, postedOnText, postText, locationText, timeText;
    private PopupWindow pw,pagePop;
    Boolean isBookMarked = false;
    Spinner dropSpinner;
    public Boolean isPhoto = false;
    private TextView communityName, numPostViews, numPostComments;
    private ImageView communityIcon;
    public int noOfComments;
    public int curPage = 1;
    CommunityPostCommentVM postVm = new CommunityPostCommentVM();

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

        setContentView(R.layout.detail_activity);

        communityName = (TextView) findViewById(R.id.communityName);
        communityIcon = (ImageView) findViewById(R.id.commIcon);
        numPostViews = (TextView) findViewById(R.id.numPostViews);
        numPostComments = (TextView) findViewById(R.id.numPostComments);

        listView = (ListView) findViewById(R.id.detail_list);
        questionText = (TextView) findViewById(R.id.questionText);
        commentEdit = (TextView) findViewById(R.id.commentBody);
        pageButton = (Button) findViewById(R.id.page);
        final FrameLayout layout_MainMenu;
        layout_MainMenu = (FrameLayout) findViewById(R.id.mainMenu);

        communityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long commId = getIntent().getLongExtra("commId", 0L);
                Intent intent = new Intent(DetailActivity.this,CommunityActivity.class);
                intent.putExtra("flag", "FromDetailActivity");
                intent.putExtra("id", commId+"");
                startActivity(intent);
            }
        });

        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_MainMenu.getForeground().setAlpha(220);
                initiatePopupWindow();
            }
        });

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        // getActionBar().setCustomView(R.layout.detail_actionbar,);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.detail_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_bg)));
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActionBar().setIcon(
        //       new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        // getActionBar().setTitle("Details");
        bookmark = (ImageView) findViewById(R.id.bookmarkedtAction);
        moreAction = (ImageView) findViewById(R.id.moreAction);

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
        Intent intent = getIntent();
        Long postID = intent.getLongExtra("postId", 0L);
        Long commID = intent.getLongExtra("commId", 0L);

        AppController.api.qnaLanding(postID, commID, AppController.getInstance().getSessionId(), new Callback<CommunityPostVM>() {
            @Override
            public void success(CommunityPostVM post, Response response) {
                communityName.setText(post.getCn());
                numPostViews.setText(post.getNov() + "");
                numPostComments.setText(post.getN_c() + "");
                questionText.setText(post.getPtl());

                postVm.setPost(true);
                postVm.setO(post.isO());
                postVm.setNol(post.getNol());
                postVm.setId(post.getId());
                postVm.setOn(post.getP());
                postVm.setCd(post.getT());
                postVm.setD(post.getPt());
                postVm.setOid(post.getOid());
                postVm.setLike(post.isLike());
                postVm.setHasImage(post.isHasImage());
                postVm.setImgs(post.getImgs());
                noOfComments = post.getN_c();
                communityItems.add(postVm);
                communityItems.addAll(post.getCs());
                listAdapter = new DetailListAdapter(DetailActivity.this, communityItems, curPage);
                listView.setAdapter(listAdapter);

                int iconMapped = CommunityIconUtil.map(post.getCi());
                if (iconMapped != -1) {
                    //Log.d(this.getClass().getSimpleName(), "getQnaDetail: replace source with local comm icon - " + commIcon);
                    communityIcon.setImageDrawable(getResources().getDrawable(iconMapped));
                } else {
                    Log.d(this.getClass().getSimpleName(), "getQnaDetail: load comm icon from background - " + post.getCi());
                    AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + post.getCi(), communityIcon);
                }

                setPageButton(curPage);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(DetailActivity.this, DetailActivity.this.getString(R.string.post_not_found), Toast.LENGTH_SHORT).show();
                finish();
                retrofitError.printStackTrace();
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
                    {
                        answerQuestion(commentString);
                        pw.dismiss();
                    }
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
        AppController.api.answerOnQuestion(new CommentPost(getIntent().getLongExtra("postId", 0L), commentString, true), AppController.getInstance().getSessionId(), new Callback<CommentResponse>() {
            @Override
            public void success(CommentResponse array, Response response) {
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
            public void success(Response array, Response response) {
                System.out.println("Response:::::::" + array);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    private void initiatePopup() {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) DetailActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.pagination_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));
            // create a 300px width and 470px height PopupWindow
            pagePop = new PopupWindow(layout, 450, 1000, true);
            // display the popup in the center
            pagePop.showAtLocation(layout, Gravity.CENTER_VERTICAL, 0, 0);

            pagePop.setOutsideTouchable(true);
            pagePop.setFocusable(false);
            ListView listView1 = (ListView) layout.findViewById(R.id.pageList);
            ArrayList<String> stringArrayList = new ArrayList<String>();
            for (int i = 0; i < getMaxPage(); i++) {
                stringArrayList.add(getString(R.string.page_before)+(i+1)+getString(R.string.page_after));
            }
            pageAdapter = new PageListAdapter(this,stringArrayList);
            listView1.setAdapter(pageAdapter);

            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(this.getClass().getSimpleName(), "listView1.onItemClick: Page " + curPage);
                    curPage = position + 1;
                    setPageButton(curPage);
                    getComments(getIntent().getLongExtra("postId", 0L),position);
                    pagePop.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPageButton(int page) {
        int maxPage = getMaxPage();
        pageButton.setText(page + "/" + getMaxPage());
        if (maxPage > 1) {
            pageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiatePopup();
                }
            });
        }
    }

    private int getMaxPage() {
        if (noOfComments == 0) {
            return 1;
        }
        return (int)Math.ceil((double)noOfComments / (double)DefaultValues.DEFAULT_PAGINATION_COUNT);
    }

    public void getBookmark(Long postId) {
        AppController.api.setBookmark(postId, AppController.getInstance().getSessionId(), new Callback<Response>() {
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
        AppController.api.setUnBookmark(postId, AppController.getInstance().getSessionId(), new Callback<Response>() {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.a:
                System.out.println("Report clicked...");
                return true;
            case R.id.b:
                System.out.println("Url clicked...");
                return true;
            default:
                return false;

        }

    }

    public void getComments(Long postID, final int offset) {
        AppController.api.getComments(postID,offset,AppController.getInstance().getSessionId(),new Callback<List<CommunityPostCommentVM>>(){

            @Override
            public void success(List<CommunityPostCommentVM> commentVMs, Response response) {
                communityItems.clear();
                List<CommunityPostCommentVM> communityPostCommentVMs = new ArrayList<CommunityPostCommentVM>();
                if (offset == 0)    // insert post itself for first page only
                    communityPostCommentVMs.add(postVm);
                communityPostCommentVMs.addAll(commentVMs);
                listAdapter = new DetailListAdapter(DetailActivity.this, communityPostCommentVMs, curPage);
                listView.setAdapter(listAdapter);

                pagePop.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

}