package miniBean.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 * Created by Tobias on 29.12.2014.
 */

public abstract class AbstractFragment extends Fragment {

    private static final String TAG = AbstractFragment.class.getName();

    /****************************************************************************************
     * Initialize the Facebook UI Helper
     *
     * @author huber
     * @since Dec 30, 2014
     */

    protected UiLifecycleHelper uiHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        }
    };

    /**
     * Facebook UI Helper Initializiation - END
     ****************************************************************************************/

    /******************************************************************************
     * Access to SAOs - BEGIN
     *
     * Access the different Sao Services. One Sao Service per fragment
     *
     * It is important to use Singletons here, otherwise the CacheChangedListener won't work, because the list is always empty.
     *
     */


    /**
     * Access to SAOs - END
     ****************************************************************************/
}
