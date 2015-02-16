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

import miniBean.CommunityActivity;
import miniBean.MyApi;
import miniBean.R;
import miniBean.adapter.FeedListAdapter;
import miniBean.adapter.TopicAdapter;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.Post;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class TopicFragment extends Fragment {

    public ListView listView;
    public TopicAdapter topicAdapter;
    public List<CommunitiesWidgetChildVM> communities =  new ArrayList<CommunitiesWidgetChildVM>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.topic_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.listTopic);
        topicAdapter = new TopicAdapter(getActivity(), this.communities);
        listView.setAdapter(topicAdapter);
        //topicAdapter.notifyDataSetChanged();



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent=new Intent(getActivity(), CommunityActivity.class);

                String noMember,noPost="100",commId,name;

                CommunitiesWidgetChildVM childVM = topicAdapter.getItem(position);

                commId=childVM.getId().toString();
                noMember=childVM.getMm().toString();
                name=childVM.getDn();

                intent.putExtra("id",commId);
                intent.putExtra("noMember",noMember);
                intent.putExtra("noPost",noPost);
                intent.putExtra("commName",name);

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
