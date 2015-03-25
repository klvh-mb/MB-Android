package miniBean.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.DetailListAdapter;
import miniBean.adapter.PageListAdapter;
import miniBean.app.AppController;
import miniBean.util.ActivityUtil;
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

    private final Integer SELECT_PICTURE = 1;
    private FrameLayout mainFrameLayout;
    private Button pageButton;
    private ImageButton backButton, nextButton;
    private ImageView backImage, bookmarkAction, moreAction;
    private TextView commentEdit;
    private ImageView image;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;
    private ListView listView;
    private DetailListAdapter listAdapter;
    private PageListAdapter pageAdapter;
    private List<CommunityPostCommentVM> communityItems;
    private TextView questionText;
    private PopupWindow commentPopup, paginationPopup;
    private Boolean isBookmarked = false;
    private Spinner dropSpinner;
    private Boolean isPhoto = false;
    private TextView communityName, numPostViews, numPostComments;
    private ImageView communityIcon;
    private int noOfComments;
    private int curPage = 1;
    private CommunityPostCommentVM postVm = new CommunityPostCommentVM();

    private ActivityUtil activityUtil;

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

        activityUtil = new ActivityUtil(this);

        communityName = (TextView) findViewById(R.id.communityName);
        communityIcon = (ImageView) findViewById(R.id.commIcon);
        numPostViews = (TextView) findViewById(R.id.numPostViews);
        numPostComments = (TextView) findViewById(R.id.numPostComments);

        listView = (ListView) findViewById(R.id.detail_list);
        questionText = (TextView) findViewById(R.id.questionText);
        commentEdit = (TextView) findViewById(R.id.commentBody);
        pageButton = (Button) findViewById(R.id.page);
        backButton = (ImageButton) findViewById(R.id.back);
        nextButton = (ImageButton) findViewById(R.id.next);

        mainFrameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout);

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
                initiateCommentPopup();
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

        bookmarkAction = (ImageView) findViewById(R.id.bookmarkAction);
        moreAction = (ImageView) findViewById(R.id.moreAction);

        final Long postID = getIntent().getLongExtra("postId", 0L);

        bookmarkAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBookmarked) {
                    bookmark(postID);
                    bookmarkAction.setImageResource(R.drawable.bookmarked);
                    isBookmarked = true;
                } else {
                    unbookmark(postID);
                    bookmarkAction.setImageResource(R.drawable.bookmark);
                    isBookmarked = false;
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

                isBookmarked = post.isBookmarked;
                if (isBookmarked) {
                    bookmarkAction.setImageResource(R.drawable.bookmarked);
                } else {
                    bookmarkAction.setImageResource(R.drawable.bookmark);
                }

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
                    ImageLoader.getInstance().displayImage(
                            getResources().getString(R.string.base_url) + post.getCi(),
                            communityIcon,
                            AppController.ROUNDED_CORNERS_IMAGE_OPTIONS);
                }

                setPageButtons(curPage);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(DetailActivity.this, DetailActivity.this.getString(R.string.post_not_found), Toast.LENGTH_SHORT).show();
                finish();
                retrofitError.printStackTrace();
            }
        });
    }

    private void initiateCommentPopup() {
        try {
            mainFrameLayout.getForeground().setAlpha(20);
            mainFrameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) DetailActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.comment_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            commentPopup = new PopupWindow(
                    layout,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, //activityUtil.getRealDimension(DefaultValues.COMMENT_POPUP_HEIGHT),
                    true);
            commentPopup.setOutsideTouchable(false);
            commentPopup.setFocusable(true);
            commentPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            commentPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);

            commentPopup.setTouchInterceptor(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent event) {
                    return false;
                }
            });

            activityUtil.popupInputMethodWindow();

            final EditText commentEditText = (EditText) layout.findViewById(R.id.commentEditText);
            TextView postButton = (TextView) layout.findViewById(R.id.postButton);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String commentString = commentEditText.getText().toString();

                    if (!commentString.equals("")) {
                        answerQuestion(commentString);
                        commentPopup.dismiss();
                    }
                }
            });
            ImageView cancelButton = (ImageView) layout.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("cancelbutton");
                    commentPopup.dismiss();
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
            if (data == null)
                return;

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
                if (isPhoto) {
                    uploadPhoto(array.getId());
                }else{
                    getComments(getIntent().getLongExtra("postId", 0L),0);  // reload page
                }
                commentPopup.dismiss();
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
                getComments(getIntent().getLongExtra("postId", 0L),0);  // reload page
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    private void initiatePaginationPopup() {
        try {
            mainFrameLayout.getForeground().setAlpha(20);
            mainFrameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) DetailActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.pagination_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            paginationPopup = new PopupWindow(
                    layout,
                    activityUtil.getRealDimension(DefaultValues.PAGINATION_POPUP_WIDTH),
                    activityUtil.getRealDimension(DefaultValues.PAGINATION_POPUP_HEIGHT),
                    true);
            paginationPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            paginationPopup.setOutsideTouchable(false);
            paginationPopup.setFocusable(true);
            paginationPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

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
                    getComments(getIntent().getLongExtra("postId", 0L),position);
                    paginationPopup.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPageButtons(int page) {
        curPage = page;

        int maxPage = getMaxPage();
        pageButton.setText(page + "/" + getMaxPage());
        if (maxPage > 1) {
            pageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiatePaginationPopup();
                }
            });
        }

        if (maxPage == 1) {
            backButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            backButton.setEnabled(false);
            nextButton.setEnabled(false);
        } else {
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);

            if (page <= 1) {
                backButton.setEnabled(false);
                backButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_back_gray));
            } else {
                backButton.setEnabled(true);
                backButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_back));

                final int pageOffset = page - 1;
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getComments(getIntent().getLongExtra("postId", 0L), pageOffset - 1);
                    }
                });
            }

            if (page >= maxPage) {
                nextButton.setEnabled(false);
                nextButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_next_gray));
            } else {
                nextButton.setEnabled(true);
                nextButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_next));

                final int pageOffset = page - 1;
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getComments(getIntent().getLongExtra("postId", 0L), pageOffset + 1);
                    }
                });
            }
        }
    }

    private int getMaxPage() {
        if (noOfComments == 0) {
            return 1;
        }
        return (int)Math.ceil((double)noOfComments / (double)DefaultValues.DEFAULT_PAGINATION_COUNT);
    }

    public void bookmark(Long postId) {
        AppController.api.setBookmark(postId, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bookmarkAction.setImageResource(R.drawable.bookmarked);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void unbookmark(Long postId) {
        AppController.api.setUnBookmark(postId, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bookmarkAction.setImageResource(R.drawable.bookmark);
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

                setPageButtons(offset + 1);     // set page before adapter as it takes in curPage
                listAdapter = new DetailListAdapter(DetailActivity.this, communityPostCommentVMs, curPage);
                listView.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}