package miniBean.fragement;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import miniBean.R;
import miniBean.app.LocalCache;
import miniBean.viewmodel.CommunityCategoryMapVM;


public class MainFragement extends Fragment {

    private static final String TAG = MainFragement.class.getName();
    ActionBar.Tab Tab1, Tab2, Tab3;
    private ViewPager viewPager;
    private MyPagerAdapter mAdapter;
    private PagerSlidingTabStrip tabs;
    private Activity myContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.main_fragement, container, false);

        System.out.println("in mainFragment......");

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(mAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);

        tabs.setViewPager(viewPager);

        tabs.setTextColor(getResources().getColor(R.color.dark_gray));
        tabs.setIndicatorColor(getResources().getColor(R.color.actionbar_selected_text));

        final int indicatorHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        tabs.setIndicatorHeight(indicatorHeight);

        final int textSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        tabs.setTextSize(textSize);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (Activity) activity;
        super.onAttach(activity);
    }

}


class MyPagerAdapter extends FragmentPagerAdapter {

    private String[] TITLES;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        TITLES = new String[LocalCache.categoryMapList.size()]; // TODO
        int index = 0;
        for (CommunityCategoryMapVM _topic : LocalCache.categoryMapList) {
            TITLES[index++] = _topic.getName();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyCommunityFragment();
            default:
                TopicFragment topicFragment = new TopicFragment();
                topicFragment.setCommunities(LocalCache.categoryMapList.get(position).communities);
                return topicFragment;
        }
    }

}
