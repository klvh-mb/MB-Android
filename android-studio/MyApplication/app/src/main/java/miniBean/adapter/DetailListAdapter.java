package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
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
import miniBean.viewmodel.Comment;

public class DetailListAdapter extends BaseAdapter {
    TextView ownerName, commentText, commentTime, commentLocation, postTime;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Comment> communityItems;

    public DetailListAdapter(Activity activity, List<Comment> communityItems) {
        this.activity = activity;
        this.communityItems = communityItems;
    }

    @Override
    public int getCount() {
        return communityItems.size();
    }

    @Override
    public Comment getItem(int location) {
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
            convertView = inflater.inflate(R.layout.detail_item, null);

        ownerName = (TextView) convertView.findViewById(R.id.postedBy);
        postTime = (TextView) convertView.findViewById(R.id.postedOn);
        commentText = (TextView) convertView.findViewById(R.id.commentText);
        commentTime = (TextView) convertView.findViewById(R.id.timeText);
        commentLocation = (TextView) convertView.findViewById(R.id.locationText);
        ImageView userPic = (ImageView) convertView.findViewById(R.id.questionnare_img);

        final Comment item = communityItems.get(position);


        commentText.setText(item.getD());

        ownerName.setText(item.getOn());

        Date date = new Date(item.getCd());
        String DATE_FORMAT_NOW = "dd-MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String stringDate = sdf.format(date);
        commentTime.setText(stringDate);
        postTime.setText(stringDate);

        AppController.mImageLoader.displayImage(activity.getResources().getString(R.string.base_url) + "/image/get-mini-image-by-id/" + item.getOid(), userPic);
        return convertView;
    }
}