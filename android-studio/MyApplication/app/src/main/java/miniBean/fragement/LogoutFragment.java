package miniBean.fragement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.activity.ActivityMain;
import miniBean.activity.LoginActivity;
import miniBean.adapter.RequestListAdapter;
import miniBean.app.MyApi;
import miniBean.viewmodel.NotificationVM;
import retrofit.RestAdapter;
import retrofit.client.OkClient;


public class LogoutFragment extends Fragment {
    private static final String TAG = LogoutFragment.class.getName();
    public SharedPreferences session = null;
    public MyApi api;
    RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.logout, container, false);
        session = getActivity().getSharedPreferences("prefs", 0);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();

        relativeLayout= (RelativeLayout) view.findViewById(R.id.logout);
        api = restAdapter.create(MyApi.class);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = session.edit();
                editor.remove("sessionID");
                editor.commit();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });
        return view;
    }
}
