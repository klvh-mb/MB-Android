package miniBean.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import miniBean.fragement.NewsFeedFragement;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new NewsFeedFragement();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of main_fragement
        return 3;
    }

}
