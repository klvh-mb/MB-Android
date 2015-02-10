package miniBean.fragement;

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
    public TopicAdapter topicFragment;
    public List<CommunitiesWidgetChildVM> communities;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.topic_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);
        topicFragment = new TopicAdapter(getActivity(), this.communities);
        return rootView;
    }

    public List<CommunitiesWidgetChildVM> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunitiesWidgetChildVM> communities) {
        this.communities = communities;
    }


}
