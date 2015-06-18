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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import miniBean.R;
import miniBean.adapter.EmoticonListAdapter;
import miniBean.adapter.MessageListAdapter;
import miniBean.app.AppController;
import miniBean.app.EmoticonCache;
import miniBean.app.TrackedFragmentActivity;
import miniBean.util.ActivityUtil;
import miniBean.util.DefaultValues;
import miniBean.util.EmoticonUtil;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.EmoticonVM;
import miniBean.viewmodel.MessagePostVM;
import miniBean.viewmodel.MessageVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

public class MessageDetailActivity extends TrackedFragmentActivity {

    private TextView commentEdit;
    private FrameLayout mainFrameLayout;
    private EditText commentEditText;
    private PopupWindow commentPopup, paginationPopup, emoPopup;
    private ActivityUtil activityUtil;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;

    private List<EmoticonVM> emoticonVMList = new ArrayList<>();
    private List<ImageView> commentImages = new ArrayList<>();
    private List<File> photos = new ArrayList<>();
    private EmoticonListAdapter emoticonListAdapter;

    private TextView commentPostButton,title;
    private ImageView commentBrowseButton, commentCancelButton, commentEmoImage;
    private List<MessageVM> messageVMList;
    private MessageListAdapter adapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_detail_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.message_detail_actionbar);
        activityUtil = new ActivityUtil(this);

        commentEdit = (TextView) findViewById(R.id.commentEdit);
        mainFrameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout1);

        listView= (ListView)findViewById(R.id.list_view_messages);
        title= (TextView) findViewById(R.id.title);

        System.out.println("inside message fragment:::::");

        messageVMList=new ArrayList<>();

        title.setText(getIntent().getStringExtra("user_name"));

        getMessages(getIntent().getLongExtra("cid",0l));

        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCommentPopup();
            }
        });

    }

    private void initCommentPopup() {
        mainFrameLayout.getForeground().setAlpha(20);
        mainFrameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

        try {
            LayoutInflater inflater = (LayoutInflater) MessageDetailActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View layout = inflater.inflate(R.layout.comment_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            if (commentPopup == null) {
                commentPopup = new PopupWindow(
                        layout,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, //activityUtil.getRealDimension(DefaultValues.COMMENT_POPUP_HEIGHT),
                        true);
            }

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

            commentEditText = (EditText) layout.findViewById(R.id.commentEditText);
            commentEditText.setLongClickable(true);

            // NOTE: UGLY WORKAROUND or pasting text to comment edit!!!
            commentEditText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(MessageDetailActivity.this.getClass().getSimpleName(), "onLongClick");
                    startActionMode(new ActionMode.Callback() {
                        final int PASTE_MENU_ITEM_ID = 0;

                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            Log.d(MessageDetailActivity.this.getClass().getSimpleName(), "onPrepareActionMode");
                            return true;
                        }

                        public void onDestroyActionMode(ActionMode mode) {
                        }

                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            Log.d(MessageDetailActivity.this.getClass().getSimpleName(), "onCreateActionMode: menu size="+menu.size());
                            menu.add(0,PASTE_MENU_ITEM_ID,0,"Paste");
                            menu.setQwertyMode(false);
                            return true;
                        }

                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            Log.d(MessageDetailActivity.this.getClass().getSimpleName(), "onActionItemClicked: item clicked="+item.getItemId()+" title="+item.getTitle());
                            switch(item.getItemId()) {
                                case PASTE_MENU_ITEM_ID:
                                    final ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                                    if (clipBoard != null && clipBoard.getPrimaryClip() != null && clipBoard.getPrimaryClip().getItemAt(0) != null) {
                                        String paste = clipBoard.getPrimaryClip().getItemAt(0).getText().toString();
                                        commentEditText.getText().insert(commentEditText.getSelectionStart(), paste);
                                    }
                            }

                            mode.finish();

                            // popup again
                            commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
                            activityUtil.popupInputMethodWindow();
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

            activityUtil.popupInputMethodWindow();

            commentPostButton = (TextView) layout.findViewById(R.id.postButton);
            commentPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("clicked:::::::::");
                    doMessage();
                }
            });

            commentCancelButton = (ImageView) layout.findViewById(R.id.cancelButton);
            commentCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentPopup.dismiss();
                }
            });

            commentBrowseButton = (ImageView) layout.findViewById(R.id.browseImage);
            commentBrowseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (photos.size() == DefaultValues.MAX_COMMENT_IMAGES) {
                        Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.comment_max_images), Toast.LENGTH_SHORT).show();
                    } else {
                        ImageUtil.openPhotoPicker(MessageDetailActivity.this);
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

            if (emoticonVMList.isEmpty() && EmoticonCache.getEmoticons().isEmpty()) {
                EmoticonCache.refresh();
            }

            //Log.d(this.getClass().getSimpleName(), "initCommentPopup: " + selectedImagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeCommentImage() {
        if (photos.size() > 0) {
            int toRemove = photos.size()-1;
            commentImages.get(toRemove).setImageDrawable(null);
            photos.remove(toRemove);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageUtil.SELECT_PICTURE && resultCode == RESULT_OK &&
                data != null && photos.size() < DefaultValues.MAX_COMMENT_IMAGES) {

            selectedImageUri = data.getData();
            selectedImagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);

            String path = selectedImageUri.getPath();
            Log.d(this.getClass().getSimpleName(), "onActivityResult: selectedImageUri=" + path + " selectedImagePath=" + selectedImagePath);

            Bitmap bitmap = ImageUtil.resizeAsPreviewThumbnail(selectedImagePath);
            if (bitmap != null) {
                setCommentImage(bitmap);
            } else {
                Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.photo_not_found), Toast.LENGTH_SHORT).show();
        }

        // pop back soft keyboard
        activityUtil.popupInputMethodWindow();
    }

    private void setCommentImage(Bitmap bitmap) {
        ImageView commentImage = commentImages.get(photos.size());
        commentImage.setImageDrawable(new BitmapDrawable(this.getResources(), bitmap));
        commentImage.setVisibility(View.VISIBLE);
        File photo = new File(selectedImagePath);
        photos.add(photo);
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
    private void initEmoticonPopup() {
        mainFrameLayout.getForeground().setAlpha(20);
        mainFrameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) MessageDetailActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.emoticon_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            // hide soft keyboard when select emoticon
            activityUtil.hideInputMethodWindow(layout);

            emoPopup = new PopupWindow(layout,
                    activityUtil.getRealDimension(DefaultValues.EMOTICON_POPUP_WIDTH, this.getResources()),
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);

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
                    EmoticonUtil.insertEmoticon(emoticonVMList.get(i), commentEditText);
                    emoPopup.dismiss();
                    activityUtil.popupInputMethodWindow();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doMessage() {

        String comment = commentEditText.getText().toString().trim();
        if (StringUtils.isEmpty(comment)) {
            Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.invalid_comment_body_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(this.getClass().getSimpleName(), "doMessage: reciverId=" + getIntent().getLongExtra("uid", 0L) + " comment=" + comment.substring(0, Math.min(5, comment.length())));
        AppController.getApi().sendMessage(new MessagePostVM(getIntent().getLongExtra("uid", 0l), comment, true), AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response1) {
                System.out.println("doMessaged called called :::::"+response1.getUrl());
                String responseVm = "";
                TypedInput body = response.getBody();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("ResponseVm::::" + line);
                        responseVm = responseVm + line;
                    }

                    JSONObject obj = new JSONObject(responseVm);

                    JSONArray userGroupArray = obj.getJSONArray("message");


                        JSONObject object1 = userGroupArray.getJSONObject(0);
                        MessageVM vm = new MessageVM();
                    System.out.println("iddddddddd:::::"+object1.getLong("id"));
                        uploadPhotos(object1.getLong("id"));
                /*System.out.println("message id::"+messageVM.getId());
                uploadPhotos(messageVM.getId());*/
                        // getMessages(messageVM.getSuid());
                        commentPopup.dismiss();
                        getMessages(getIntent().getLongExtra("cid", 0l));


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

            private void uploadPhotos(long commentId) {
                System.out.println("upload photo called::::"+photos.size());
                for (File photo : photos) {
                    photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
                    TypedFile typedFile = new TypedFile("application/octet-stream", photo);
                    AppController.getApi().uploadMessagePhoto(AppController.getInstance().getSessionId(),commentId, typedFile, new Callback<Response>() {
                        @Override
                        public void success(Response array, Response response) {

                            System.out.println("upload success::::");


                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            retrofitError.printStackTrace(); //to see if you have errors
                        }
                    });
                }
            }


            private void getMessages(Long id) {
                AppController.getApi().getMessages(id, 0l, AppController.getInstance().getSessionId(), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response1) {
                        System.out.println("getMEssaged called called :::::"+response1.getUrl());
                        String responseVm = "";
                        TypedInput body = response.getBody();
                        messageVMList.clear();
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.out.println("ResponseVm::::" + line);
                                responseVm = responseVm + line;
                            }

                            JSONObject obj = new JSONObject(responseVm);

                            JSONArray userGroupArray = obj.getJSONArray("message");

                            for (int i = 0; i < userGroupArray.length(); i++) {
                                JSONObject object1 = userGroupArray.getJSONObject(i);
                                MessageVM vm = new MessageVM();
                                vm.setId(object1.getLong("id"));
                                vm.setHasImage(object1.getBoolean("hasImage"));
                                vm.setSnm(object1.getString("snm"));
                                vm.setSuid(object1.getLong("suid"));
                                vm.setCd(object1.getLong("cd"));
                                vm.setTxt(object1.getString("txt"));

                                if(!object1.isNull("imgs")) {
                                    System.out.println("fill image:::"+object1.getLong("imgs"));
                                    vm.setImgs(object1.getLong("imgs"));
                                }
                                messageVMList.add(vm);
                        /*tb_user_group group = new tb_user_group();
                        group.setId((long)object1.getInt("id"));
                        group.setHashcode(object1.getString("hashcode"));
                        group.setUsg_code(object1.getString("usg_code"));
                        group.setUsg_name(object1.getString("usg_name"));
                        group.setOptid(object1.getInt("optid"));
                        groups.add(group);*/
                            }


                            //messageVMList.addAll(messageVMs);

                            Collections.sort(messageVMList, new Comparator<MessageVM>() {
                                public int compare(MessageVM m1, MessageVM m2) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(m1.getCd());

                                    Calendar calendar1 = Calendar.getInstance();
                                    calendar1.setTimeInMillis(m1.getCd());

                                    //Date date1=calendar.getTimeInMillis();

                                    Date date2=calendar1.getTime();
                                   return Long.compare(m1.getCd(), m2.getCd());
                                    //return calendar.getTimeInMillis().compare(calendar1.getTimeInMillis());
                                }
                            });


                            adapter = new MessageListAdapter(MessageDetailActivity.this, messageVMList);
                            listView.setAdapter(adapter);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // System.out.println("url::::"+error.getResponse().getUrl());
                        error.printStackTrace();
                    }
                });
       }
  }






