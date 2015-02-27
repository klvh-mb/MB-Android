package miniBean.fragement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.NotificationListAdapter;
import miniBean.app.MyApi;
import miniBean.viewmodel.NotificationVM;
import retrofit.RestAdapter;
import retrofit.client.OkClient;


public class NotificationFragment extends Fragment {

    private static final String TAG = NotificationFragment.class.getName();
    public SharedPreferences session = null;
    public MyApi api;
    NotificationListAdapter adapter;
    private ListView listView;
    private List<NotificationVM> notificationItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.notificaction_list_view, container, false);
        session = getActivity().getSharedPreferences("prefs", 0);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();

        api = restAdapter.create(MyApi.class);

        notificationItems = new ArrayList<NotificationVM>();

        String notif = getArguments().getString("notifAll");
        Gson gson = new GsonBuilder().create();
        List<NotificationVM> notificationVMs = new ArrayList<>();
        JSONArray jsonArray1 = null;
        try {
            jsonArray1 = new JSONArray(notif);
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject json_data = jsonArray1.getJSONObject(i);
                NotificationVM vm = gson.fromJson(json_data.toString(), NotificationVM.class);
                notificationVMs.add(vm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView = (ListView) view.findViewById(R.id.listNotification);
        adapter = new NotificationListAdapter(getActivity(), notificationVMs);
        listView.setAdapter(adapter);
        return view;
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
