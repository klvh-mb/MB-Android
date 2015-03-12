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
import miniBean.app.AppController;
import miniBean.viewmodel.CommunitiesWidgetChildVM;

public class CommunityListAdapter extends BaseAdapter {
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
    public CommunitiesWidgetChildVM getItem(int location) {
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
        TextView numTopicsToday = (TextView) convertView.findViewById(R.id.numTopicsToday);

        ImageView communityPic = (ImageView) convertView
                .findViewById(R.id.communityPic);

        final CommunitiesWidgetChildVM item = communityItems.get(position);

        commName.setText(item.getDn());
        noMembers.setText(item.getMm().toString());
        numTopicsToday.setText("-");

        AppController.mImageLoader.displayImage(activity.getResources().getString(R.string.base_url) + item.getGi(), communityPic);
        return convertView;
    }
}