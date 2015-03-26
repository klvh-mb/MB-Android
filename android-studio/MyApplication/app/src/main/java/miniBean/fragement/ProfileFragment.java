package miniBean.fragement;

import android.content.Intent;
import android.graphics.Bitmap;
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

import java.lang.reflect.Field;

import miniBean.R;
import miniBean.activity.NewsfeedActivity;
import miniBean.app.AppController;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.BookmarkSummaryVM;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getName();
    private ImageView userCoverPic, userPic;
    private ProgressBar spinner;
    private TextView questionsCount, answersCount, bookmarksCount, userName;
    private LinearLayout questionMenu, answerMenu, bookmarksMenu;
    private Long userId;

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

        questionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsfeedActivity.class);
                intent.putExtra("id",userId);
                intent.putExtra("key","question");
                startActivity(intent);

                /*Bundle bundle=new Bundle();
                bundle.putString("id",userId.toString());
                bundle.putString("key","question");
                NewsFeedFragement fragment = new NewsFeedFragement();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(ProfileFragment.this);
                fragmentTransaction.replace(R.id.children_fragement, fragment);
                fragmentTransaction.commit();*/
            }
        });

        answerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsfeedActivity.class);
                intent.putExtra("id",userId);
                intent.putExtra("key","answer");
                startActivity(intent);

               /* Bundle bundle=new Bundle();
                bundle.putString("id",userId.toString());
                bundle.putString("key","answer");
                NewsFeedFragement fragment = new NewsFeedFragement();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(ProfileFragment.this);
                fragmentTransaction.replace(R.id.children_fragement, fragment);
                fragmentTransaction.commit();*/
            }
        });

        bookmarksMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsfeedActivity.class);
                intent.putExtra("id",userId);
                intent.putExtra("key","bookmark");
                startActivity(intent);

               /* Bundle bundle=new Bundle();
                bundle.putString("id",userId.toString());
                bundle.putString("key","bookmark");
                NewsFeedFragement fragment = new NewsFeedFragement();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(ProfileFragment.this);
                fragmentTransaction.replace(R.id.children_fragement, fragment);
                fragmentTransaction.commit();*/

            }
        });

        getUserInfo();
        getBookmarkSummary();

        return view;
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


}