package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import miniBean.R;
import miniBean.viewmodel.CommunityPostVM;

public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommunityPostVM> feedItems;

    public FeedListAdapter(Activity activity, List<CommunityPostVM> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
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
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);


        TextView name = (TextView) convertView.findViewById(R.id.commName);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView timeText = (TextView) convertView.findViewById(R.id.timeText);
        TextView noComment = (TextView) convertView
                .findViewById(R.id.commentnoTEXT);

        final CommunityPostVM item = feedItems.get(position);


        name.setText(item.getPtl());
        username.setText(item.getP());
        System.out.println("nocomment:::::" + item.getN_c());
        noComment.setText(item.getN_c() + "");

        Date date = new Date(item.getT());
        String DATE_FORMAT_NOW = "dd-MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String stringDate = sdf.format(date);
        timeText.setText(stringDate);
        return convertView;
    }
}