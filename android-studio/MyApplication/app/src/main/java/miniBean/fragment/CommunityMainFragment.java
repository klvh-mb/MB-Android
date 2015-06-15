package miniBean.fragment;

import android.app.Activity;
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

import com.astuetz.PagerSlidingTabStrip;

import miniBean.R;
import miniBean.app.LocalCommunityTabCache;
import miniBean.app.TrackedFragment;
import miniBean.viewmodel.CommunityCategoryMapVM;

public class CommunityMainFragment extends TrackedFragment {

    private static final String TAG = CommunityMainFragment.class.getName();
    private ViewPager viewPager;
    private CommunityMainPagerAdapter mAdapter;
    private PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.community_main_fragement, container, false);

        getActivity().getActionBar().hide();

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new CommunityMainPagerAdapter(getChildFragmentManager());

        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);
        viewPager.setAdapter(mAdapter);
        
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
        super.onAttach(activity);
    }
}

class CommunityMainPagerAdapter extends FragmentPagerAdapter {

    private static String[] TITLES;

    public CommunityMainPagerAdapter(FragmentManager fm) {
        super(fm);

        if (TITLES == null && LocalCommunityTabCache.getCommunityCategoryMapList() != null) {
            Log.d(this.getClass().getSimpleName(), "CommunityMainPagerAdapter: TITLES size="+LocalCommunityTabCache.getCommunityCategoryMapList().size());
            TITLES = new String[LocalCommunityTabCache.getCommunityCategoryMapList().size()]; // TODO
        }

        int index = 0;
        for (CommunityCategoryMapVM mapList : LocalCommunityTabCache.getCommunityCategoryMapList()) {
            Log.d(this.getClass().getSimpleName(), "CommunityMainPagerAdapter: TITLES["+index+"]="+mapList.getName());
            TITLES[index] = mapList.getName();
            index++;
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
        Log.d(this.getClass().getSimpleName(), "getItem: item - " + position);
        switch (position) {
            case 0: {
                MyCommunityFragment fragment = new MyCommunityFragment();
                return fragment;
            }
            default: {
                TopicCommunityFragment fragment = new TopicCommunityFragment();
                fragment.setTrackedOnce();
                fragment.setCommunities(LocalCommunityTabCache.getCommunityCategoryMapList().get(position).communities);
                return fragment;
            }
        }
    }
}
