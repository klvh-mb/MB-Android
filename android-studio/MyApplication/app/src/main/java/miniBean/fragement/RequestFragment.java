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

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.RequestListAdapter;
import miniBean.app.MyApi;
import miniBean.viewmodel.NotificationVM;
import retrofit.RestAdapter;
import retrofit.client.OkClient;


public class RequestFragment extends Fragment {

    private static final String TAG = RequestFragment.class.getName();
    public SharedPreferences session = null;
    public MyApi api;
    RequestListAdapter adapter;
    private ListView listView;
    private List<NotificationVM> requestItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.request_list_view, container, false);
        session = getActivity().getSharedPreferences("prefs", 0);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();


        api = restAdapter.create(MyApi.class);

        requestItems = new ArrayList<NotificationVM>();

        listView = (ListView) view.findViewById(R.id.listRequest);

        String notif = getArguments().getString("requestNotif");
        System.out.println("REQnotice::::::::::" + notif);

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

        adapter = new RequestListAdapter(getActivity(), notificationVMs);
        listView.setAdapter(adapter);

        return view;
    }

}
