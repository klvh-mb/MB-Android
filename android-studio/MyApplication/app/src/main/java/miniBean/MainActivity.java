package miniBean;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;

import com.astuetz.PagerSlidingTabStrip;

import miniBean.app.LocalCache;
import miniBean.fragement.MyCommunityFragment;
import miniBean.fragement.TopicFragment;
import miniBean.viewmodel.CommunityCategoryMapVM;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public Fragment newsFeedFragement;
    ActionBar.Tab Tab1, Tab2, Tab3;
    private ViewPager viewPager;
    private MyPagerAdapter mAdapter;
    private ActionBar actionBar;
    private PagerSlidingTabStrip tabs;

    static Topic topics[] =  // TODO remove this hardcodding later
                {
                        new Topic("My Comm",1) ,
                        new Topic("Mom",2),
                        new Topic("Dad",3)
                };
    // Tab titles
    //private String[] tabs = { "Top Rated", "Games", "Movies" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        actionBar = getSupportActionBar();
        actionBar.hide();


        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);

        tabs.setViewPager(viewPager);

        tabs.setIndicatorColor(Color.RED);


    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}

class Topic {
    Topic(String title, long id) {
        this.title = title;
        this.id = id;
    }

    String title;
    long id;

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
                topicFragment.setCommunities(LocalCache.getCategoryMapList().get(position).getCommunities());
                return topicFragment;
        }
    }

}