package info.androidhive.listviewfeed.adapter;

import info.androidhive.listviewfeed.FeedImageView;
import info.androidhive.listviewfeed.R;
import info.androidhive.listviewfeed.app.AppController;
import info.androidhive.listviewfeed.data.CommentItem;
import info.androidhive.listviewfeed.data.FeedItem;
 
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
 
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
 
public class CommentListAdapter extends BaseAdapter {  
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommentItem> feedItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
 
    public CommentListAdapter(Activity activity, List<CommentItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }
 
    @Override
    public int getCount() {
        return feedItems.size();
    }
 
    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
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
            convertView = inflater.inflate(R.layout.comment_item, null);
 
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
 
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        //TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        /*FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);*/
 
        CommentItem item = feedItems.get(position);
        statusMsg.setText(item.getStatus());
 
        name.setText(item.getName());
        
        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);
 
        // user profile pic
        profilePic.setImageUrl(item.getProfilePic(), imageLoader);
        
        return convertView;
    }
 
}