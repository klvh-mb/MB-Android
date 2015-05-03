package miniBean.fragment;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.lang.reflect.Field;

import miniBean.R;
import miniBean.activity.EditProfileActivity;
import miniBean.activity.MyProfileActionActivity;
import miniBean.activity.NewsfeedActivity;
import miniBean.app.AppController;
import miniBean.app.UserInfoCache;
import miniBean.util.AnimationUtil;
import miniBean.util.DefaultValues;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.BookmarkSummaryVM;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getName();
    private ImageView userCoverPic, userPic, editCoverImage;
    private ProgressBar spinner;
    private TextView questionsCount, answersCount, bookmarksCount, userName;
    private LinearLayout questionMenu, answerMenu, bookmarksMenu, settingsMenu, userInfoLayout;
    private Long userId;
    private Boolean isPhoto = false;
    private final Integer SELECT_PICTURE = 1;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;
    private boolean coverPhotoClicked = false, profilePhotoClicked=false;
    private Button editButton;

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
        spinner = (ProgressBar) view.findViewById(R.id.spinner);
        questionMenu = (LinearLayout) view.findViewById(R.id.menuQuestion);
        answerMenu = (LinearLayout) view.findViewById(R.id.menuAnswer);
        bookmarksMenu = (LinearLayout) view.findViewById(R.id.menuBookmarks);
        settingsMenu = (LinearLayout) view.findViewById(R.id.menuSettings);
        userInfoLayout = (LinearLayout) view.findViewById(R.id.userInfoLayout);
        editButton= (Button) view.findViewById(R.id.editButton);
        userInfoLayout.setVisibility(View.GONE);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
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
                intent.putExtra("id",userId);
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
        getBookmarkSummary();

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == SELECT_PICTURE) {
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
                    changeCoverPhoto(userId);
                    coverPhotoClicked = false;
                } else if (profilePhotoClicked) {
                    userPic.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
                    userPic.setVisibility(View.VISIBLE);
                    changeProfilePhoto(userId);
                    profilePhotoClicked = false;
                }
            }
        }
    }

    private void getUserInfo() {
        AnimationUtil.show(spinner);

        UserVM user = UserInfoCache.getUser();

        //Log.d(ProfileFragment.this.getClass().getSimpleName(), "questionsCount - "+user.getQuestionsCount());
        //Log.d(ProfileFragment.this.getClass().getSimpleName(), "answersCount - "+user.getAnswersCount());
        //Log.d(ProfileFragment.this.getClass().getSimpleName(), "enableSignInForToday - "+user.isEnableSignInForToday());

        userId = user.getId();
        userName.setText(user.getDisplayName());
        questionsCount.setText(user.getQuestionsCount()+"");
        answersCount.setText(user.getAnswersCount()+"");

        ImageUtil.displayThumbnailProfileImage(userId, userPic);
        ImageUtil.displayCoverImage(userId, userCoverPic, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                AnimationUtil.show(spinner);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                AnimationUtil.cancel(spinner);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                AnimationUtil.cancel(spinner);
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

    private void changeCoverPhoto(final long id){
        Log.d(this.getClass().getSimpleName(), "changeCoverPhoto: Id="+id);
        File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);

        AnimationUtil.show(spinner);

        ImageUtil.clearCoverImageCache(id);
        AppController.getApi().uploadCoverPhoto(typedFile,AppController.getInstance().getSessionId(),new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        ImageUtil.displayCoverImage(id, userCoverPic, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                AnimationUtil.show(spinner);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                AnimationUtil.cancel(spinner);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                AnimationUtil.cancel(spinner);
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

    private void changeProfilePhoto(final long id) {
        Log.d(this.getClass().getSimpleName(), "changeProfilePhoto: Id=" + id);
        File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);

        AnimationUtil.show(spinner);

        ImageUtil.clearProfileImageCache(id);
        AppController.getApi().uploadProfilePhoto(typedFile,AppController.getInstance().getSessionId(),new Callback<Response>(){
            @Override
            public void success(Response response, Response response2) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        ImageUtil.displayThumbnailProfileImage(id, userPic, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                AnimationUtil.show(spinner);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                AnimationUtil.cancel(spinner);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                AnimationUtil.cancel(spinner);
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
