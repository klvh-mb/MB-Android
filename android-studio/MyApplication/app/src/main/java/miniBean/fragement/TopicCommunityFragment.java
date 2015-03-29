package miniBean.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import miniBean.R;
import miniBean.activity.CommunityActivity;
import miniBean.adapter.TopicCommunityListAdapter;
import miniBean.app.LocalCommunityTabCache;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunitiesWidgetChildVM;

public class TopicCommunityFragment extends Fragment {

    public ListView listView;
    public TopicCommunityListAdapter topicAdapter;
    public List<CommunitiesWidgetChildVM> communities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.topic_community_fragment, container, false);

        listView = (ListView) rootView.findViewById(R.id.listTopic);

        // Tricky... listview header and footer dividers need to add in code...
        listView.addHeaderView(new View(getActivity().getBaseContext()), null, true);
        listView.addFooterView(new View(getActivity().getBaseContext()), null, true);

        topicAdapter = new TopicCommunityListAdapter(getActivity(), this.communities);
        listView.setAdapter(topicAdapter);
        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String noPost = "-";

                position = position - 1;    // offset by header
                CommunitiesWidgetChildVM childVM = topicAdapter.getItem(position);
                if (childVM != null) {
                    Intent intent = new Intent(getActivity(), CommunityActivity.class);
                    intent.putExtra("id", childVM.getId().toString());
                    intent.putExtra("noMember", childVM.getMm().toString());
                    intent.putExtra("noPost", noPost);
                    intent.putExtra("commName", childVM.getDn());
                    intent.putExtra("icon", childVM.getGi());
                    intent.putExtra("isM", childVM.getIsM());
                    intent.putExtra("flag", "FromTopicFragment");
                    startActivity(intent);

                    // getFragmentManager().beginTransaction().remove(TopicFragment.this).commit();
                }
            }
        });

        LocalCommunityTabCache.addTopicCommunityFragment(this);

        return rootView;
    }

    public void notifyChange() {
        topicAdapter.notifyDataSetChanged();
        //progressBarComm.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public List<CommunitiesWidgetChildVM> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunitiesWidgetChildVM> communities) {
        this.communities = communities;
    }
}
