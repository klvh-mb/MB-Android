package miniBean.fragement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import miniBean.MyApi;
import miniBean.R;
import miniBean.adapter.CommunityListAdapter;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;


public class CommunityFragment extends Fragment {

    private static final String TAG = CommunityFragment.class.getName();
    public SharedPreferences session = null;
    public MyApi api;
    private ListView listView;
    private CommunityListAdapter listAdapter;
    private List<CommunitiesWidgetChildVM> communityItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.community_activity, container, false);
        session = getActivity().getSharedPreferences("prefs", 0);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();

        api = restAdapter.create(MyApi.class);

        listView = (ListView) view.findViewById(R.id.listComm);

        communityItems = new ArrayList<>();

        listAdapter = new CommunityListAdapter(getActivity(), communityItems);
        listView.setAdapter(listAdapter);
        System.out.println("Before getCommunity");
        getCommunity();
        System.out.println("After getCommunity");
        return view;
    }

    private void getCommunity() {
        System.out.println("In getCommunity");
        api.getMyCommunities(session.getString("sessionID", null), new Callback<CommunitiesParentVM>() {
            @Override
            public void success(CommunitiesParentVM array, retrofit.client.Response response) {
                communityItems.addAll(array.getCommunities());
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors

            }
        });
    }


}
