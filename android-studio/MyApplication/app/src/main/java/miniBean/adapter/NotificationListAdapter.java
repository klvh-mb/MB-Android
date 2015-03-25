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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.joda.time.DateTime;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.ActivityUtil;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.NotificationVM;

public class NotificationListAdapter extends BaseAdapter {
    ProgressBar spinner;
    ImageView userPhoto;
    TextView username, message, timeText;
    private Activity activity;
    private LayoutInflater inflater;
    private List<NotificationVM> notificationItems;

    private ActivityUtil activityUtil;

    public NotificationListAdapter(Activity activity, List<NotificationVM> notificationItems) {
        this.activity = activity;
        this.notificationItems = notificationItems;
        this.activityUtil = new ActivityUtil(activity);
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
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.notification_item, null);

        final NotificationVM item = notificationItems.get(position);

        username = (TextView) convertView.findViewById(R.id.postedBy);
        message = (TextView) convertView.findViewById(R.id.notificationMessage);
        timeText = (TextView) convertView.findViewById(R.id.notificationTime);
        spinner = (ProgressBar) convertView.findViewById(R.id.imageLoader);
        userPhoto = (ImageView) convertView.findViewById(R.id.userImage);

        ImageLoader.getInstance().displayImage(
                activity.getResources().getString(R.string.base_url) + item.getUrl().getPhoto(),
                userPhoto,
                AppController.ROUNDED_CORNERS_IMAGE_OPTIONS);

        message.setText(item.getMsg());

        timeText.setText(activityUtil.getTimeAgo(new DateTime(Long.parseLong(item.getUpd())).getMillis()));

        return convertView;
    }
}