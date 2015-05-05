package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.activity.CommunityActivity;
import miniBean.adapter.RequestListAdapter;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.util.UrlUtil;
import miniBean.viewmodel.NotificationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestListFragment extends Fragment {

    private static final String TAG = RequestListFragment.class.getName();
    RequestListAdapter adapter;
    private ListView listView;
    private TextView tipText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.request_list_fragment, container, false);

        listView = (ListView) view.findViewById(R.id.listRequest);
        tipText = (TextView) view.findViewById(R.id.tipText);

        String notif = getArguments().getString("requestNotif");

        StringBuilder ids = new StringBuilder();

        Gson gson = new GsonBuilder().create();
        List<NotificationVM> notificationVMs = new ArrayList<>();
        JSONArray jsonArray1 = null;
        try {
            jsonArray1 = new JSONArray(notif);
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject json_data = jsonArray1.getJSONObject(i);
                NotificationVM vm = gson.fromJson(json_data.toString(), NotificationVM.class);
                notificationVMs.add(vm);

                if(vm.getSta()==0) {
                    if (i != 0) {
                        ids.append(",");
                        }
                    ids.append(vm.getNid());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(ids.length()!=0)
            markAsRead(ids.toString());


        if(notificationVMs.size() == 0){
            tipText.setVisibility(View.VISIBLE);
        }else {
            adapter = new RequestListAdapter(getActivity(), notificationVMs);
            listView.setAdapter(adapter);
        }

        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationVM item = adapter.getItem(position);
                if (item != null) {
                    /*
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "Status:" + item.getSta());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "Msg:"+item.getMsg());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "Type:"+item.getTp());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "Update:"+item.getUpd());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "OnClick:"+item.getUrl().getOnClick());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "actor:"+item.getUrl().getActor());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "target:"+item.getUrl().getTarget());
                    */

                    if (item.getTp().equals("COMM_JOIN_APPROVED")) {
                        try {
                            long commId = UrlUtil.parseCommunityUrlId(item.getUrl().getOnClick());
                            Log.d(RequestListFragment.this.getClass().getSimpleName(), "click request: commId="+commId);

                            Intent intent = new Intent(getActivity(), CommunityActivity.class);
                            intent.putExtra("id", commId);
                            intent.putExtra("flag", "FromRequest");
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(RequestListFragment.this.getClass().getSimpleName(), "Failed to parse comm id from url", e);
                        }
                    }

                    /*if(item.getTp().equals("FRD_REQUEST")){
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        intent.putExtra("id",item.getUrl().getTarget());
                        startActivity(intent);
                    }*/
                }
            }
        });

        return view;
    }
    private void markAsRead(String ids){
        AppController.getApi().markAsRead(ids,AppController.getInstance().getSessionId(),new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                    error.printStackTrace();
            }
        });
    }

}
