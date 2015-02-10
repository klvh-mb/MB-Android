package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.CommunitiesWidgetChildVM;

public class CommunityListAdapter extends BaseAdapter {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommunitiesWidgetChildVM> communityItems;

    public CommunityListAdapter(Activity activity, List<CommunitiesWidgetChildVM> communityItems) {
        this.activity = activity;
        this.communityItems = communityItems;
    }

    @Override
    public int getCount() {
        return communityItems.size();
    }

    @Override
    public Object getItem(int location) {
        return communityItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.community_item, null);

        TextView commName = (TextView) convertView.findViewById(R.id.commName);
        TextView noMembers = (TextView) convertView.findViewById(R.id.noMember);
        TextView noTopics = (TextView) convertView.findViewById(R.id.noTopics);
        NetworkImageView communityPic = (NetworkImageView) convertView
                .findViewById(R.id.communityPic);


        final CommunitiesWidgetChildVM item = communityItems.get(position);

        commName.setText(item.getDn());
        noMembers.setText(item.getMm().toString());
        noTopics.setText("11");
        communityPic.setImageUrl(this.activity.getResources().getString(R.string.base_url) + "/image/get-mini-image-by-id/" + item.getId(), imageLoader);

        return convertView;
    }
}