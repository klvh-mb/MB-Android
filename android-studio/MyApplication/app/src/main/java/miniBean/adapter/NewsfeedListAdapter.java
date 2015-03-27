package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.util.CommunityIconUtil;
import miniBean.util.DateTimeUtil;
import miniBean.util.DefaultValues;
import miniBean.util.ImageUtil;
import miniBean.util.PostUtil;
import miniBean.viewmodel.CommunityPostVM;

public class NewsfeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private LinearLayout iconsLayout;
    private List<CommunityPostVM> feedItems;
    private boolean isNewsfeed = true;
    private int lastPosition = -1;

    public NewsfeedListAdapter(Activity activity, List<CommunityPostVM> feedItems) {
        this(activity, feedItems, true);
    }

    public NewsfeedListAdapter(Activity activity, List<CommunityPostVM> feedItems, boolean isNewsfeed) {
        this.activity = activity;
        this.feedItems = feedItems;
        this.isNewsfeed = isNewsfeed;
    }

    @Override
    public int getCount() {
        if (feedItems == null)
            return 0;
        return feedItems.size();
    }

    @Override
    public CommunityPostVM getItem(int location) {
        if (feedItems == null || location > feedItems.size()-1)
            return null;
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
            convertView = inflater.inflate(R.layout.newsfeed_list_item, null);

        TextView name = (TextView) convertView.findViewById(R.id.postTitle);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView timeText = (TextView) convertView.findViewById(R.id.time);
        TextView numComment = (TextView) convertView.findViewById(R.id.numComment);
        ImageView communityIcon = (ImageView) convertView.findViewById(R.id.commIcon);
        TextView commName = (TextView) convertView.findViewById(R.id.commName);

        final CommunityPostVM item = feedItems.get(position);

        //Log.d(this.getClass().getSimpleName(), "getView: Post - " + item.getPtl() + "|#comment: " + item.getN_c());

        name.setText(item.getPtl());
        username.setText(item.getP());
        numComment.setText(item.getN_c() + "");

        timeText.setText(DateTimeUtil.getTimeAgo(item.getUt()));

        if (isNewsfeed) {
            commName.setText(item.getCn());
            int iconMapped = CommunityIconUtil.map(item.getCi());
            if (iconMapped != -1) {
                //Log.d(this.getClass().getSimpleName(), "getView: replace source with local comm icon - " + commIcon);
                communityIcon.setImageDrawable(convertView.getResources().getDrawable(iconMapped));
            } else {
                Log.d(this.getClass().getSimpleName(), "getView: load comm icon from background - " + item.getCi());
                ImageUtil.displayRoundedCornersImage(item.getCi(), communityIcon);
            }
        } else {
            commName.setVisibility(View.GONE);
            communityIcon.setVisibility(View.GONE);
        }

        // icons
        LinearLayout iconsLayout = (LinearLayout) convertView.findViewById(R.id.iconsLayout);
        ImageView iconImage = (ImageView) convertView.findViewById(R.id.iconImage);
        ImageView iconNew = (ImageView) convertView.findViewById(R.id.iconNew);
        ImageView iconHot = (ImageView) convertView.findViewById(R.id.iconHot);
        iconsLayout.setVisibility(View.GONE);
        iconImage.setVisibility(View.GONE);
        iconNew.setVisibility(View.GONE);
        iconHot.setVisibility(View.GONE);

        // New and Hot icons are mutually exclusive
        if (PostUtil.isNewPost(item)) {
            iconsLayout.setVisibility(View.VISIBLE);
            iconNew.setVisibility(View.VISIBLE);
        } else if (PostUtil.isHotPost(item)) {
            iconsLayout.setVisibility(View.VISIBLE);
            iconHot.setVisibility(View.VISIBLE);
        }
        if (PostUtil.isImagePost(item)) {
            iconsLayout.setVisibility(View.VISIBLE);
            iconImage.setVisibility(View.VISIBLE);
        }

        //Animation animation = AnimationUtils.loadAnimation(activity.getBaseContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        // Slide in from bottom
        if (position > lastPosition) {
            if (position > DefaultValues.LISTVIEW_SLIDE_IN_ANIM_START && lastPosition != -1) {
                //Log.d(this.getClass().getSimpleName(), "getView: animate up_from_bottom - " + position+"");
                Animation animation = AnimationUtils.loadAnimation(activity.getBaseContext(), R.anim.up_from_bottom);
                convertView.startAnimation(animation);
            }
            lastPosition = position;
        }

        return convertView;
    }
}