package miniBean.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import miniBean.R;
import miniBean.activity.MainActivity;
import miniBean.activity.NewPostActivity;
import miniBean.app.TrackedFragment;
import miniBean.app.UserInfoCache;
import miniBean.util.AnimationUtil;
import miniBean.util.ImageUtil;

public class MyCommunityNewsfeedFragment extends TrackedFragment {

    private RelativeLayout profileLayout;
    private ImageView profileImage;
    private TextView usernameText;
    private ImageView mascotIcon, newPostIcon;
    private Button newsfeed, joined;
    private boolean newsfeedPressed = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.my_community_newsfeed_fragement, container, false);

        profileLayout = (RelativeLayout) view.findViewById(R.id.profileLayout);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        usernameText = (TextView) view.findViewById(R.id.usernameText);

        mascotIcon = (ImageView) view.findViewById(R.id.mascotIcon);
        newPostIcon = (ImageView) view.findViewById(R.id.newPostIcon);

        newsfeed = (Button) view.findViewById(R.id.buttonNewsfeed); // obsolete...
        joined = (Button) view.findViewById(R.id.buttonJoined);     // obsolete...

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.rotateBackForthOnce(mascotIcon);
            }
        }, 2000);

        pressNewsfeedButton();

        // profile
        ImageUtil.displayThumbnailProfileImage(UserInfoCache.getUser().getId(), profileImage);
        usernameText.setText(UserInfoCache.getUser().getDisplayName());

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = MainActivity.getInstance();
                if (mainActivity != null) {
                    mainActivity.pressProfileTab();
                }
            }
        });

        mascotIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch game
            }
        });

        newPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch new post page with no comm id, user will select
                Intent intent = new Intent(MyCommunityNewsfeedFragment.this.getActivity(), NewPostActivity.class);
                intent.putExtra("id",0L);
                intent.putExtra("flag","FromCommActivity");
                startActivity(intent);
            }
        });

        // obsolete...

        newsfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newsfeedPressed) {
                    pressNewsfeedButton();
                    newsfeedPressed = true;
                }
            }

        });

        joined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsfeedPressed) {
                    pressJoinedButton();
                    newsfeedPressed = false;
                }
            }
        });

        return view;
    }

    private void pressJoinedButton() {
        joined.setBackgroundColor(Color.WHITE);
        joined.setTextColor(getResources().getColor(R.color.actionbar_selected_text));
        newsfeed.setBackgroundColor(getResources().getColor(R.color.actionbar_bg_light));
        newsfeed.setTextColor(Color.WHITE);

        TrackedFragment fragment = new CommunityListFragment();
        fragment.setTrackedOnce();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, fragment).commit();
    }

    private void pressNewsfeedButton() {
        newsfeed.setBackgroundColor(Color.WHITE);
        newsfeed.setTextColor(getResources().getColor(R.color.actionbar_selected_text));
        joined.setBackgroundColor(getResources().getColor(R.color.actionbar_bg_light));
        joined.setTextColor(Color.WHITE);

        Bundle bundle = new Bundle();
        bundle.putString("key","feed");
        MyCommunityNewsfeedListFragement fragment = new MyCommunityNewsfeedListFragement();
        fragment.setTrackedOnce();
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.children_fragement, fragment);
        fragmentTransaction.commit();
    }
}

