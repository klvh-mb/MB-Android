package miniBean.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.lang.reflect.Field;

import miniBean.R;
import miniBean.activity.EditProfileActivity;
import miniBean.activity.GameActivity;
import miniBean.activity.MyProfileActionActivity;
import miniBean.activity.NewsfeedActivity;
import miniBean.app.AppController;
import miniBean.app.TrackedFragment;
import miniBean.app.UserInfoCache;
import miniBean.util.DefaultValues;
import miniBean.util.GameConstants;
import miniBean.util.ImageUtil;
import miniBean.util.SharedPreferencesUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.BookmarkSummaryVM;
import miniBean.viewmodel.GameAccountVM;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class ProfileFragment extends TrackedFragment {

    private static final String TAG = ProfileFragment.class.getName();
    private ImageView userCoverPic, userPic, editCoverImage;
    private TextView questionsCount, answersCount, bookmarksCount, userName;
    private LinearLayout questionMenu, answerMenu, bookmarksMenu, settingsMenu, userInfoLayout;
    private Long userId;
    private Boolean isPhoto = false;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;
    private boolean coverPhotoClicked = false, profilePhotoClicked = false;
    private boolean hasProfilePic = false;

    private Button editButton, messageButton;
    private LinearLayout gameLayout;
    private TextView pointsText;

    private FrameLayout uploadProfilePicTipsLayout;
    private TextView tipsDescText, tipsPointsText, tipsEndText;
    private ImageView cancelTipsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        userName = (TextView) view.findViewById(R.id.usernameText);
        questionsCount = (TextView) view.findViewById(R.id.questionsCount);
        answersCount = (TextView) view.findViewById(R.id.answersCount);
        bookmarksCount = (TextView) view.findViewById(R.id.bookmarksCount);
        userCoverPic = (ImageView) view.findViewById(R.id.userCoverPic);
        userPic = (ImageView) view.findViewById(R.id.userImage);
        editCoverImage = (ImageView) view.findViewById(R.id.editCoverImage);
        questionMenu = (LinearLayout) view.findViewById(R.id.menuQuestion);
        answerMenu = (LinearLayout) view.findViewById(R.id.menuAnswer);
        bookmarksMenu = (LinearLayout) view.findViewById(R.id.menuBookmarks);
        settingsMenu = (LinearLayout) view.findViewById(R.id.menuSettings);
        userInfoLayout = (LinearLayout) view.findViewById(R.id.userInfoLayout);
        editButton = (Button) view.findViewById(R.id.editButton);
        messageButton = (Button) view.findViewById(R.id.messageButton);
        gameLayout = (LinearLayout) view.findViewById(R.id.gameLayout);
        pointsText = (TextView) view.findViewById(R.id.pointsText);

        uploadProfilePicTipsLayout = (FrameLayout) view.findViewById(R.id.uploadProfilePicTipsLayout);
        tipsDescText = (TextView) view.findViewById(R.id.tipsDescText);
        tipsPointsText = (TextView) view.findViewById(R.id.tipsPointsText);
        tipsEndText = (TextView) view.findViewById(R.id.tipsEndText);
        cancelTipsButton = (ImageView) view.findViewById(R.id.cancelTipsButton);

        messageButton.setVisibility(View.GONE);
        userInfoLayout.setVisibility(View.GONE);

        gameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                startActivityForResult(intent, ViewUtil.START_ACTIVITY_REQUEST_CODE);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivityForResult(intent, ViewUtil.START_ACTIVITY_REQUEST_CODE);
            }
        });

        editCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.openPhotoPicker(ProfileFragment.this.getActivity(), getString(R.string.edit_cover_photo));
                isPhoto = true;
                coverPhotoClicked = true;
            }
        });

        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.openPhotoPicker(ProfileFragment.this.getActivity(), getString(R.string.edit_user_photo));
                isPhoto = true;
                profilePhotoClicked = true;
            }
        });

        questionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsfeedActivity.class);
                intent.putExtra("id", userId);
                intent.putExtra("key","question");
                startActivity(intent);
            }
        });

        answerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsfeedActivity.class);
                intent.putExtra("id",userId);
                intent.putExtra("key","answer");
                startActivity(intent);
            }
        });

        bookmarksMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsfeedActivity.class);
                intent.putExtra("id",userId);
                intent.putExtra("key","bookmark");
                startActivity(intent);
            }
        });

        settingsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActionActivity.class);
                intent.putExtra("id",userId);
                intent.putExtra("key","settings");
                startActivity(intent);
            }
        });

        getUserInfo();
        getGameAccount();
        getBookmarkSummary();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ViewUtil.SELECT_PICTURE_REQUEST_CODE) {
            if (data == null)
                return;

            selectedImageUri = data.getData();
            selectedImagePath = ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri);
            String path = selectedImageUri.getPath();

            Log.d(this.getClass().getSimpleName(), "onActivityResult: selectedImageUri="+path+" selectedImagePath="+selectedImagePath);
            Bitmap bp = ImageUtil.resizeAsPreviewThumbnail(selectedImagePath);
            if (bp != null) {
                if (coverPhotoClicked) {
                    userCoverPic.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
                    userCoverPic.setVisibility(View.VISIBLE);
                    uploadCoverPhoto(userId);
                    coverPhotoClicked = false;
                } else if (profilePhotoClicked) {
                    userPic.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
                    userPic.setVisibility(View.VISIBLE);
                    uploadProfilePhoto(userId);
                    profilePhotoClicked = false;
                }
            }
        }

        if(requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            boolean refresh = data.getBooleanExtra(ViewUtil.INTENT_VALUE_REFRESH, false);
            if (refresh) {
                getUserInfo();
                getGameAccount();

                // refresh parent activity
                Intent intent = new Intent();
                intent.putExtra(ViewUtil.INTENT_VALUE_REFRESH, true);
                getActivity().setResult(Activity.RESULT_OK, intent);
            }
        }
    }

    private void getUserInfo() {
        ViewUtil.showSpinner(getActivity());

        UserVM user = UserInfoCache.getUser();

        //Log.d(ProfileFragment.this.getClass().getSimpleName(), "questionsCount - "+user.getQuestionsCount());
        //Log.d(ProfileFragment.this.getClass().getSimpleName(), "answersCount - "+user.getAnswersCount());
        //Log.d(ProfileFragment.this.getClass().getSimpleName(), "enableSignInForToday - "+user.isEnableSignInForToday());

        userId = user.getId();
        userName.setText(user.getDisplayName());
        questionsCount.setText(user.getQuestionsCount()+"");
        answersCount.setText(user.getAnswersCount()+"");

        ImageUtil.displayProfileImage(userId, userPic);
        ImageUtil.displayCoverImage(userId, userCoverPic, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ViewUtil.showSpinner(getActivity());
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ViewUtil.stopSpinner(getActivity());
            }
        });
    }

    private void getGameAccount() {
        ViewUtil.showSpinner(getActivity());
        AppController.getApi().getGameAccount(AppController.getInstance().getSessionId(), new Callback<GameAccountVM>() {
            @Override
            public void success(final GameAccountVM gameAccountVM, Response response) {
                pointsText.setText(gameAccountVM.getGmpt() + "");
                hasProfilePic = gameAccountVM.hasProfilePic();
                if (hasProfilePic ||
                        SharedPreferencesUtil.getInstance().isScreenViewed(SharedPreferencesUtil.Screen.UPLOAD_PROFILE_PIC_TIPS)) {
                    uploadProfilePicTipsLayout.setVisibility(View.GONE);
                } else {
                    uploadProfilePicTipsLayout.setVisibility(View.VISIBLE);
                    tipsDescText.setText(getString(R.string.game_upload_profile_pic_title));
                    tipsPointsText.setText("+" + GameConstants.POINTS_UPLOAD_PROFILE_PHOTO);
                    cancelTipsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferencesUtil.getInstance().setScreenViewed(SharedPreferencesUtil.Screen.UPLOAD_PROFILE_PIC_TIPS);
                            uploadProfilePicTipsLayout.setVisibility(View.GONE);
                        }
                    });
                }
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameActivity.class.getSimpleName(), "getGameAccount: failure", error);
                ViewUtil.stopSpinner(getActivity());
            }
        });
    }

    private void getBookmarkSummary() {
        AppController.getApi().getBookmarkSummary(AppController.getInstance().getSessionId(), new Callback<BookmarkSummaryVM>() {
            @Override
            public void success(BookmarkSummaryVM bookmarkSummary, retrofit.client.Response response) {
                Log.d(ProfileFragment.this.getClass().getSimpleName(), "bookmarksCount - "+bookmarkSummary.getQc());
                bookmarksCount.setText(bookmarkSummary.getQc()+"");
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void uploadCoverPhoto(final long id){
        ViewUtil.showSpinner(getActivity());

        Log.d(this.getClass().getSimpleName(), "changeCoverPhoto: Id=" + id);

        ImageUtil.clearCoverImageCache(id);

        File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);
        AppController.getApi().uploadCoverPhoto(typedFile,AppController.getInstance().getSessionId(),new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        ImageUtil.displayCoverImage(id, userCoverPic, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                ViewUtil.showSpinner(getActivity());
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                ViewUtil.stopSpinner(getActivity());
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                ViewUtil.stopSpinner(getActivity());
                            }
                        });
                    }
                }, DefaultValues.DEFAULT_HANDLER_DELAY);
            }

            @Override
            public void failure(RetrofitError error) {
                    error.printStackTrace();
            }
        });
    }

    private void uploadProfilePhoto(final long id) {
        ViewUtil.showSpinner(getActivity());

        Log.d(this.getClass().getSimpleName(), "changeProfilePhoto: Id=" + id);

        ImageUtil.clearProfileImageCache(id);

        File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);
        AppController.getApi().uploadProfilePhoto(typedFile,AppController.getInstance().getSessionId(),new Callback<Response>(){
            @Override
            public void success(Response response, Response response2) {
                if (!hasProfilePic) {
                    hasProfilePic = true;
                    uploadProfilePicTipsLayout.setVisibility(View.GONE);
                    ViewUtil.alertGameStatus(getActivity(),
                            getActivity().getString(R.string.game_upload_profile_pic_title),
                            GameConstants.POINTS_UPLOAD_PROFILE_PHOTO);
                }

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        ImageUtil.displayProfileImage(id, userPic, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                ViewUtil.showSpinner(getActivity());
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                ViewUtil.stopSpinner(getActivity());
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                ViewUtil.stopSpinner(getActivity());
                            }
                        });
                    }
                }, DefaultValues.DEFAULT_HANDLER_DELAY);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
