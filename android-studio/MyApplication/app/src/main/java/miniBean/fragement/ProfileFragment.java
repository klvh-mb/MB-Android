package miniBean.fragement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.lang.reflect.Field;

import miniBean.R;
import miniBean.activity.NewsfeedActivity;
import miniBean.app.AppController;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.BookmarkSummaryVM;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getName();
    private ImageView userCoverPic, userPic;
    private ProgressBar spinner;
    private TextView questionsCount, answersCount, bookmarksCount, userName;
    private LinearLayout questionMenu, answerMenu, bookmarksMenu;
    private Long userId;
    private Boolean isPhoto = false;
    private final Integer SELECT_PICTURE = 1;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;
    private boolean usercoverpicClicked=false,userpicClicked=false;
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
        spinner = (ProgressBar) view.findViewById(R.id.imageLoader);
        questionMenu = (LinearLayout) view.findViewById(R.id.menuQuestion);
        answerMenu = (LinearLayout) view.findViewById(R.id.menuAnswer);
        bookmarksMenu = (LinearLayout) view.findViewById(R.id.menuBookmarks);

        userCoverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                isPhoto = true;
                usercoverpicClicked=true;
            }
        });


        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                isPhoto = true;
                userpicClicked=true;
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
                if(usercoverpicClicked){
                    userCoverPic.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
                    userCoverPic.setVisibility(View.VISIBLE);
                    changeCoverPic(userId.toString());
                }else if(userpicClicked){
                    userPic.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
                    userPic.setVisibility(View.VISIBLE);
                    changeProfilePic(userId.toString());
                }

            }


        }
    }

    private void getUserInfo() {
        AppController.api.getUserInfo(AppController.getInstance().getSessionId(), new Callback<UserVM>() {
            @Override
            public void success(UserVM user, retrofit.client.Response response) {
                Log.d(ProfileFragment.this.getClass().getSimpleName(), "questionsCount - "+user.getQuestionsCount());
                Log.d(ProfileFragment.this.getClass().getSimpleName(), "answersCount - "+user.getAnswersCount());
                Log.d(ProfileFragment.this.getClass().getSimpleName(), "enableSignInForToday - "+user.isEnableSignInForToday());

                userId = user.getId();
                userName.setText(user.getDisplayName());
                questionsCount.setText(user.getQuestionsCount()+"");
                answersCount.setText(user.getAnswersCount()+"");

                ImageUtil.displayThumbnailProfileImage(user.getId(), userPic);
                ImageUtil.displayCoverImage(user.getId(), userCoverPic, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        spinner.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        spinner.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    private void getBookmarkSummary() {
        AppController.api.getBookmarkSummary(AppController.getInstance().getSessionId(), new Callback<BookmarkSummaryVM>() {
            @Override
            public void success(BookmarkSummaryVM bookmarkSummary, retrofit.client.Response response) {
                Log.d(ProfileFragment.this.getClass().getSimpleName(), "bookmarksCount - "+bookmarkSummary.getQc());
                bookmarksCount.setText(bookmarkSummary.getQc()+"");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
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
    private void changeCoverPic(final String id){
        System.out.println("change cover:::::");
        File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);

        AppController.api.uploadCoverPhoto(id,typedFile,AppController.getInstance().getSessionId(),new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                ImageUtil.displayCoverImage(Long.parseLong(id), userCoverPic, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        spinner.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        spinner.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                    error.printStackTrace();
            }
        });

    }
  private void changeProfilePic(final String id)
  {
      System.out.println("change profile:::::");
      File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
      TypedFile typedFile = new TypedFile("application/octet-stream", photo);

      AppController.api.uploadProfilePhoto(id,typedFile,AppController.getInstance().getSessionId(),new Callback<Response>(){

          @Override
          public void success(Response response, Response response2) {
              ImageUtil.displayThumbnailProfileImage(Long.parseLong(id), userPic);
          }

          @Override
          public void failure(RetrofitError error) {
                        error.printStackTrace();
          }
      });
  }
}