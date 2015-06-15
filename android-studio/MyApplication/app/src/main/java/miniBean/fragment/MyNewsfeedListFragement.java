package miniBean.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import miniBean.R;
import miniBean.app.LocalCommunityTabCache;
import miniBean.viewmodel.CommunitiesWidgetChildVM;

public class MyNewsfeedListFragement extends NewsfeedListFragement {

    private static final String TAG = MyNewsfeedListFragement.class.getName();

    private ViewPager viewPager;
    private MyCommunityPagerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        View headerView = getHeaderView();
        viewPager = (ViewPager) headerView.findViewById(R.id.commsPager);
        mAdapter = new MyCommunityPagerAdapter(LocalCommunityTabCache.CommunityTabType.TOPIC_COMMUNITY, getChildFragmentManager());

        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);
        viewPager.setAdapter(mAdapter);

        return view;
    }
}

class MyCommunityPagerAdapter extends FragmentPagerAdapter {

    public static final int COMMUNITIES_PER_PAGE = 4;

    private LocalCommunityTabCache.CommunityTabType tabType;
    private String title;

    public MyCommunityPagerAdapter(LocalCommunityTabCache.CommunityTabType tabType, FragmentManager fm) {
        super(fm);

        this.tabType = tabType;
        this.title  = LocalCommunityTabCache.getCommunityCategoryMapByType(tabType).getName();
        Log.d(this.getClass().getSimpleName(), "create: tabType="+tabType.name()+" title="+title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title;
    }

    @Override
    public int getCount() {
        int count = (int) Math.ceil((double) LocalCommunityTabCache.getCommunityCategoryMapByType(tabType).getCommunities().size() / (double) COMMUNITIES_PER_PAGE);
        return count;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(this.getClass().getSimpleName(), "getItem: item - " + position);
        switch (position) {
            default: {
                MyCommunityPagerFragment fragment = new MyCommunityPagerFragment();
                fragment.setTrackedOnce();
                fragment.setCommunities(getCommunitiesForPage(position));
                return fragment;
            }
        }
    }

    private List<CommunitiesWidgetChildVM> getCommunitiesForPage(int position) {
        int start = position * COMMUNITIES_PER_PAGE;
        int end = start + COMMUNITIES_PER_PAGE;

        List<CommunitiesWidgetChildVM> communities = LocalCommunityTabCache.getCommunityCategoryMapByType(tabType).getCommunities();
        if (start >= communities.size()) {
            Log.e(this.getClass().getSimpleName(), "getCommunitiesForPage: position out of bound... position="+position+" communities.size="+communities.size());
            return null;
        }

        if (end >= communities.size()) {
            end = communities.size();
        }

        return communities.subList(start, end);
    }
}