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
    private List<CommunitiesWidgetChildVM> communities;

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

        communities = new ArrayList<>();

        listAdapter = new CommunityListAdapter(getActivity(), communities);
        listView.setAdapter(listAdapter);
        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CommunityActivity.class);

                String noMember, commId, name;

                CommunitiesWidgetChildVM childVM = listAdapter.getItem(position - 1);

                commId = childVM.getId().toString();
                noMember = childVM.getMm().toString();
                name = childVM.getDn();

                Log.d(this.getClass().getSimpleName(), "onCreateView: listView.onItemClick with commId - " + commId);
                intent.putExtra("id", commId);
                intent.putExtra("noMember", noMember);
                intent.putExtra("commName", name);
                intent.putExtra("icon", childVM.getGi());
                intent.putExtra("isM", childVM.getIsM());
                intent.putExtra("flag","FromCommunityFragment");
                startActivity(intent);
            }
        });

        LocalCache.setMyCommunityFragment(this);
        if (LocalCache.getMyCommunitiesParentVM() != null) {
            Log.d(this.getClass().getSimpleName(), "onCreateView: reload my communities from LocalCache");
            notifyChange(LocalCache.getMyCommunitiesParentVM().getCommunities());
        } else {
            LocalCache.refreshMyCommunities();
            //getMyCommunities();
        }

        return view;
    }

    private void getMyCommunities() {
        AppController.api.getMyCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>() {
            @Override
            public void success(CommunitiesParentVM array, retrofit.client.Response response) {
                LocalCache.setMyCommunitiesParentVM(array);
                notifyChange(array.communities);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors

            }
        });
    }

    public void notifyChange(List<CommunitiesWidgetChildVM> communities) {
        this.communities.clear();
        this.communities.addAll(communities);
        listAdapter.notifyDataSetChanged();
        progressBarComm.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(LocalCache.getMyCommunitiesParentVM() != null) {
            Log.d(this.getClass().getSimpleName(), "onResume: my communities size - " + LocalCache.getMyCommunitiesParentVM().getCommunities().size());
            notifyChange(LocalCache.getMyCommunitiesParentVM().getCommunities());
        }
    }
}
