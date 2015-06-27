package miniBean.activity;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Button;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import miniBean.R;
import miniBean.adapter.EmoticonListAdapter;
import miniBean.adapter.MessageListAdapter;
import miniBean.app.AppController;
import miniBean.app.BroadcastService;
import miniBean.app.EmoticonCache;
import miniBean.app.TrackedFragmentActivity;
import miniBean.util.ActivityUtil;
import miniBean.util.DefaultValues;
import miniBean.util.EmoticonUtil;
import miniBean.util.ImageUtil;
import miniBean.util.ViewUtil;
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
    private PopupWindow commentPopup, emoPopup;
    private ActivityUtil activityUtil;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;

    private List<EmoticonVM> emoticonVMList = new ArrayList<>();
    private List<ImageView> commentImages = new ArrayList<>();
    private List<File> photos = new ArrayList<>();
    private EmoticonListAdapter emoticonListAdapter;

    private TextView commentPostButton,title;
    private ImageView backImage, commentBrowseButton, commentCancelButton, commentEmoImage, profileButton;
    private List<MessageVM> messageVMList;
    private MessageListAdapter adapter;
    private ListView listView;
    private Intent intent;
    private Button loadMessage;
    private Long offset = 1l;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("in receiver:::");
            messageVMList.clear();
            messageVMList.addAll(AppController.getInstance().messageVMList);
            System.out.println("in rec size::"+messageVMList.size());
            adapter.notifyDataSetChanged();
            // adapter = new MessageListAdapter(MessageDetailActivity.this, messageVMList);
            //listView.setAdapter(adapter);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_detail_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.message_detail_actionbar);
        activityUtil = new ActivityUtil(this);

        commentEdit = (TextView) findViewById(R.id.commentEdit);
        mainFrameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout);
        loadMessage= (Button) findViewById(R.id.loadButton);
        profileButton= (ImageView) findViewById(R.id.profileButton);

        intent = new Intent(this, BroadcastService.class);

        listView = (ListView)findViewById(R.id.list_view_messages);
        title = (TextView) findViewById(R.id.title);

        ViewUtil.showSpinner(this);

        messageVMList = new ArrayList<>();

        title.setText(getIntent().getStringExtra("user_name"));

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MessageDetailActivity.this, UserProfileActivity.class);
                i.putExtra("oid", getIntent().getLongExtra("uid",0l));
                i.putExtra("name", getIntent().getStringExtra("user_name"));
                startActivity(i);

            }
        });

            getMessages(getIntent().getLongExtra("cid", 0l), 0l);
        loadMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("offset::::::" + offset);
                loadMoreMessages(getIntent().getLongExtra("cid",0l),offset);
                offset++;
            }
        });



        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCommentPopup();
            }
        });

        backImage = (ImageView) this.findViewById(R.id.backAction);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                    doMessage();
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
                    if (photos.size() == DefaultValues.MAX_MESSAGE_IMAGES) {
                        Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.pm_max_images), Toast.LENGTH_SHORT).show();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageUtil.SELECT_PICTURE && resultCode == RESULT_OK &&
                data != null && photos.size() < DefaultValues.MAX_MESSAGE_IMAGES) {

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

            if (emoPopup == null) {
                emoPopup = new PopupWindow(
                        layout,
                        activityUtil.getRealDimension(DefaultValues.EMOTICON_POPUP_WIDTH, this.getResources()),
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
                    EmoticonUtil.insertEmoticon(emoticonVMList.get(i), commentEditText);
                    emoPopup.dismiss();
                    emoPopup = null;
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

        Log.d(this.getClass().getSimpleName(), "doMessage: receiverId=" + getIntent().getLongExtra("uid", 0L) + " message=" + comment.substring(0, Math.min(5, comment.length())));
        AppController.getApi().sendMessage(new MessagePostVM(getIntent().getLongExtra("uid", 0l), comment, true), AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response1) {
                String responseVm = "";
                TypedInput body = response.getBody();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseVm = responseVm + line;
                    }

                    JSONObject obj = new JSONObject(responseVm);
                    JSONArray userGroupArray = obj.getJSONArray("message");
                    JSONObject object1 = userGroupArray.getJSONObject(0);
                    uploadPhotos(object1.getLong("id"));
                    getMessages(getIntent().getLongExtra("cid", 0l),0l);

                    reset();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(MessageDetailActivity.this.getClass().getSimpleName(), "doMessage.api.sendMessage: failed with error", error);
                Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.pm_send_failed), Toast.LENGTH_SHORT).show();
                reset();
            }
        });
    }

    private void uploadPhotos(long commentId) {
        for (File photo : photos) {
            photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
            TypedFile typedFile = new TypedFile("application/octet-stream", photo);
            AppController.getApi().uploadMessagePhoto(AppController.getInstance().getSessionId(),commentId, typedFile, new Callback<Response>() {
                @Override
                public void success(Response array, Response response) {
                    getMessages(getIntent().getLongExtra("cid", 0l),0l);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(MessageDetailActivity.class.getSimpleName(), "uploadPhotos.api.uploadMessagePhoto: failure", error);
                }
            });
        }
    }

    private void getMessages(Long id,Long offset) {
        AppController.getApi().getMessages(id, offset, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response1) {
                String responseVm = "";
                TypedInput body = response.getBody();
                messageVMList.clear();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseVm = responseVm + line;
                    }

                    JSONObject obj = new JSONObject(responseVm);

                    JSONArray userGroupArray = obj.getJSONArray("message");
                    System.out.println("size message::::" + userGroupArray.length());
                    for (int i = 0; i < userGroupArray.length(); i++) {
                        JSONObject object1 = userGroupArray.getJSONObject(i);
                        MessageVM vm = new MessageVM();
                        vm.setId(object1.getLong("id"));
                        vm.setHasImage(object1.getBoolean("hasImage"));
                        vm.setSnm(object1.getString("snm"));
                        vm.setSuid(object1.getLong("suid"));
                        vm.setCd(object1.getLong("cd"));
                        vm.setTxt(object1.getString("txt"));

                        if (!object1.isNull("imgs")) {
                            System.out.println("fill image:::" + object1.getLong("imgs"));
                            vm.setImgs(object1.getLong("imgs"));
                        }
                        messageVMList.add(vm);
                    }

                    Collections.sort(messageVMList, new Comparator<MessageVM>() {
                        public int compare(MessageVM m1, MessageVM m2) {
                            //return Long.compare(m1.getCd(), m2.getCd());
                            return ((Long)m1.getCd()).compareTo(m2.getCd());
                        }
                    });

                    adapter = new MessageListAdapter(MessageDetailActivity.this, messageVMList);
                    listView.setAdapter(adapter);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ViewUtil.stopSpinner(MessageDetailActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(MessageDetailActivity.this);
                Log.e(MessageDetailActivity.class.getSimpleName(), "getMessages: failure", error);
            }
        });
    }

    private void loadMoreMessages(Long id,Long offset) {
        AppController.getApi().getMessages(id, offset, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response1) {
                String responseVm = "";
                TypedInput body = response.getBody();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseVm = responseVm + line;
                    }

                    JSONObject obj = new JSONObject(responseVm);

                    JSONArray userGroupArray = obj.getJSONArray("message");
                    System.out.println("size message::::"+userGroupArray.length());
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
                    }

                    Collections.sort(messageVMList, new Comparator<MessageVM>() {
                        public int compare(MessageVM m1, MessageVM m2) {
                            //return Long.compare(m1.getCd(), m2.getCd());
                            return ((Long)m1.getCd()).compareTo(m2.getCd());
                        }
                    });

                    adapter.notifyDataSetChanged();
                   // adapter = new MessageListAdapter(MessageDetailActivity.this, messageVMList);
                   // listView.setAdapter(adapter);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ViewUtil.stopSpinner(MessageDetailActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(MessageDetailActivity.this);
                Log.e(MessageDetailActivity.class.getSimpleName(), "loadMoreMessages.api.getMessages: failure", error);
            }
        });
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
        resetCommentImages();
    }

    @Override
    public void onResume() {
        super.onResume();

        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }
}






