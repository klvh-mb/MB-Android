package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.NotificationVM;

public class NotificationListAdapter extends BaseAdapter {
    ProgressBar spinner;
    ImageView userPhoto;
    TextView username, message, date;
    private Activity activity;
    private LayoutInflater inflater;
    private List<NotificationVM> notificationItems;

    public NotificationListAdapter(Activity activity, List<NotificationVM> notificationItems) {
        this.activity = activity;
        this.notificationItems = notificationItems;
    }

    @Override
    public int getCount() {
        return notificationItems.size();
    }

    @Override
    public NotificationVM getItem(int location) {
        return notificationItems.get(location);
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
            convertView = inflater.inflate(R.layout.notification_item, null);


        final NotificationVM item = notificationItems.get(position);

        username = (TextView) convertView.findViewById(R.id.postedBy);
        message = (TextView) convertView.findViewById(R.id.notificationMessage);
        date = (TextView) convertView.findViewById(R.id.notificationTime);
        spinner = (ProgressBar) convertView.findViewById(R.id.imageLoader);

        userPhoto = (ImageView) convertView.findViewById(R.id.userImage);
        AppController.mImageLoader.displayImage(activity.getResources().getString(R.string.base_url) + item.getUrl().getPhoto(), userPhoto);
        message.setText(item.getMsg());

        String DATE_FORMAT_NOW = "dd-MMM";
        long val = 1346524199000l;
        Date date1 = new Date(Long.parseLong(item.getUpd()));
        SimpleDateFormat df2 = new SimpleDateFormat(DATE_FORMAT_NOW);
        String dateText = df2.format(date1);
        date.setText(dateText);

        return convertView;
    }
}