package miniBean.app;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;

public abstract class TrackedFragmentActivity extends FragmentActivity {

    protected EasyTracker tracker;

    protected EasyTracker getTracker() {
        if (tracker == null)
            tracker = EasyTracker.getInstance(this);
        return tracker;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(this.getClass().getSimpleName(), "[DEBUG] activityStart");
        getTracker().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(this.getClass().getSimpleName(), "[DEBUG] activityStop");
        getTracker().activityStop(this);
    }
}
