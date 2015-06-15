package miniBean.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

import miniBean.R;
import miniBean.app.LocalCommunityTabCache;
import miniBean.util.ActivityUtil;
import miniBean.viewmodel.CommunitiesWidgetChildVM;

public class MyNewsfeedListFragement extends NewsfeedListFragement {

    private static final String TAG = MyNewsfeedListFragement.class.getName();

    private Button topicCommsButton, yearCommsButton;
    private boolean topicCommsPressed = true;

    private ViewPager viewPager;
    private MyCommunityPagerAdapter mAdapter;

    private LinearLayout dotsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        View headerView = getHeaderView();

        topicCommsButton = (Button) headerView.findViewById(R.id.topicCommsButton);
        yearCommsButton = (Button) headerView.findViewById(R.id.yearCommsButton);

        viewPager = (ViewPager) headerView.findViewById(R.id.commsPager);
        dotsLayout = (LinearLayout) view.findViewById(R.id.dots);

        int pageMargin = ActivityUtil.getRealDimension(2, this.getResources());
        viewPager.setPageMargin(pageMargin);

        // comms pager buttons
        topicCommsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!topicCommsPressed) {
                    pressTopicCommsButton();
                    topicCommsPressed = true;
                }
            }

        });

        yearCommsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (topicCommsPressed) {
                    pressYearCommsButton();
                    topicCommsPressed = false;
                }
            }
        });

        pressTopicCommsButton();

        LocalCommunityTabCache.setMyNewsfeedListFragement(this);

        return view;
    }

    public void notifyChange() {
        mAdapter.notifyDataSetChanged();
        viewPager.invalidate();
    }

    private void pressTopicCommsButton() {
        yearCommsButton.setBackgroundColor(getResources().getColor(R.color.view_bg));
        yearCommsButton.setTextColor(getResources().getColor(R.color.actionbar_selected_text));
        topicCommsButton.setBackgroundColor(getResources().getColor(R.color.actionbar_bg_light));
        topicCommsButton.setTextColor(getResources().getColor(R.color.view_bg));

        mAdapter = new MyCommunityPagerAdapter(LocalCommunityTabCache.CommunityTabType.TOPIC_COMMUNITY, getChildFragmentManager());
        viewPager.setAdapter(mAdapter);

        // pager indicator
        addDots(mAdapter.getCount(), dotsLayout, viewPager);

        notifyChange();
    }

    private void pressYearCommsButton() {
        topicCommsButton.setBackgroundColor(getResources().getColor(R.color.view_bg));
        topicCommsButton.setTextColor(getResources().getColor(R.color.actionbar_selected_text));
        yearCommsButton.setBackgroundColor(getResources().getColor(R.color.actionbar_bg_light));
        yearCommsButton.setTextColor(getResources().getColor(R.color.view_bg));

        mAdapter = new MyCommunityPagerAdapter(LocalCommunityTabCache.CommunityTabType.ZODIAC_YEAR_COMMUNITY, getChildFragmentManager());
        viewPager.setAdapter(mAdapter);

        // pager indicator
        addDots(mAdapter.getCount(), dotsLayout, viewPager);

        notifyChange();
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

    /**
     * HACK... returns POSITION_NONE will refresh pager more frequent than needed... but works in this case
     * http://stackoverflow.com/questions/12510404/reorder-pages-in-fragmentstatepageradapter-using-getitempositionobject-object
     *
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
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