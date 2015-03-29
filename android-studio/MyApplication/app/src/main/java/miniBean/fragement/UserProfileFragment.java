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
import miniBean.util.ImageUtil;
import miniBean.viewmodel.ProfileVM;
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
        spinner = (ProgressBar) view.findViewById(R.id.spinner);
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
                intent.putExtra("id", getArguments().getLong("oid"));
                intent.putExtra("key", "useranswer");
                startActivity(intent);
            }
        });

        long userId = getArguments().getLong("oid");
        getUserProfile(userId);

        return view;
    }

    private void getUserProfile(final long userId) {
        spinner.setVisibility(View.VISIBLE);
        spinner.bringToFront();

        AppController.api.getUserProfile(userId, AppController.getInstance().getSessionId(), new Callback<ProfileVM>() {
            @Override
            public void success(ProfileVM profile, retrofit.client.Response response) {
                userName.setText(profile.getDn());
                questionsCount.setText(profile.getQc() + "");
                answersCount.setText(profile.getAc() + "");

                ImageUtil.displayThumbnailProfileImage(userId, userPic);
                ImageUtil.displayCoverImage(userId, userCoverPic, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        spinner.setVisibility(View.VISIBLE);
                        spinner.bringToFront();
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