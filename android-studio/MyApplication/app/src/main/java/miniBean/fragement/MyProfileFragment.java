package miniBean.fragement;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.HeaderDataVM;
import miniBean.viewmodel.NotificationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyProfileFragment extends Fragment {

    public List<NotificationVM> requestNotif, notifAll;
    private ImageView setting, back;
    private ViewGroup request, notification;
    private TextView requestCount, notificationCount;
    Gson gson = new Gson();
    View actionBarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.my_profile_fragement, container, false);

        setHasOptionsMenu(true);

        actionBarView = inflater.inflate(R.layout.my_profile_actionbar, null);

        request = (ViewGroup) actionBarView.findViewById(R.id.requestLayout);
        notification = (ViewGroup) actionBarView.findViewById(R.id.notificationLayout);
        requestCount = (TextView) actionBarView.findViewById(R.id.requestCount);
        notificationCount = (TextView) actionBarView.findViewById(R.id.notificationCount);
        setting = (ImageView) actionBarView.findViewById(R.id.setting);

        back = (ImageView) actionBarView.findViewById(R.id.backAction);
        back.setVisibility(View.INVISIBLE);

        requestCount.setVisibility(View.INVISIBLE);
        notificationCount.setVisibility(View.INVISIBLE);

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        getActivity().getActionBar().setCustomView(actionBarView, lp);
        getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        AppController.api.getHeaderBarData(AppController.getInstance().getSessionId(), new Callback<HeaderDataVM>() {
            @Override
            public void success(HeaderDataVM headerDataVM, Response response) {
                setHeaderBarData(headerDataVM);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace(); //to see if you have errors

            }
        });

        return view;
    }

    private void setHeaderBarData(HeaderDataVM headerDataVM) {
        Log.d(MyProfileFragment.this.getClass().getSimpleName(), "getHeaderBarData.success: user=" + headerDataVM.getName() + " request=" + headerDataVM.getRequestCounts() + " notif=" + headerDataVM.getNotifyCounts());

        requestNotif = headerDataVM.getRequestNotif();
        notifAll = headerDataVM.getAllNotif();

        if (headerDataVM.getRequestCounts() == 0) {
            requestCount.setVisibility(View.INVISIBLE);
        } else {
            requestCount.setVisibility(View.VISIBLE);
            requestCount.setText(headerDataVM.getRequestCounts() + "");
        }

        if (headerDataVM.getNotifyCounts() == 0) {
            notificationCount.setVisibility(View.INVISIBLE);
        } else {
            notificationCount.setVisibility(View.VISIBLE);
            notificationCount.setText(headerDataVM.getNotifyCounts() + "");
        }

        Fragment profileFragment = new ProfileFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, profileFragment).commit();

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.INVISIBLE);
                ((TextView) actionBarView.findViewById(R.id.title)).setText(getString(R.string.request_actionbar_title));

                Fragment requestFragment = new RequestListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("requestNotif", gson.toJson(requestNotif));
                requestFragment.setArguments(bundle);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.children_fragement, requestFragment).commit();
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.INVISIBLE);
                ((TextView) actionBarView.findViewById(R.id.title)).setText(getString(R.string.notification_actionbar_title));

                Fragment notificactionFragment = new NotificationListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("notifAll", gson.toJson(notifAll));
                notificactionFragment.setArguments(bundle);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.children_fragement, notificactionFragment).commit();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.setVisibility(View.INVISIBLE);
                notification.setVisibility(View.INVISIBLE);
                setting.setVisibility(View.INVISIBLE);
                ((TextView) actionBarView.findViewById(R.id.title)).setText("Settings");

                Fragment settingFragment = new LogoutFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.children_fragement, settingFragment).commit();

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
