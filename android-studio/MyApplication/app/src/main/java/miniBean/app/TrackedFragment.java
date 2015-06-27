package miniBean.app;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.util.ViewUtil;

public abstract class TrackedFragment extends Fragment {

    public static final int PAGER_INDICATOR_DOT_DIMENSION = 8;

    protected boolean tracked = false;

    protected boolean trackedOnce = false;

    protected EasyTracker tracker;

    // UI helper
    protected List<ImageView> dots = new ArrayList<>();

    public void setTracked() {
        this.tracked = true;
    }

    public void setTrackedOnce() {
        this.trackedOnce = true;
    }

    protected EasyTracker getTracker() {
        if (tracker == null)
            tracker = EasyTracker.getInstance(getActivity());
        return tracker;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // NOTE: init tracker here
        getTracker();
    }

    /**
     * https://developers.google.com/analytics/devguides/collection/android/v3/migration
     */
    @Override
    public void onResume() {
        super.onResume();

        if (tracked || trackedOnce) {
            trackFragmentShow();
            trackedOnce = false;
        }
    }

    protected void trackFragmentShow() {
        Log.d(this.getClass().getSimpleName(), "[DEBUG] fragment show");
        getTracker().set(Fields.SCREEN_NAME, getClass().getSimpleName());
        getTracker().send(MapBuilder.createAppView().build());
    }

    //
    // UI helper
    //

    /**
     * Checked by MainActivity
     *
     * @return
     */
    public boolean allowBackPressed() {
        return true;
    }

    protected void addDots(final int numPages, LinearLayout dotsLayout, ViewPager viewPager) {
        if (dotsLayout == null) {
            return;
        }

        dots = new ArrayList<>();
        dotsLayout.removeAllViews();

        int imageResource = R.drawable.ic_dot_sel;      // select the first dot by default
        for(int i = 0; i < numPages; i++) {
            ImageView dot = new ImageView(this.getActivity());
            dot.setImageDrawable(getResources().getDrawable(imageResource));

            int dimension = ViewUtil.getRealDimension(PAGER_INDICATOR_DOT_DIMENSION, this.getResources());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimension, dimension);
            params.gravity = Gravity.CENTER_VERTICAL;
            dotsLayout.addView(dot, params);

            dots.add(dot);
            imageResource = R.drawable.ic_dot;
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position, numPages);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    protected void selectDot(int index, int numPages) {
        if (dots.size() == 0) {
            return;
        }

        Resources res = getResources();
        for (int i = 0; i < numPages; i++) {
            int drawableId = (i == index)? R.drawable.ic_dot_sel : R.drawable.ic_dot;
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }
}
