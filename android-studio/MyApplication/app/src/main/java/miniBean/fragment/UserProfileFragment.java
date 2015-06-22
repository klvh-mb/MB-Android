package miniBean.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.lang.reflect.Field;
import java.util.List;

import miniBean.R;
import miniBean.activity.MessageDetailActivity;
import miniBean.activity.NewsfeedActivity;
import miniBean.app.AppController;
import miniBean.app.TrackedFragment;
import miniBean.util.AnimationUtil;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.ProfileVM;
import miniBean.viewmodel.ResponseConversationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserProfileFragment extends TrackedFragment {

    private static final String TAG = UserProfileFragment.class.getName();
    private ImageView userCoverPic, userPic;
    private ProgressBar spinner;
    private TextView questionsCount, answersCount, bookmarksCount, userName, userInfoText;
    private LinearLayout questionMenu, answerMenu, bookmarksMenu, settingsMenu, userInfoLayout;
    private Button editButton,messageButton;
    private String nameUser;
    private Long convID;
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
        settingsMenu = (LinearLayout) view.findViewById(R.id.menuSettings);
        settingsMenu.setVisibility(View.GONE);

        editButton = (Button) view.findViewById(R.id.editButton);
        editButton.setVisibility(View.GONE);

        userInfoLayout = (LinearLayout) view.findViewById(R.id.userInfoLayout);
        userInfoLayout.setVisibility(View.GONE);
        userInfoText = (TextView) view.findViewById(R.id.userInfoText);

        ImageView editCoverImage = (ImageView) view.findViewById(R.id.editCoverImage);
        editCoverImage.setVisibility(View.GONE);
        ImageView editUserImage = (ImageView) view.findViewById(R.id.editUserImage);
        editUserImage.setVisibility(View.GONE);

        messageButton= (Button) view.findViewById(R.id.messageButton);

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

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConversation();


            }
        });

        long userId = getArguments().getLong("oid");
        getUserProfile(userId);

        return view;
    }

    private void getUserProfile(final long userId) {
        AnimationUtil.show(spinner);

        AppController.getApi().getUserProfile(userId, AppController.getInstance().getSessionId(), new Callback<ProfileVM>() {
            @Override
            public void success(ProfileVM profile, retrofit.client.Response response) {
                userName.setText(profile.getDn());
                questionsCount.setText(profile.getQc() + "");
                answersCount.setText(profile.getAc() + "");
                nameUser=profile.getDn();


                // admin only
                if (AppController.isUserAdmin()) {
                    userInfoText.setText(profile.toString());
                    userInfoLayout.setVisibility(View.VISIBLE);
                } else {
                    userInfoLayout.setVisibility(View.GONE);
                }

                ImageUtil.displayProfileImage(userId, userPic);
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
    private void startConversation(){
        AppController.getApi().startConversation(getArguments().getLong("oid"),AppController.getInstance().getSessionId(),new Callback<List<ResponseConversationVM>>() {
            @Override
            public void success(List<ResponseConversationVM> responseConversationVM, Response response1) {
                Intent intent=new Intent(getActivity(), MessageDetailActivity.class);
                intent.putExtra("user_name",nameUser);
                intent.putExtra("uid",getArguments().getLong("oid"));
                System.out.println("cid:::::::::::::::"+responseConversationVM.get(0).getId());
                intent.putExtra("cid",responseConversationVM.get(0).getId());
                startActivity(intent);

            }

            @Override
            public void failure(RetrofitError error) {
                //error.printStackTrace();
                System.out.println("url:::"+error.getResponse().getUrl());
            }
        });


    }
}