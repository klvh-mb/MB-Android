package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import miniBean.R;
import miniBean.util.DateTimeUtil;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.NotificationVM;

public class NotificationListAdapter extends BaseAdapter {
    private ImageView userPhoto;
    private TextView message, timeText;
    private Activity activity;
    private LayoutInflater inflater;
    private List<NotificationVM> notificationItems;

    public NotificationListAdapter(Activity activity, List<NotificationVM> notificationItems) {
        this.activity = activity;
        this.notificationItems = notificationItems;
    }

    @Override
    public int getCount() {
        if (notificationItems == null)
            return 0;
        return notificationItems.size();
    }

    @Override
    public NotificationVM getItem(int location) {
        if (notificationItems == null || location > notificationItems.size()-1)
            return null;
        return notificationItems.get(location);
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
            convertView = inflater.inflate(R.layout.notification_list_item, null);

        final NotificationVM item = notificationItems.get(position);

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.mainLayout);
        if (item.getSta() == 0)
            layout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.drawable.rect_border_notification_new));

        message = (TextView) convertView.findViewById(R.id.notificationMessage);
        timeText = (TextView) convertView.findViewById(R.id.notificationTime);
        userPhoto = (ImageView) convertView.findViewById(R.id.userImage);

        message.setText(item.getMsg());
        timeText.setText(DateTimeUtil.getTimeAgo(new DateTime(Long.parseLong(item.getUpd())).getMillis()));

        ImageUtil.displayRoundImage(item.getUrl().getPhoto(), userPhoto);

        return convertView;
    }
}