package miniBean.fragement;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import miniBean.R;

public class MyCommunityFragment extends Fragment {

    boolean joinedPressed = true;
    Button joined, newsfeed;
    ProgressBar progressbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.mycomm_fragement, container, false);

        joined = (Button) view.findViewById(R.id.button1);
        newsfeed = (Button) view.findViewById(R.id.button2);
        progressbar = (ProgressBar) view.findViewById(R.id.progressComm);
        progressbar.setVisibility(View.VISIBLE);

        if (joinedPressed) {
            pressJoinedButton();
        } else {
            pressNewsfeedButton();
        }

        joined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!joinedPressed) {
                    pressJoinedButton();
                    progressbar.setVisibility(View.INVISIBLE);
                    joinedPressed = true;
                }
            }
        });

        newsfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinedPressed) {
                    pressNewsfeedButton();
                    progressbar.setVisibility(View.INVISIBLE);
                    joinedPressed = false;
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

        Fragment communityFragment = new CommunityFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, communityFragment).commit();
    }

    private void pressNewsfeedButton() {
        newsfeed.setBackgroundColor(Color.WHITE);
        newsfeed.setTextColor(getResources().getColor(R.color.actionbar_selected_text));
        joined.setBackgroundColor(getResources().getColor(R.color.actionbar_bg_light));
        joined.setTextColor(Color.WHITE);

        Bundle bundle = new Bundle();
        bundle.putString("key","feed");
        NewsFeedFragement fragment = new NewsFeedFragement();
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.children_fragement, fragment);
        fragmentTransaction.commit();
    }
}

