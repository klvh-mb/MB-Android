package miniBean.activity;

import android.app.ActionBar;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.DetailListAdapter;
import miniBean.adapter.EmoticonListAdapter;
import miniBean.adapter.PopupPageListAdapter;
import miniBean.app.AppController;
import miniBean.app.EmoticonCache;
import miniBean.app.TrackedFragmentActivity;
import miniBean.util.ImageMapping;
import miniBean.util.DefaultValues;
import miniBean.util.ImageUtil;
import miniBean.util.SharingUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.CommentPost;
import miniBean.viewmodel.CommentResponse;
import miniBean.viewmodel.CommunityPostCommentVM;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.EmoticonVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class DetailActivity extends TrackedFragmentActivity {

    private FrameLayout mainFrameLayout;
    private Button pageButton;
    private ImageButton backButton, nextButton;
    private ImageView backImage, whatsappAction, bookmarkAction;
    private TextView commentEdit;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;
    private ListView listView;
    private DetailListAdapter listAdapter;
    private PopupPageListAdapter pageAdapter;
    private List<CommunityPostCommentVM> communityItems;
    private TextView questionText;
    private PopupWindow commentPopup, paginationPopup, emoPopup;
    private Boolean isBookmarked = false;
    private TextView communityName, numPostViews, numPostComments;
    private ImageView communityIcon;
    private EditText commentEditText;

    private long postId, commId;
    private int noOfComments;
    private int curPage = 1;
    private CommunityPostCommentVM postVm = new CommunityPostCommentVM();

    private List<EmoticonVM> emoticonVMList = new ArrayList<>();
    private EmoticonListAdapter emoticonListAdapter;

    private TextView commentPostButton;
    private ImageView commentBrowseButton, commentCancelButton, commentEmoImage;
    private List<ImageView> commentImages = new ArrayList<>();
    private List<File> photos = new ArrayList<>();

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
        commentEdit = (TextView) findViewById(R.id.commentEdit);
        pageButton = (Button) findViewById(R.id.page);
        backButton = (ImageButton) findViewById(R.id.back);
        nextButton = (ImageButton) findViewById(R.id.next);

        mainFrameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout);

        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCommentPopup();
            }
        });

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.detail_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        // hide it first... show after set actionbar bg with post subtype
        getActionBar().hide();

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        //getActionBar().setTitle("Details");

        postId = getIntent().getLongExtra("postId", 0L);
        commId = getIntent().getLongExtra("commId", 0L);

        whatsappAction = (ImageView) findViewById(R.id.whatsappAction);
        bookmarkAction = (ImageView) findViewById(R.id.bookmarkAction);

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

    private boolean isFromPN(CommunityPostVM post) {
        String flag = getIntent().getStringExtra("flag");
        return "FromPN".equals(flag) ||
                (!"FromKG".equals(flag) && ("PN".equals(post.getSubType()) || "PN_KG".equals(post.getSubType())));
    }

    private boolean isFromKG(CommunityPostVM post) {
        String flag = getIntent().getStringExtra("flag");
        return "FromKG".equals(flag) ||
                (!"FromPN".equals(flag) && "KG".equals(post.getSubType()));
    }

    private void getQnaDetail() {
        ViewUtil.showSpinner(this);

        AppController.getApi().qnaLanding(postId, commId, AppController.getInstance().getSessionId(), new Callback<CommunityPostVM>() {
            @Override
            public void success(final CommunityPostVM post, Response response) {
                //Log.d(DetailActivity.class.getSimpleName(), "getQnaDetail: post subtype="+post.getSubType());

                getActionBar().show();

                final boolean isFromPN = isFromPN(post);
                final boolean isFromKG = isFromKG(post);
                if (isFromPN) {
                    getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_green));
                } else if (isFromKG) {
                    getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_maroon));
                } else {
                    getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_purple));
                }

                communityName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = null;
                        if (isFromPN) {
                            intent = new Intent(DetailActivity.this, PNCommunityActivity.class);
                            intent.putExtra("id", post.getPnId());
                            intent.putExtra("commId", getIntent().getLongExtra("commId", 0L));
                            intent.putExtra("flag", "FromPN");
                        } else if (isFromKG) {
                            intent = new Intent(DetailActivity.this, KGCommunityActivity.class);
                            intent.putExtra("id", post.getKgId());
                            intent.putExtra("commId", getIntent().getLongExtra("commId", 0L));
                            intent.putExtra("flag", "FromKG");
                        } else {
                            intent = new Intent(DetailActivity.this, CommunityActivity.class);
                            intent.putExtra("id", getIntent().getLongExtra("commId", 0L));
                            intent.putExtra("flag", "FromDetailActivity");
                        }
                        startActivity(intent);
                    }
                });

                communityName.setText(post.getCn());
                numPostViews.setText(post.getNov() + "");
                numPostComments.setText(post.getN_c() + "");

                ViewUtil.setHtmlText(post.getPtl(), questionText, DetailActivity.this, true);

                isBookmarked = post.isBookmarked;
                if (isBookmarked) {
                    bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
                } else {
                    bookmarkAction.setImageResource(R.drawable.ic_bookmark);
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
                postVm.setAndroid(post.isAndroid());
                postVm.setIOS(post.isIOS());
                postVm.setMobile(post.isMobile());
                noOfComments = post.getN_c();
                communityItems.add(postVm);
                communityItems.addAll(post.getCs());
                listAdapter = new DetailListAdapter(DetailActivity.this, communityItems, curPage);
                listView.setAdapter(listAdapter);

                int iconMapped = ImageMapping.map(post.getCi());
                if (iconMapped != -1) {
                    //Log.d(this.getClass().getSimpleName(), "getQnaDetail: replace source with local comm icon - " + commIcon);
                    communityIcon.setImageDrawable(getResources().getDrawable(iconMapped));
                } else {
                    Log.d(this.getClass().getSimpleName(), "getQnaDetail: load comm icon from background - " + post.getCi());
                    ImageUtil.displayRoundedCornersImage(post.getCi(), communityIcon);
                }

                setPageButtons(curPage);

                // actionbar actions...
                whatsappAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharingUtil.shareToWhatapp(post, DetailActivity.this);
                    }
                });

                bookmarkAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isBookmarked) {
                            bookmark(post.getId());
                            bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
                            isBookmarked = true;
                        } else {
                            unbookmark(post.getId());
                            bookmarkAction.setImageResource(R.drawable.ic_bookmark);
                            isBookmarked = false;
                        }
                    }
                });

                ViewUtil.stopSpinner(DetailActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                if (RetrofitError.Kind.NETWORK.equals(error.getKind()) ||
                        RetrofitError.Kind.HTTP.equals(error.getKind())) {
                    Toast.makeText(DetailActivity.this, DetailActivity.this.getString(R.string.post_not_found), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, DetailActivity.this.getString(R.string.connection_timeout_message), Toast.LENGTH_SHORT).show();
                }

                ViewUtil.stopSpinner(DetailActivity.this);

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, DefaultValues.DEFAULT_HANDLER_DELAY);

                Log.e(DetailActivity.class.getSimpleName(), "getQnaDetail: failure", error);
            }
        });
    }

    private void initCommentPopup() {
        mainFrameLayout.getForeground().setAlpha(20);
        mainFrameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

        try {
            LayoutInflater inflater = (LayoutInflater) DetailActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View layout = inflater.inflate(R.layout.comment_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            if (commentPopup == null) {
                commentPopup = new PopupWindow(
                        layout,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, //activityUtil.getRealDimension(DefaultValues.COMMENT_POPUP_HEIGHT),
                        true);

                commentPopup.setOutsideTouchable(false);
                commentPopup.setFocusable(true);
                commentPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
                commentPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                commentPopup.setTouchInterceptor(new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent event) {
                        return false;
                    }
                });

                commentEditText = (EditText) layout.findViewById(R.id.commentEditText);
                commentEditText.setLongClickable(true);

                // NOTE: UGLY WORKAROUND or pasting text to comment edit!!!
                commentEditText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.d(DetailActivity.this.getClass().getSimpleName(), "onLongClick");
                        startActionMode(new ActionMode.Callback() {
                            final int PASTE_MENU_ITEM_ID = 0;

                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                Log.d(DetailActivity.this.getClass().getSimpleName(), "onPrepareActionMode");
                                return true;
                            }

                            public void onDestroyActionMode(ActionMode mode) {
                            }

                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                Log.d(DetailActivity.this.getClass().getSimpleName(), "onCreateActionMode: menu size=" + menu.size());
                                menu.add(0, PASTE_MENU_ITEM_ID, 0, "Paste");
                                menu.setQwertyMode(false);
                                return true;
                            }

                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                Log.d(DetailActivity.this.getClass().getSimpleName(), "onActionItemClicked: item clicked=" + item.getItemId() + " title=" + item.getTitle());
                                switch (item.getItemId()) {
                                    case PASTE_MENU_ITEM_ID:
                                        final ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                        if (clipBoard != null && clipBoard.getPrimaryClip() != null && clipBoard.getPrimaryClip().getItemAt(0) != null) {
                                            String paste = clipBoard.getPrimaryClip().getItemAt(0).getText().toString();
                                            commentEditText.getText().insert(commentEditText.getSelectionStart(), paste);
                                        }
                                }

                                mode.finish();

                                // popup again
                                commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
                                ViewUtil.popupInputMethodWindow(DetailActivity.this);
                                return true;
                            }
                        });
                        return true;
                    }
                });

                /*
                commentEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        Log.d(DetailActivity.this.getClass().getSimpleName(), "onPrepareActionMode");
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode mode) {
                    }

                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        Log.d(DetailActivity.this.getClass().getSimpleName(), "onCreateActionMode");
                        return false;
                    }

                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        Log.d(DetailActivity.this.getClass().getSimpleName(), "onActionItemClicked");
                        return false;
                    }
                });
                */

                commentPostButton = (TextView) layout.findViewById(R.id.postButton);
                commentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doComment();
                    }
                });

                commentCancelButton = (ImageView) layout.findViewById(R.id.cancelButton);
                commentCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentPopup.dismiss();
                        commentPopup = null;
                    }
                });

                commentBrowseButton = (ImageView) layout.findViewById(R.id.browseImage);
                commentBrowseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (photos.size() == DefaultValues.MAX_COMMENT_IMAGES) {
                            Toast.makeText(DetailActivity.this, DetailActivity.this.getString(R.string.comment_max_images), Toast.LENGTH_SHORT).show();
                        } else {
                            ImageUtil.openPhotoPicker(DetailActivity.this);
                        }
                    }
                });

                if (commentImages.size() == 0) {
                    commentImages.add((ImageView) layout.findViewById(R.id.commentImage1));
                    commentImages.add((ImageView) layout.findViewById(R.id.commentImage2));
                    commentImages.add((ImageView) layout.findViewById(R.id.commentImage3));

                    for (ImageView commentImage : commentImages) {
                        commentImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeCommentImage();
                            }
                        });
                    }
                }

                commentEmoImage = (ImageView) layout.findViewById(R.id.emoImage);
                commentEmoImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initEmoticonPopup();
                    }
                });
            }

            if (emoticonVMList.isEmpty() && EmoticonCache.getEmoticons().isEmpty()) {
                EmoticonCache.refresh();
            }

            commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            ViewUtil.popupInputMethodWindow(this);

            Log.d(this.getClass().getSimpleName(), "initCommentPopup: " + selectedImagePath);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "initCommentPopup: failure", e);
        }
    }

    private void resetCommentImages() {
        commentImages = new ArrayList<>();
        photos = new ArrayList<>();
    }

    private void setCommentImage(Bitmap bitmap) {
        ImageView commentImage = commentImages.get(photos.size());
        commentImage.setImageDrawable(new BitmapDrawable(this.getResources(), bitmap));
        commentImage.setVisibility(View.VISIBLE);
        File photo = new File(selectedImagePath);
        photos.add(photo);
    }

    private void removeCommentImage() {
        if (photos.size() > 0) {
            int toRemove = photos.size()-1;
            commentImages.get(toRemove).setImageDrawable(null);
            photos.remove(toRemove);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ViewUtil.SELECT_PICTURE_REQUEST_CODE && resultCode == RESULT_OK &&
                data != null && photos.size() < DefaultValues.MAX_COMMENT_IMAGES) {

            selectedImageUri = data.getData();
            selectedImagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);

            String path = selectedImageUri.getPath();
            Log.d(this.getClass().getSimpleName(), "onActivityResult: selectedImageUri=" + path + " selectedImagePath=" + selectedImagePath);

            Bitmap bitmap = ImageUtil.resizeAsPreviewThumbnail(selectedImagePath);
            if (bitmap != null) {
                setCommentImage(bitmap);
            } else {
                Toast.makeText(DetailActivity.this, DetailActivity.this.getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(DetailActivity.this, DetailActivity.this.getString(R.string.photo_not_found), Toast.LENGTH_SHORT).show();
        }

        // pop back soft keyboard
        ViewUtil.popupInputMethodWindow(this);
    }

    private void doComment() {
        String comment = commentEditText.getText().toString().trim();
        if (StringUtils.isEmpty(comment)) {
            Toast.makeText(DetailActivity.this, DetailActivity.this.getString(R.string.invalid_comment_body_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        ViewUtil.showSpinner(this);

        final boolean withPhotos = photos.size() > 0;

        Log.d(this.getClass().getSimpleName(), "doComment: postId="+getIntent().getLongExtra("postId", 0L)+" comment="+comment.substring(0, Math.min(5, comment.length())));
        AppController.getApi().answerOnQuestion(new CommentPost(getIntent().getLongExtra("postId", 0L), comment, withPhotos), AppController.getInstance().getSessionId(), new Callback<CommentResponse>() {
            @Override
            public void success(CommentResponse array, Response response) {
                if (withPhotos) {
                    uploadPhotos(array.getId());
                } else {
                    getComments(getIntent().getLongExtra("postId", 0L), 0);  // reload page
                }
                Toast.makeText(DetailActivity.this, DetailActivity.this.getString(R.string.comment_success), Toast.LENGTH_LONG).show();

                reset();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(DetailActivity.this.getClass().getSimpleName(), "doComment.api.answerOnQuestion: failed with error", error);
                Toast.makeText(DetailActivity.this, DetailActivity.this.getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
                reset();
            }
        });
    }

    private void uploadPhotos(String commentId) {
        for (File photo : photos) {
            photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
            TypedFile typedFile = new TypedFile("application/octet-stream", photo);
            AppController.getApi().uploadCommentPhoto(commentId, typedFile, new Callback<Response>() {
                @Override
                public void success(Response array, Response response) {
                    getComments(getIntent().getLongExtra("postId", 0L), 0);  // reload page
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(DetailActivity.class.getSimpleName(), "uploadPhotos: failure", error);
                }
            });
        }
    }

    private void initPaginationPopup() {
        mainFrameLayout.getForeground().setAlpha(20);
        mainFrameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) DetailActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.pagination_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            if (paginationPopup == null) {
                paginationPopup = new PopupWindow(
                        layout,
                        ViewUtil.getRealDimension(DefaultValues.PAGINATION_POPUP_WIDTH, this.getResources()),
                        ViewUtil.getRealDimension(DefaultValues.PAGINATION_POPUP_HEIGHT, this.getResources()),
                        true);
            }

            paginationPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            paginationPopup.setOutsideTouchable(false);
            paginationPopup.setFocusable(true);
            paginationPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ListView listView1 = (ListView) layout.findViewById(R.id.pageList);
            ArrayList<String> stringArrayList = new ArrayList<String>();
            for (int i = 0; i < getMaxPage(); i++) {
                stringArrayList.add(getString(R.string.page_before) + (i + 1) + getString(R.string.page_after));
            }
            pageAdapter = new PopupPageListAdapter(this, stringArrayList);
            listView1.setAdapter(pageAdapter);

            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(this.getClass().getSimpleName(), "listView1.onItemClick: Page " + (position + 1));
                    getComments(getIntent().getLongExtra("postId", 0L), position);
                    paginationPopup.dismiss();
                    paginationPopup = null;
                }
            });
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "initPaginationPopup: failure", e);
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
                    initPaginationPopup();
                }
            });
        }

        if (maxPage == 1) {
            backButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            backButton.setEnabled(false);
            nextButton.setEnabled(false);
        } else {
            if (page <= 1) {
                backButton.setEnabled(false);
                backButton.setBackgroundResource(R.drawable.rounded_corner_button_gray);
                backButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_back_gray));
            } else {
                backButton.setEnabled(true);
                backButton.setBackgroundResource(R.drawable.rounded_corner_button_pink);
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
                nextButton.setBackgroundResource(R.drawable.rounded_corner_button_gray);
                nextButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_next_gray));
            } else {
                nextButton.setEnabled(true);
                nextButton.setBackgroundResource(R.drawable.rounded_corner_button_pink);
                nextButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_next));

                final int pageOffset = page - 1;
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getComments(getIntent().getLongExtra("postId", 0L), pageOffset + 1);
                    }
                });
            }

            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    private int getMaxPage() {
        if (noOfComments == 0) {
            return 1;
        }
        return (int)Math.ceil((double)noOfComments / (double)DefaultValues.DEFAULT_PAGINATION_COUNT);
    }

    private void bookmark(Long postId) {
        AppController.getApi().setBookmark(postId, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(DetailActivity.class.getSimpleName(), "bookmark: failure", error);
            }
        });
    }

    private void unbookmark(Long postId) {
        AppController.getApi().setUnBookmark(postId, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bookmarkAction.setImageResource(R.drawable.ic_bookmark);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(DetailActivity.class.getSimpleName(), "unbookmark: failure", error);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // comment out more options for now...
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.a:
                Log.d(this.getClass().getSimpleName(), "onOptionsItemSelected: "+item.getItemId());
                return true;
            case R.id.b:
                Log.d(this.getClass().getSimpleName(), "onOptionsItemSelected: "+item.getItemId());
                return true;
            default:
                return false;
        }
    }

    private void getComments(Long postID, final int offset) {
        ViewUtil.showSpinner(this);

        AppController.getApi().getComments(postID,offset,AppController.getInstance().getSessionId(),new Callback<List<CommunityPostCommentVM>>(){
            @Override
            public void success(List<CommunityPostCommentVM> commentVMs, Response response) {
                communityItems.clear();
                List<CommunityPostCommentVM> communityPostCommentVMs = new ArrayList<CommunityPostCommentVM>();
                if (offset == 0) {   // insert new_post itself for first page only
                    postVm.imageLoaded = false;
                    communityPostCommentVMs.add(postVm);
                }
                communityPostCommentVMs.addAll(commentVMs);

                setPageButtons(offset + 1);     // set page before adapter as it takes in curPage
                listAdapter = new DetailListAdapter(DetailActivity.this, communityPostCommentVMs, curPage);
                listView.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();

                ViewUtil.stopSpinner(DetailActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(DetailActivity.this);
                Log.e(DetailActivity.class.getSimpleName(), "getComments: failure", error);
            }
        });
    }

    private void initEmoticonPopup() {
        mainFrameLayout.getForeground().setAlpha(20);
        mainFrameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) DetailActivity.this
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
                    ImageMapping.insertEmoticon(emoticonVMList.get(i), commentEditText);
                    emoPopup.dismiss();
                    emoPopup = null;
                    ViewUtil.popupInputMethodWindow(DetailActivity.this);
                }
            });
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "initEmoticonPopup: failure", e);
        }
    }

    private void reset() {
        if (commentPopup != null) {
            commentPopup.dismiss();
            commentPopup = null;
        }
        if (emoPopup != null) {
            emoPopup.dismiss();
            emoPopup = null;
        }
        if (paginationPopup != null) {
            paginationPopup.dismiss();
            paginationPopup = null;
        }
        resetCommentImages();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        reset();
    }
}