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
import miniBean.util.CommunityIconUtil;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.CommunitiesWidgetChildVM;

public class PopupMyCommunityListAdapter extends BaseAdapter {
    private ImageView communityIcon;
    private TextView communityName;
    private Activity activity;
    private List<CommunitiesWidgetChildVM> communities;

    private LayoutInflater inflater;

    public PopupMyCommunityListAdapter(Activity activity, List<CommunitiesWidgetChildVM> communities) {
        this.activity = activity;
        this.communities = communities;
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
            convertView = inflater.inflate(R.layout.my_community_popup_item, null);

        communityIcon = (ImageView) convertView.findViewById(R.id.commIcon);
        communityName = (TextView) convertView.findViewById(R.id.communityName);

        CommunitiesWidgetChildVM item = communities.get(position);

        communityName.setText(item.getDn());
        int iconMapped = CommunityIconUtil.map(item.getGi());
        if (iconMapped != -1) {
            //Log.d(this.getClass().getSimpleName(), "getView: replace source with local comm icon - " + commIcon);
            communityIcon.setImageDrawable(activity.getResources().getDrawable(iconMapped));
        } else {
            Log.d(this.getClass().getSimpleName(), "getView: load comm icon from background - " + item.getGi());
            ImageUtil.displayRoundedCornersImage(item.getGi(), communityIcon);
        }

        return convertView;
    }
}