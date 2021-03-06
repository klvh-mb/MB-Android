package miniBean.fragment;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.List;

import miniBean.R;
import miniBean.activity.MyProfileActionActivity;
import miniBean.app.NotificationCache;
import miniBean.app.TrackedFragment;
import miniBean.viewmodel.NotificationVM;
import miniBean.viewmodel.NotificationsParentVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyProfileFragment extends TrackedFragment {

    public List<NotificationVM> requestNotif, notifAll;
    private ImageView back;
    private ViewGroup request, notification,message;
    private TextView requestCount, notificationCount,messageCount;
    private View actionBarView;

    private Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.my_profile_fragement, container, false);

        actionBarView = inflater.inflate(R.layout.my_profile_actionbar, null);

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        getActivity().getActionBar().setCustomView(actionBarView, lp);
        getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActivity().getActionBar().show();

        setHasOptionsMenu(true);

        request = (ViewGroup) actionBarView.findViewById(R.id.requestLayout);
        notification = (ViewGroup) actionBarView.findViewById(R.id.notificationLayout);
        requestCount = (TextView) actionBarView.findViewById(R.id.requestCount);
        notificationCount = (TextView) actionBarView.findViewById(R.id.notificationCount);
        messageCount = (TextView) actionBarView.findViewById(R.id.messageCount);
        message = (ViewGroup) actionBarView.findViewById(R.id.messageLayout);

        back = (ImageView) actionBarView.findViewById(R.id.backAction);
        back.setVisibility(View.INVISIBLE);

        requestCount.setVisibility(View.INVISIBLE);
        notificationCount.setVisibility(View.INVISIBLE);
        messageCount.setVisibility(View.INVISIBLE);

        TrackedFragment fragment = new ProfileFragment();
        fragment.setTrackedOnce();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, fragment,"profile").commit();

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActionActivity.class);
                intent.putExtra("key","requests");
                intent.putExtra("requestNotif", gson.toJson(requestNotif));
                startActivity(intent);

                /*
                back.setVisibility(View.INVISIBLE);
                ((TextView) actionBarView.findViewById(R.id.title)).setText(getString(R.string.request_actionbar_title));

                Fragment requestFragment = new RequestListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("requestNotif", gson.toJson(requestNotif));
                requestFragment.setArguments(bundle);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.children_fragement, requestFragment).commit();
                */
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActionActivity.class);
                intent.putExtra("key","notifications");
                intent.putExtra("notifAll", gson.toJson(notifAll));
                startActivity(intent);

                /*
                back.setVisibility(View.INVISIBLE);
                ((TextView) actionBarView.findViewById(R.id.title)).setText(getString(R.string.notification_actionbar_title));

                Fragment notificactionFragment = new NotificationListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("notifAll", gson.toJson(notifAll));
                notificactionFragment.setArguments(bundle);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.children_fragement, notificactionFragment).commit();
                */
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActionActivity.class);
                intent.putExtra("key", "messages");
                intent.putExtra("notifAll", gson.toJson(notifAll));
                startActivity(intent);
            }
        });
                return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        NotificationCache.refresh(new Callback<NotificationsParentVM>() {
            @Override
            public void success(NotificationsParentVM notificationsParentVM, Response response) {
                setHeaderBarData(notificationsParentVM);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void setHeaderBarData(NotificationsParentVM notificationsParentVM) {
        requestNotif = notificationsParentVM.getRequestNotif();
        notifAll = notificationsParentVM.getAllNotif();

        if (notificationsParentVM.getRequestCounts() == 0) {
            requestCount.setVisibility(View.INVISIBLE);
        } else {
            requestCount.setVisibility(View.VISIBLE);
            requestCount.setText(notificationsParentVM.getRequestCounts() + "");
        }

        if (notificationsParentVM.getNotifyCounts() == 0) {
            notificationCount.setVisibility(View.INVISIBLE);
        } else {
            notificationCount.setVisibility(View.VISIBLE);
            notificationCount.setText(notificationsParentVM.getNotifyCounts() + "");
        }

        if(notificationsParentVM.getMessageCount() == 0) {
            messageCount.setVisibility(View.INVISIBLE);
        }else{
            messageCount.setVisibility(View.VISIBLE);
            messageCount.setText(notificationsParentVM.getMessageCount() + "");
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Fragment fragment = (Fragment) getChildFragmentManager().findFragmentByTag("profile");
        if(fragment != null){
            fragment.onActivityResult(requestCode, resultCode, intent);
        }
    }
}
