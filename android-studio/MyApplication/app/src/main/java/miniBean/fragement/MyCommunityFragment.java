package miniBean.fragement;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import miniBean.R;

public class MyCommunityFragment extends Fragment {

    boolean buttonBool=false;
    Button joined, newsfeed;
    ProgressBar progressbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.mycomm, container, false);

        Fragment communityFragment = new CommunityFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, communityFragment).commit();

        joined = (Button) view.findViewById(R.id.button1);
        newsfeed = (Button) view.findViewById(R.id.button2);
        progressbar= (ProgressBar) view.findViewById(R.id.progressComm);

        progressbar.setVisibility(View.VISIBLE);

        joined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonBool==true) {
                    joined.setBackgroundColor(Color.WHITE);
                    newsfeed.setBackgroundColor(Color.RED);
                    Fragment communityFragment = new CommunityFragment();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.children_fragement, communityFragment).commit();
                    buttonBool = false;
                }

            }

        });

        newsfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonBool==false) {
                    newsfeed.setBackgroundColor(Color.WHITE);
                    joined.setBackgroundColor(Color.RED);
                    Fragment newsFeedFragement = new NewsFeedFragement();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.children_fragement, newsFeedFragement).commit();
                    buttonBool=true;
                }
                }

        });

        return view;
    }
}
