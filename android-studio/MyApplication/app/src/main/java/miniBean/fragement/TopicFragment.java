package miniBean.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import miniBean.activity.CommunityActivity;
import miniBean.R;
import miniBean.adapter.TopicAdapter;
import miniBean.viewmodel.CommunitiesWidgetChildVM;

public class TopicFragment extends Fragment {

    public ListView listView;
    public TopicAdapter topicAdapter;
    public List<CommunitiesWidgetChildVM> communities = new ArrayList<CommunitiesWidgetChildVM>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.topic_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.listTopic);
        topicAdapter = new TopicAdapter(getActivity(), this.communities);
        listView.setAdapter(topicAdapter);
        topicAdapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent = new Intent(getActivity(), CommunityActivity.class);

                String noPost = "100";

                CommunitiesWidgetChildVM childVM = topicAdapter.getItem(position);

                intent.putExtra("id", childVM.getId().toString());
                intent.putExtra("noMember", childVM.getMm().toString());
                intent.putExtra("noPost", noPost);
                intent.putExtra("commName", childVM.getDn());
                intent.putExtra("icon", childVM.getGi());
                intent.putExtra("isM", childVM.getIsM());

                startActivity(intent);


            }
        });
        return rootView;
    }

    public List<CommunitiesWidgetChildVM> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunitiesWidgetChildVM> communities) {
        this.communities = communities;
    }


}
