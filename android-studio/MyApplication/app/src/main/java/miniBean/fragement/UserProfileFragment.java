package miniBean.fragement;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.reflect.Field;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class UserProfileFragment extends Fragment {

    private static final String TAG = UserProfileFragment.class.getName();
    ImageView userCoverPic, userPic;
    ProgressBar spinner;
    TextView question, answer, bookmarks, userName;
    LinearLayout questionMenu,answerMenu,bookmarksMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.user_profile_fragement, container, false);

        userName = (TextView) view.findViewById(R.id.usernameText);
        question = (TextView) view.findViewById(R.id.Edit1);
        answer = (TextView) view.findViewById(R.id.Edit2);
        userCoverPic = (ImageView) view.findViewById(R.id.userCoverPic);
        userPic = (ImageView) view.findViewById(R.id.userImages);
        spinner = (ProgressBar) view.findViewById(R.id.imageLoader);
        questionMenu= (LinearLayout) view.findViewById(R.id.menuQuestions);
        answerMenu= (LinearLayout) view.findViewById(R.id.menuAnswer);
       // bookmarksMenu= (LinearLayout) view.findViewById(R.id.menuBookmarks);

        final String id=getArguments().getString("id");
        System.out.println("nnnnnnnnnnnnn"+getArguments().getString("name"));
        System.out.println("iiiiiiiiiiiii"+getArguments().getString("id"));

        questionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putLong("id",getArguments().getLong("oid"));
                bundle.putString("key","userquestion");
                NewsFeedFragement fragment = new NewsFeedFragement();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(UserProfileFragment.this);
                //fragmentTransaction.add(R.id.placeHolder, fragment);
                fragmentTransaction.replace(R.id.placeHolder, fragment);
                fragmentTransaction.commit();
                /*Intent i = new Intent(getActivity(), NewsfeedActivity.class);
                i.putExtra("id",id);
                startActivity(i);*/
            }


        });
        answerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putLong("id",getArguments().getLong("oid"));
                bundle.putString("key","useranswer");
                NewsFeedFragement fragment = new NewsFeedFragement();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(UserProfileFragment.this);
                fragmentTransaction.replace(R.id.placeHolder, fragment);
                fragmentTransaction.commit();
            }
        });

        getUserInfo();

        return view;
    }

    void getUserInfo() {
        AppController.api.getUserInfo(AppController.getInstance().getSessionId(), new Callback<UserVM>() {
            @Override
            public void success(UserVM user, retrofit.client.Response response) {

                userName.setText(getArguments().getString("name"));
                answer.setText("100");
                question.setText("100");
                //bookmarks.setText("100");

                AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + "/image/get-cover-image-by-id/" + getArguments().getString("id"), userCoverPic, new SimpleImageLoadingListener() {
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
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(25)) // default
                        .build();
                AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + "/image/get-profile-image-by-id/" + getArguments().getLong("oid"), userPic);
                System.out.println("userrrrrrrrr::::::::::"+getArguments().getLong("oid"));
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