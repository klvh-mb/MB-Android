package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.activity.CommunityActivity;
import miniBean.adapter.CommunityListAdapter;
import miniBean.app.LocalCommunityTabCache;
import miniBean.app.TrackedFragment;
import miniBean.util.DefaultValues;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.CommunitiesWidgetChildVM;

public class CommunityListFragment extends TrackedFragment {

    private static final String TAG = CommunityListFragment.class.getName();
    private ListView listView;
    private CommunityListAdapter listAdapter;
    private List<CommunitiesWidgetChildVM> communities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.community_list_fragment, container, false);

        ViewUtil.showSpinner(getActivity());

        listView = (ListView) view.findViewById(R.id.listComm);

        // Tricky... listview header and footer dividers need to add in code...
        listView.addHeaderView(new View(getActivity().getBaseContext()), null, true);
        listView.addFooterView(new View(getActivity().getBaseContext()), null, true);

        communities = new ArrayList<>();

        listAdapter = new CommunityListAdapter(getActivity(), communities);
        listView.setAdapter(listAdapter);
        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - 1;    // offset by header
                CommunitiesWidgetChildVM childVM = listAdapter.getItem(position);
                if (childVM != null) {
                    Log.d(this.getClass().getSimpleName(), "onCreateView: listView.onItemClick with commId - " + childVM.getId());
                    Intent intent = new Intent(getActivity(), CommunityActivity.class);
                    intent.putExtra("id", childVM.getId());
                    intent.putExtra("flag", "FromCommunityFragment");
                    startActivity(intent);
                }
            }
        });

        LocalCommunityTabCache.setMyCommunityFragment(this);
        if (LocalCommunityTabCache.getMyCommunities() != null) {
            Log.d(this.getClass().getSimpleName(), "onCreateView: reload my communities from LocalCache");
            notifyChange(LocalCommunityTabCache.getMyCommunities().getCommunities());
        } else {
            LocalCommunityTabCache.refreshMyCommunities();
        }

        return view;
    }

    public void notifyChange(List<CommunitiesWidgetChildVM> communities) {
        this.communities.clear();
        this.communities.addAll(communities);
        listAdapter.notifyDataSetChanged();
        ViewUtil.stopSpinner(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        if(LocalCommunityTabCache.getMyCommunities() != null) {
            Log.d(this.getClass().getSimpleName(), "onResume: my communities size - " + LocalCommunityTabCache.getMyCommunities().getCommunities().size());
            notifyChange(LocalCommunityTabCache.getMyCommunities().getCommunities());
        }
    }
}
