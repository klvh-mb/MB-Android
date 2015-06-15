package miniBean.fragment;

import android.app.ActionBar;
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
import miniBean.app.AppController;
import miniBean.app.TrackedFragment;
import miniBean.util.ActivityUtil;

public class SchoolsMainFragment extends TrackedFragment {

    private static final String TAG = SchoolsMainFragment.class.getName();
    private ActionBar.Tab Tab1, Tab2, Tab3;
    private ViewPager viewPager;
    private SchoolsPagerAdapter mAdapter;
    private PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.schools_main_fragement, container, false);

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new SchoolsPagerAdapter(getChildFragmentManager());

        int pageMargin = ActivityUtil.getRealDimension(2, this.getResources());
        viewPager.setPageMargin(pageMargin);
        viewPager.setAdapter(mAdapter);

        tabs.setViewPager(viewPager);
        tabs.setTextColor(getResources().getColor(R.color.dark_gray));
        tabs.setIndicatorColor(getResources().getColor(R.color.pn_box_border));

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == SchoolsPagerAdapter.PN_PAGE)
                    tabs.setIndicatorColor(getResources().getColor(R.color.pn_box_border));
                else
                    tabs.setIndicatorColor(getResources().getColor(R.color.kg_box_border));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        int indicatorHeight = ActivityUtil.getRealDimension(5, this.getResources());
        tabs.setIndicatorHeight(indicatorHeight);

        int textSize = ActivityUtil.getRealDimension(16, this.getResources());
        tabs.setTextSize(textSize);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public boolean allowBackPressed() {
        TrackedFragment fragment = mAdapter.getFragment(viewPager.getCurrentItem());
        Log.d(this.getClass().getSimpleName(), "allowBackPressed: call "+fragment.getClass().getSimpleName());

        if (fragment != null)
            return fragment.allowBackPressed();
        return super.allowBackPressed();
    }
}

class SchoolsPagerAdapter extends FragmentPagerAdapter {

    public static final int PN_PAGE = 0;
    public static final int KG_PAGE = 1;

    private TrackedFragment pnFragment;
    private TrackedFragment kgFragment;

    private static String[] TITLES = {
            AppController.getInstance().getString(R.string.schools_tab_title_pn),
            AppController.getInstance().getString(R.string.schools_tab_title_kg)
    };

    public SchoolsPagerAdapter(FragmentManager fm) {
        super(fm);
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
            case PN_PAGE:
                pnFragment = new SchoolsPNFragment();
                return pnFragment;
            case KG_PAGE:
            default:
                kgFragment = new SchoolsKGFragment();
                return kgFragment;
        }
    }

    public TrackedFragment getFragment(int position) {
        switch (position) {
            case PN_PAGE:
                return pnFragment;
            case KG_PAGE:
            default:
                return kgFragment;
        }
    }
}
