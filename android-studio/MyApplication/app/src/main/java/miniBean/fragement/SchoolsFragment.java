package miniBean.fragement;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
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

public class SchoolsFragment extends Fragment {

    private static final String TAG = SchoolsFragment.class.getName();
    private ActionBar.Tab Tab1, Tab2, Tab3;
    private ViewPager viewPager;
    private SchoolPagerAdapter mAdapter;
    private PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.schools_main_fragement, container, false);

        getActivity().getActionBar().hide();

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new SchoolPagerAdapter(getChildFragmentManager());

        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);
        viewPager.setAdapter(mAdapter);

        tabs.setViewPager(viewPager);
        tabs.setTextColor(getResources().getColor(R.color.dark_gray));
        tabs.setIndicatorColor(Color.parseColor("#57B154"));

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

class SchoolPagerAdapter extends FragmentPagerAdapter {

    private static String[] TITLES={"Prenursery","Kindy"};

    public SchoolPagerAdapter(FragmentManager fm) {
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
            case 0:
                return new MySchoolsFragment();
            default:
                return new MyKindyFragment();
        }
    }
}
