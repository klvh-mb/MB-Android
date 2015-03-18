package miniBean.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.activity.CommunityActivity;
import miniBean.adapter.CommunityListAdapter;
import miniBean.app.AppController;
import miniBean.app.LocalCache;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class CommunityFragment extends Fragment {

    private static final String TAG = CommunityFragment.class.getName();
    ProgressBar progressBarComm;
    private ListView listView;
    private CommunityListAdapter listAdapter;
    private List<CommunitiesWidgetChildVM> communityItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.community_list_view, container, false);

        listView = (ListView) view.findViewById(R.id.listComm);

        // Tricky... listview header and footer dividers need to add in code...
        listView.addHeaderView(new View(getActivity().getBaseContext()), null, true);
        listView.addFooterView(new View(getActivity().getBaseContext()), null, true);

        progressBarComm = (ProgressBar) view.findViewById(R.id.progressComm1);
        progressBarComm.setVisibility(View.VISIBLE);

        communityItems = new ArrayList<>();

        listAdapter = new CommunityListAdapter(getActivity(), communityItems);
        listView.setAdapter(listAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CommunityActivity.class);

                String noMember, commId, name;

                CommunitiesWidgetChildVM childVM = listAdapter.getItem(position);

                commId = childVM.getId().toString();
                noMember = childVM.getMm().toString();
                name = childVM.getDn();
                System.out.println("idchecked 0:::"+commId);
                intent.putExtra("id", commId);
                intent.putExtra("noMember", noMember);
                intent.putExtra("commName", name);
                intent.putExtra("icon", childVM.getGi());
                intent.putExtra("isM", childVM.getIsM());
                intent.putExtra("flag","FromCommunityFragment");
                startActivity(intent);
            }
        });


        System.out.println("Before getCommunity");
        if (LocalCache.getMyCommunitiesParentVM() != null) {
            System.out.println("in localcache:::::::::::");
            communityItems.addAll(LocalCache.getMyCommunitiesParentVM().getCommunities());
            listAdapter.notifyDataSetChanged();
            progressBarComm.setVisibility(View.GONE);
        } else {
            getCommunity();
        }
        System.out.println("After getCommunity");
        return view;
    }

    private void getCommunity() {
        System.out.println("In getCommunity");
        AppController.api.getMyCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>() {
            @Override
            public void success(CommunitiesParentVM array, retrofit.client.Response response) {
                filterMyCommunities(array);
                LocalCache.setMyCommunitiesParentVM(array);
                communityItems.addAll(array.getCommunities());
                listAdapter.notifyDataSetChanged();
                progressBarComm.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors

            }
        });
    }

    private void filterMyCommunities(CommunitiesParentVM array) {
        for (int i = array.communities.size()-1; i > 0; i--) {
            if (DefaultValues.FILTER_MY_COMM_TYPE.contains(array.communities.get(i).tp) ||
                    DefaultValues.FILTER_MY_COMM_TARGETING_INFO.contains(array.communities.get(i).tinfo)) {
                System.out.println("Filtered myCommunity - " + array.communities.get(i).dn);
                array.communities.remove(i);
            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        if(LocalCache.getMyCommunitiesParentVM() != null) {
            System.out.println("onresume::::::::" + LocalCache.getMyCommunitiesParentVM().getCommunities().size());
            communityItems.clear();
            communityItems.addAll(LocalCache.getMyCommunitiesParentVM().getCommunities());
            listAdapter.notifyDataSetChanged();
            progressBarComm.setVisibility(View.GONE);
        }

    }
}
