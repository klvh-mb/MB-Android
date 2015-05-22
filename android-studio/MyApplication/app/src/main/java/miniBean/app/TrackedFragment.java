package miniBean.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public abstract class TrackedFragment extends Fragment {

    protected EasyTracker tracker;

    protected EasyTracker getTracker() {
        if (tracker == null)
            tracker = EasyTracker.getInstance(getActivity());
        return tracker;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * https://developers.google.com/analytics/devguides/collection/android/v3/migration
     */
    @Override
    public void onResume() {
        super.onResume();

        Log.d(this.getClass().getSimpleName(), "[DEBUG] fragment resumes");
        getTracker().set(Fields.SCREEN_NAME, getClass().getSimpleName());
        getTracker().send(MapBuilder.createAppView().build());
    }

    /**
     * Checked by MainActivity
     *
     * @return
     */
    public boolean allowBackPressed() {
        return true;
    }
}
