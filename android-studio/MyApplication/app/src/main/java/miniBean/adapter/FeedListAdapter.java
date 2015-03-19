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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.CommunityIconUtil;
import miniBean.viewmodel.CommunityPostVM;

public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommunityPostVM> feedItems;
    private boolean isNewsfeed = true;

    public FeedListAdapter(Activity activity, List<CommunityPostVM> feedItems) {
        this(activity, feedItems, true);
    }

    public FeedListAdapter(Activity activity, List<CommunityPostVM> feedItems, boolean isNewsfeed) {
        this.activity = activity;
        this.feedItems = feedItems;
        this.isNewsfeed = isNewsfeed;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public CommunityPostVM getItem(int location) {
        return feedItems.get(location);
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
            convertView = inflater.inflate(R.layout.newsfeed_item, null);

        TextView name = (TextView) convertView.findViewById(R.id.postTitle);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView timeText = (TextView) convertView.findViewById(R.id.time);
        TextView numComment = (TextView) convertView.findViewById(R.id.numComment);
        ImageView communityIcon = (ImageView) convertView.findViewById(R.id.communityIcon);
        TextView commName = (TextView) convertView.findViewById(R.id.commName);

        final CommunityPostVM item = feedItems.get(position);

        name.setText(item.getPtl());
        username.setText(item.getP());
        Log.d("getView", item.getPtl() + "   #comment: " + item.getN_c());
        numComment.setText(item.getN_c() + "");

        Date date = new Date(item.getT());
        String DATE_FORMAT_NOW = "dd-MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String stringDate = sdf.format(date);
        timeText.setText(stringDate);

        if (isNewsfeed) {
            commName.setText(item.getCn());
            int iconMapped = CommunityIconUtil.map(item.getCi());
            if (iconMapped != -1) {
                //Log.d("getView", "replace source with local comm icon - " + commIcon);
                communityIcon.setImageDrawable(convertView.getResources().getDrawable(iconMapped));
            } else {
                Log.d("getView", "load comm icon from background - " + item.getCi());
                AppController.mImageLoader.displayImage(convertView.getResources().getString(R.string.base_url) + item.getCi(), communityIcon);
            }
        } else {
            commName.setVisibility(View.GONE);
            communityIcon.setVisibility(View.GONE);
        }

        return convertView;
    }
}