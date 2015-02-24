package miniBean.fragement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import miniBean.app.MyApi;
import miniBean.R;
import miniBean.adapter.DetailListAdapter;
import miniBean.viewmodel.Comment;
import retrofit.RestAdapter;
import retrofit.client.OkClient;


public class DetailFragment extends Fragment {

    private static final String TAG = DetailFragment.class.getName();
    public SharedPreferences session = null;
    public MyApi api;
    private ListView listView;
    private DetailListAdapter listAdapter;
    private List<Comment> communityItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.detail_activity, container, false);
        session = getActivity().getSharedPreferences("prefs", 0);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();

        api = restAdapter.create(MyApi.class);

        listView = (ListView) view.findViewById(R.id.list);


        communityItems = new ArrayList<>();

        listAdapter = new DetailListAdapter(getActivity(), communityItems);
        listView.setAdapter(listAdapter);

        Intent intent = getActivity().getIntent();

        String postID = intent.getStringExtra("postID");
        String commID = intent.getStringExtra("commID");


        System.out.println("Before getCommunity");
        getCommunityDetail();
        System.out.println("After getCommunity");
        return view;
    }

    private void getCommunityDetail() {
        System.out.println("In getCommunity");
        Intent intent = getActivity().getIntent();

        String postID = intent.getStringExtra("postID");
        String commID = intent.getStringExtra("commID");

        Long cId, feedId;
        cId = Long.parseLong(commID);
        feedId = Long.parseLong(postID);

      /* api.qnaLanding(feedId,cId, new Callback<CommunityPostVM>() {
            @Override
            public void success(CommunityPostVM array, retrofit.client.Response response) {
                communityItems.addAll(array.getCs());
                listAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors

            }
        });*/
    }


}
