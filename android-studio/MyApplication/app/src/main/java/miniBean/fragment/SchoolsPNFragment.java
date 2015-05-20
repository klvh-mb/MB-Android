package miniBean.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import miniBean.R;
import miniBean.activity.NewPNPostActivity;
import miniBean.activity.TopSchoolsActivity;

public class SchoolsPNFragment extends MyFragment {

    public static final String INTENT_FLAG = "FromPN";

    private Button buttonList,buttonNews,buttonBookmark;
    private boolean listClicked=true,newsClicked,bookmarkClicked;

    private ImageView newPostIcon,rankingActionIcon;

    private MyFragment selectedFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.schools_pn_fragement, container, false);

        buttonList = (Button) view.findViewById(R.id.buttonListing);
        buttonBookmark = (Button) view.findViewById(R.id.buttonBookmark);
        buttonNews = (Button) view.findViewById(R.id.buttonNewsfeed);
        newPostIcon = (ImageView) view.findViewById(R.id.newPostIcon);
        rankingActionIcon= (ImageView) view.findViewById(R.id.rankingAction);

        if(listClicked){
            pressListButton();
        }else if(newsClicked){
            pressNewsButton();
        }else if(bookmarkClicked){
            pressBookmarkButton();
        }

        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressListButton();
                bookmarkClicked=false;
                newsClicked=false;
                listClicked=true;
            }
        });

        buttonBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressBookmarkButton();
                bookmarkClicked=true;
                newsClicked=false;
                listClicked=false;
            }
        });

        buttonNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressNewsButton();
                bookmarkClicked=false;
                newsClicked=true;
                listClicked=false;
            }
        });

        newPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch new post page with no comm id, user will select
                Intent intent = new Intent(SchoolsPNFragment.this.getActivity(), NewPNPostActivity.class);
                intent.putExtra("id",0L);
                intent.putExtra("flag",INTENT_FLAG);
                startActivity(intent);
            }
        });

        rankingActionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SchoolsPNFragment.this.getActivity(),TopSchoolsActivity.class);
                intent.putExtra("flag",INTENT_FLAG);
                startActivity(intent);
            }
        });

        return view;
    }

    private void pressListButton(){
        buttonList.setBackgroundColor(Color.WHITE);
        buttonList.setTextColor(getResources().getColor(R.color.pn_box_border));

        buttonBookmark.setTextColor(Color.WHITE);
        buttonBookmark.setBackgroundColor(getResources().getColor(R.color.pn_box_border));

        buttonNews.setTextColor(Color.WHITE);
        buttonNews.setBackgroundColor(getResources().getColor(R.color.pn_box_border));

        selectedFragment = new SchoolsPNListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, selectedFragment).commit();
    }

    private void pressNewsButton(){
        buttonList.setBackgroundColor(getResources().getColor(R.color.pn_box_border));
        buttonList.setTextColor(Color.WHITE);

        buttonBookmark.setTextColor(Color.WHITE);
        buttonBookmark.setBackgroundColor(getResources().getColor(R.color.pn_box_border));

        buttonNews.setTextColor(getResources().getColor(R.color.pn_box_border));
        buttonNews.setBackgroundColor(Color.WHITE);

        SchoolsNewsfeedListFragement fragment = new SchoolsNewsfeedListFragement();
        fragment.setIsPN(true);
        selectedFragment = fragment;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, selectedFragment).commit();
    }

    private void pressBookmarkButton(){
        buttonList.setBackgroundColor(getResources().getColor(R.color.pn_box_border));
        buttonList.setTextColor(Color.WHITE);

        buttonBookmark.setTextColor(getResources().getColor(R.color.pn_box_border));
        buttonBookmark.setBackgroundColor(Color.WHITE);

        buttonNews.setTextColor(Color.WHITE);
        buttonNews.setBackgroundColor(getResources().getColor(R.color.pn_box_border));

        selectedFragment = new PNBookmarksFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, selectedFragment).commit();
    }

    @Override
    public boolean allowBackPressed() {
        if (selectedFragment != null)
            return selectedFragment.allowBackPressed();
        return super.allowBackPressed();
    }
}