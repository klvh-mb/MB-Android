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

public class SchoolsKGFragment extends Fragment {

    private Button buttonList,buttonBookmark;
    private boolean listClicked=true,bookmarkClicked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.schools_kg_fragement, container, false);

        buttonList = (Button) view.findViewById(R.id.buttonListing);
        buttonBookmark = (Button) view.findViewById(R.id.buttonBookmark);

        if(listClicked){
            pressListButton();
        }else if(bookmarkClicked){
            pressBookmarkButton();
        }

        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressListButton();
                bookmarkClicked=false;
                listClicked=true;
            }
        });

        buttonBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressBookmarkButton();
                bookmarkClicked=true;
                listClicked=false;

            }
        });

        return view;
    }

    private void pressListButton(){
        buttonList.setBackgroundColor(Color.WHITE);
        buttonList.setTextColor(getResources().getColor(R.color.kg_box_border));

        buttonBookmark.setTextColor(Color.WHITE);
        buttonBookmark.setBackgroundColor(getResources().getColor(R.color.kg_box_border));

        Fragment kindyListFragment = new KindyListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, kindyListFragment).commit();
    }

    private void pressBookmarkButton(){
        buttonList.setBackgroundColor(getResources().getColor(R.color.kg_box_border));
        buttonList.setTextColor(Color.WHITE);

        buttonBookmark.setTextColor(getResources().getColor(R.color.kg_box_border));
        buttonBookmark.setBackgroundColor(Color.WHITE);

        Fragment kindyBookmarkFragment = new KindyBookmarkFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, kindyBookmarkFragment).commit();

    }
}

