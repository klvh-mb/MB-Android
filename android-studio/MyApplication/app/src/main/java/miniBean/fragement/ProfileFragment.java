package miniBean.fragement;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.reflect.Field;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.MyApi;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;


public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getName();
    public SharedPreferences session = null;
    public MyApi api;
    ImageView userCoverPic, userPic;
    ProgressBar spinner;
    TextView question, answer, bookmarks, userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.profile_view, container, false);
        session = getActivity().getSharedPreferences("prefs", 0);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();


        userName = (TextView) view.findViewById(R.id.usernameText);
        bookmarks = (TextView) view.findViewById(R.id.Edit1);
        question = (TextView) view.findViewById(R.id.Edit2);
        answer = (TextView) view.findViewById(R.id.Edit3);
        userCoverPic = (ImageView) view.findViewById(R.id.userCoverPic);
        userPic = (ImageView) view.findViewById(R.id.userImage);
        spinner= (ProgressBar) view.findViewById(R.id.imageLoader);
        getUserInfo();

        return view;
    }

    void getUserInfo() {
        AppController.api.getUserInfo(session.getString("sessionID", null), new Callback<UserVM>() {
            @Override
            public void success(UserVM user, retrofit.client.Response response) {

                userName.setText(user.getDisplayName());
                answer.setText("100");
                question.setText("100");
                bookmarks.setText("100");

                AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + "/image/get-cover-image-by-id/" + user.getId(), userCoverPic,new SimpleImageLoadingListener(){
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
                AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + "/image/get-profile-image-by-id/" + user.getId(), userPic);
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