package miniBean.app;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;

public class TrackedFragmentActivity extends FragmentActivity {

    @Override
    public void onStart() {
        super.onStart();
        Log.d(this.getClass().getSimpleName(), "[DEBUG] activityStart");
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(this.getClass().getSimpleName(), "[DEBUG] activityStop");
        EasyTracker.getInstance(this).activityStop(this);
    }
}
