package miniBean.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import miniBean.R;
import miniBean.activity.NewPostActivity;
import miniBean.app.TrackedFragment;
import miniBean.util.AnimationUtil;

public class MyCommunityFragment extends TrackedFragment {

    private ImageView signInAction, newPostIcon;
    private Button newsfeed, joined;
    private boolean newsfeedPressed = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.my_community_fragement, container, false);

        newPostIcon = (ImageView) view.findViewById(R.id.newPostIcon);
        signInAction = (ImageView) view.findViewById(R.id.signInAction);
        newsfeed = (Button) view.findViewById(R.id.buttonNewsfeed);
        joined = (Button) view.findViewById(R.id.buttonJoined);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.rotateBackForthOnce(signInAction);
            }
        }, 2000);

        if (newsfeedPressed) {
            pressNewsfeedButton();
        } else {
            pressJoinedButton();
        }

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

        newPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch new post page with no comm id, user will select
                Intent intent = new Intent(MyCommunityFragment.this.getActivity(), NewPostActivity.class);
                intent.putExtra("id",0L);
                intent.putExtra("flag","FromCommActivity");
                startActivity(intent);
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
        MyNewsfeedListFragement fragment = new MyNewsfeedListFragement();
        fragment.setTrackedOnce();
        fragment.setHeaderResouce(R.layout.my_community_newsfeed_header);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.children_fragement, fragment);
        fragmentTransaction.commit();
    }
}

