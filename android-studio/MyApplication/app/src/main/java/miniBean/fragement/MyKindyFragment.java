package miniBean.fragement;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import miniBean.R;

public class MyKindyFragment extends Fragment {

    private Button buttonList,buttonNews,buttonBookmark;
    private boolean listClicked=true,newsClicked,bookmarkClicked;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.my_school_fragement, container, false);

        buttonList= (Button) view.findViewById(R.id.buttonListing);
        buttonBookmark= (Button) view.findViewById(R.id.buttonBookmark);
        buttonNews= (Button) view.findViewById(R.id.buttonNewsfeed);

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

        return view;
    }

    private void pressListButton(){
        buttonList.setBackgroundColor(Color.WHITE);
        buttonList.setTextColor(Color.parseColor("#57B154"));

        buttonBookmark.setTextColor(Color.WHITE);
        buttonBookmark.setBackgroundColor(Color.parseColor("#57B154"));

        buttonNews.setTextColor(Color.WHITE);
        buttonNews.setBackgroundColor(Color.parseColor("#57B154"));

        Fragment kindyListFragment = new KindyListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, kindyListFragment).commit();
    }

    private void pressNewsButton(){
        buttonList.setBackgroundColor(Color.parseColor("#57B154"));
        buttonList.setTextColor(Color.WHITE);

        buttonBookmark.setTextColor(Color.WHITE);
        buttonBookmark.setBackgroundColor(Color.parseColor("#57B154"));

        buttonNews.setTextColor(Color.parseColor("#57B154"));
        buttonNews.setBackgroundColor(Color.WHITE);

        Fragment kindyFeedListFragment = new KindyNewsfeedListFragement();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, kindyFeedListFragment).commit();

    }
    private void pressBookmarkButton(){
        buttonList.setBackgroundColor(Color.parseColor("#57B154"));
        buttonList.setTextColor(Color.WHITE);

        buttonBookmark.setTextColor(Color.parseColor("#57B154"));
        buttonBookmark.setBackgroundColor(Color.WHITE);

        buttonNews.setTextColor(Color.WHITE);
        buttonNews.setBackgroundColor(Color.parseColor("#57B154"));

        Fragment kindyBookmarkFragment = new KindyBookmarkFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, kindyBookmarkFragment).commit();

    }
}

