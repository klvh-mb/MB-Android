package miniBean.fragement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class UserProfileFragment extends Fragment {

    private static final String TAG = UserProfileFragment.class.getName();
    private ImageView userCoverPic, userPic;
    private ProgressBar spinner;
    private TextView questionsCount, answersCount, bookmarksCount, userName;
    private LinearLayout questionMenu, answerMenu, bookmarksMenu;

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
        bookmarksMenu.setVisibility(View.GONE);

        questionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Bundle bundle = new Bundle();
                bundle.putLong("id",getArguments().getLong("oid"));
                bundle.putString("key","userquestion");

                NewsfeedListFragement fragment = new NewsfeedListFragement();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(UserProfileFragment.this);
                fragmentTransaction.replace(R.id.placeHolder, fragment);
                fragmentTransaction.commit();
                */

                Intent intent = new Intent(getActivity(), NewsfeedActivity.class);
                intent.putExtra("id",getArguments().getLong("oid"));
                intent.putExtra("key","userquestion");
                startActivity(intent);
            }
        });

        answerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Bundle bundle = new Bundle();
                bundle.putLong("id",getArguments().getLong("oid"));
                bundle.putString("key","useranswer");

                NewsfeedListFragement fragment = new NewsfeedListFragement();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(UserProfileFragment.this);
                fragmentTransaction.replace(R.id.placeHolder, fragment);
                fragmentTransaction.commit();
                */

                Intent intent = new Intent(getActivity(), NewsfeedActivity.class);
                intent.putExtra("id",getArguments().getLong("oid"));
                intent.putExtra("key","useranswer");
                startActivity(intent);
            }
        });

        getUserInfo();

        return view;
    }

    private void getUserInfo() {
        AppController.api.getUserInfo(AppController.getInstance().getSessionId(), new Callback<UserVM>() {
            @Override
            public void success(UserVM user, retrofit.client.Response response) {
                userName.setText(getArguments().getString("name"));
                questionsCount.setText(user.getQuestionsCount()+"");
                answersCount.setText(user.getAnswersCount()+"");

                AppController.getImageLoader().displayImage(getResources().getString(R.string.base_url) + "/image/get-cover-image-by-id/" + getArguments().getLong("oid"), userCoverPic, new SimpleImageLoadingListener() {
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

                ImageLoader.getInstance().displayImage(
                        getResources().getString(R.string.base_url) + "/image/get-profile-image-by-id/" + getArguments().getLong("oid"),
                        userPic,
                        AppController.ROUNDED_CORNERS_IMAGE_OPTIONS);
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