package miniBean.fragement;

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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.activity.CommunityActivity;
import miniBean.activity.DetailActivity;
import miniBean.adapter.NotificationListAdapter;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.util.UrlUtil;
import miniBean.viewmodel.NotificationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationListFragment extends Fragment {

    private static final String TAG = NotificationListFragment.class.getName();
    NotificationListAdapter adapter;
    private ListView listView;
    private TextView tipText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.notification_list_fragment, container, false);

        listView = (ListView) view.findViewById(R.id.listNotification);
        tipText = (TextView) view.findViewById(R.id.tipText);

        String notif = getArguments().getString("notifAll");

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

        if(ids.length() != 0)
            markAsRead(ids.toString());

        if(notificationVMs.size() == 0){
            tipText.setVisibility(View.VISIBLE);
        } else {
            adapter = new NotificationListAdapter(getActivity(), notificationVMs);
            listView.setAdapter(adapter);
        }

        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationVM item = adapter.getItem(position);
                if (item != null) {
                    try {
                        long postId = UrlUtil.parsePostLandingUrlId(item.getUrl().getOnClick());
                        long commId = UrlUtil.parsePostLandingUrlCommId(item.getUrl().getOnClick());
                        Log.d(NotificationListFragment.this.getClass().getSimpleName(), "click notif: postId="+postId+" commId="+commId);

                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra("postId", postId);
                        intent.putExtra("commId", commId);
                        intent.putExtra("flag", "FromRequest");
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(NotificationListFragment.this.getClass().getSimpleName(), "Failed to parse comm id from url", e);
                    }

                    /*
                    if (item.getTp().equals("POSTED") ||
                            item.getTp().equals("POSTED_QUESTION") ||
                            item.getTp().equals("COMMENTED") ||
                            item.getTp().equals("ANSWERED") ||
                            item.getTp().equals("WANT_ANS") ||
                            item.getTp().equals("LIKED")) {
                        try {
                            long postId = UrlUtil.parsePostLandingUrlId(item.getUrl().getOnClick());
                            long commId = UrlUtil.parsePostLandingUrlCommId(item.getUrl().getOnClick());
                            Log.d(NotificationListFragment.this.getClass().getSimpleName(), "click notif: postId="+postId+" commId="+commId);

                            Intent intent = new Intent(getActivity(), DetailActivity.class);
                            intent.putExtra("postId", postId);
                            intent.putExtra("commId", commId);
                            intent.putExtra("flag", "FromRequest");
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(NotificationListFragment.this.getClass().getSimpleName(), "Failed to parse comm id from url", e);
                        }
                    }
                    */
                }
            }
        });

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
