package miniBean.fragement;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.readystatesoftware.viewbadger.BadgeView;

import java.lang.reflect.Field;
import java.util.List;

import miniBean.R;
import miniBean.activity.ActivityMain;
import miniBean.app.AppController;
import miniBean.app.MyApi;
import miniBean.viewmodel.HeaderDataVM;
import miniBean.viewmodel.NotificationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyProfileFragment extends Fragment {

    public List<NotificationVM> requestNotif, notifAll;
    public MyApi api;
    public SharedPreferences session = null;
    ImageView request, notification,setting,back;
    Gson gson = new Gson();
    View actionBarView;
    BadgeView notifyBadge, requestBadge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.myprofile_fragement, container, false);
        session = getActivity().getSharedPreferences("prefs", 0);


        setHasOptionsMenu(true);

        actionBarView = inflater.inflate(R.layout.profile_actionbar,null);

        request = (ImageView) actionBarView.findViewById(R.id.bookmarkedtAction);

        notification = (ImageView) actionBarView.findViewById(R.id.moreAction);

        setting= (ImageView) actionBarView.findViewById(R.id.setting);

        back= (ImageView) actionBarView.findViewById(R.id.backAction);
        back.setVisibility(View.INVISIBLE);
        ((ActivityMain) getActivity()).getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT ,ActionBar.LayoutParams.MATCH_PARENT);

        ((ActivityMain) getActivity()).getActionBar().setCustomView(actionBarView,lp);

        AppController.api.getHeaderBaeData(session.getString("sessionID", null), new Callback<HeaderDataVM>() {
            @Override
            public void success(HeaderDataVM headerDataVM, Response response) {
                System.out.println("headerdata" + headerDataVM.getName());
                requestNotif = headerDataVM.getRequestNotif();
                notifAll = headerDataVM.getAllNotif();
                getHeaderBarData();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace(); //to see if you have errors

            }
        });


        return view;
    }

    void getHeaderBarData() {

        Fragment profileFragment = new ProfileFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.children_fragement, profileFragment).commit();


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.INVISIBLE);
                setting.setVisibility(View.INVISIBLE);
                ((TextView) actionBarView.findViewById(R.id.titleAction)).setText("Request");
                Fragment requestFragment = new RequestFragment();
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
                setting.setVisibility(View.INVISIBLE);
                ((TextView) actionBarView.findViewById(R.id.titleAction)).setText("Notification");
                Fragment notificactionFragment = new NotificationFragment();
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
                ((TextView) actionBarView.findViewById(R.id.titleAction)).setText("Settings");
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
