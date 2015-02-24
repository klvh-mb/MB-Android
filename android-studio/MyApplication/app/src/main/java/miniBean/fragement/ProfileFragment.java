package miniBean.fragement;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import miniBean.activity.ActivityMain;
import miniBean.app.MyApi;
import miniBean.R;
import miniBean.app.AppController;
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
    Button editButton;
    TextView question, answer, bookmarks, userName;
    private UserVM userItems;

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
        setHasOptionsMenu(true);
        ((ActivityMain) getActivity()).getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ((ActivityMain) getActivity()).getActionBar().setCustomView(R.layout.profile_actionbar);
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

                AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + "/image/get-cover-image-by-id/" + user.getId(), userCoverPic);
                AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + "/image/get-profile-image-by-id/" + user.getId(), userPic);
                // ImageLoader.getInstance().displayImage(getResources().getString(R.string.base_url) + "/image/get-cover-community-image-by-id/"+user.getId(), userCoverPic, defaultOptions);
                //  ImageLoader.getInstance().displayImage(getResources().getString(R.string.base_url) + "/image/get-profile-image-by-id/"+user.getId(),userPic, defaultOptions);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }
}