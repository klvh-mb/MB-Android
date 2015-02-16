package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.viewmodel.Post;

public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Post> feedItems;

    public FeedListAdapter(Activity activity, List<Post> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Post getItem(int location) {
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
            convertView = inflater.inflate(R.layout.feed_item, null);


        TextView name = (TextView) convertView.findViewById(R.id.commName);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView timeText = (TextView) convertView.findViewById(R.id.timeText);
        TextView noComment = (TextView) convertView
                .findViewById(R.id.commentnoTEXT);

        final Post item = feedItems.get(position);


        name.setText(item.getCn());
        username.setText(item.getP());
        noComment.setText("20");

        CharSequence time = DateUtils.getRelativeTimeSpanString(item.getT(),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timeText.setText(time);
        return convertView;
    }
}