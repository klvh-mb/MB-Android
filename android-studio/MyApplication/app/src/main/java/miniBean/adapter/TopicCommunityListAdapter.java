package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.util.CommunityUtil;
import miniBean.viewmodel.CommunitiesWidgetChildVM;

public class TopicCommunityListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private CommunityUtil communityUtil;
    private List<CommunitiesWidgetChildVM> communities;

    public TopicCommunityListAdapter(Activity activity, List<CommunitiesWidgetChildVM> communities) {
        this.activity = activity;
        this.communities = communities;
        this.communityUtil = new CommunityUtil(activity, communities);
    }

    @Override
    public int getCount() {
        if (communities == null)
            return 0;
        return communities.size();
    }

    @Override
    public CommunitiesWidgetChildVM getItem(int location) {
        if (communities == null || location > communities.size()-1)
            return null;
        return communities.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.topic_community_list_item, null);

        ImageView commImage = (ImageView) convertView.findViewById(R.id.commImage);
        TextView commName = (TextView) convertView.findViewById(R.id.commName);
        TextView noMember = (TextView) convertView.findViewById(R.id.noMember);
        TextView noPost = (TextView) convertView.findViewById(R.id.noPost);
        ImageView joinButton = (ImageView) convertView.findViewById(R.id.joinButton);

        communityUtil.setCommunityLayout(position, convertView, commImage, commName, noMember, noPost, joinButton);

        return convertView;
    }
}
