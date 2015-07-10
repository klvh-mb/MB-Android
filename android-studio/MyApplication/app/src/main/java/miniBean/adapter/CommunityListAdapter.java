package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.util.ImageMapping;
import miniBean.util.ImageUtil;
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
        if (communityItems == null)
            return 0;
        return communityItems.size();
    }

    @Override
    public CommunitiesWidgetChildVM getItem(int location) {
        if (communityItems == null || location > communityItems.size()-1)
            return null;
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
            convertView = inflater.inflate(R.layout.community_list_item, null);

        TextView commName = (TextView) convertView.findViewById(R.id.commName);
        TextView noMembers = (TextView) convertView.findViewById(R.id.noMember);
        TextView numTopicsToday = (TextView) convertView.findViewById(R.id.numTopicsToday);

        ImageView communityPic = (ImageView) convertView.findViewById(R.id.communityPic);

        final CommunitiesWidgetChildVM item = communityItems.get(position);

        commName.setText(item.getDn());
        noMembers.setText(item.getMm().toString());
        numTopicsToday.setText("-");

        int iconMapped = ImageMapping.map(item.gi);
        if (iconMapped != -1) {
            //Log.d(this.getClass().getSimpleName(), "getView: replace source with local comm icon - " + item.gi);
            communityPic.setImageDrawable(activity.getResources().getDrawable(iconMapped));
        } else {
            Log.d(this.getClass().getSimpleName(), "getView: load comm icon from background - " + item.gi);
            ImageUtil.displayRoundedCornersImage(item.gi, communityPic);
        }

        return convertView;
    }
}