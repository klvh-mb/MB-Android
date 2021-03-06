package miniBean.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import miniBean.R;
import miniBean.app.TrackedFragment;
import miniBean.util.ViewUtil;

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

        int pageMargin = ViewUtil.getRealDimension(2, this.getResources());
        viewPager.setPageMargin(pageMargin);
        viewPager.setAdapter(mAdapter);
        
        tabs.setViewPager(viewPager);
        tabs.setTextColor(getResources().getColor(R.color.dark_gray));
        tabs.setIndicatorColor(getResources().getColor(R.color.actionbar_selected_text));

        int indicatorHeight = ViewUtil.getRealDimension(5, this.getResources());
        tabs.setIndicatorHeight(indicatorHeight);

        final int textSize = ViewUtil.getRealDimension(16, this.getResources());
        tabs.setTextSize(textSize);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}

/**
 * Obsolete... already replace CommunityMainPagerAdapter with MyCommunityPagerAdapter
 * Now just placeholder of 1 fragment MyCommunityFragment. Should remove completely later...
 */
class CommunityMainPagerAdapter extends FragmentStatePagerAdapter {

    private static String[] TITLES;

    public CommunityMainPagerAdapter(FragmentManager fm) {
        super(fm);

        /*
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
        */
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //return TITLES[position];
        return "";
    }

    @Override
    public int getCount() {
        //return TITLES.length;
        return 1;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(this.getClass().getSimpleName(), "getItem: item - " + position);
        MyCommunityNewsfeedFragment fragment = new MyCommunityNewsfeedFragment();
        return fragment;
        /*
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
        */
    }
}
