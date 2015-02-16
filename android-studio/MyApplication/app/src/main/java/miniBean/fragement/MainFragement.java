package miniBean.fragement;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import miniBean.DetailActivity;
import miniBean.InfiniteScrollListener;
import miniBean.MyApi;
import miniBean.R;
import miniBean.adapter.FeedListAdapter;
import miniBean.adapter.TabsPagerAdapter;
import miniBean.app.LocalCache;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.Post;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;


public class MainFragement extends Fragment{

    private static final String TAG = MainFragement.class.getName();
    private ViewPager viewPager;
    private MyPagerAdapter mAdapter;

    private PagerSlidingTabStrip tabs;
    ActionBar.Tab Tab1, Tab2, Tab3;
    private Activity myContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.tabs, container, false);

        System.out.println("in mainFragment......");

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(mAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);

        tabs.setViewPager(viewPager);

        tabs.setIndicatorColor(Color.RED);


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(Activity) activity;
        super.onAttach(activity);
    }

}


class MyPagerAdapter extends FragmentPagerAdapter {

    private String[] TITLES ;

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
