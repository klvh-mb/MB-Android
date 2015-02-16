package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.CommunityPostCommentVM;

public class DetailListAdapter extends BaseAdapter {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommunityPostCommentVM> communityItems;

    public DetailListAdapter(Activity activity, List<CommunityPostCommentVM> communityItems) {
        this.activity = activity;
        this.communityItems = communityItems;
    }

    @Override
    public int getCount() {
        return communityItems.size();
    }

    @Override
    public CommunityPostCommentVM getItem(int location) {
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

        TextView commName = (TextView) convertView.findViewById(R.id.Text1);
        TextView noMembers = (TextView) convertView.findViewById(R.id.Text2);
        TextView noTopics = (TextView) convertView.findViewById(R.id.Text3);
        TextView noTopic = (TextView) convertView.findViewById(R.id.Text4);

        NetworkImageView userPic = (NetworkImageView)convertView.findViewById(R.id.questionnare_img);

        final CommunityPostCommentVM item = communityItems.get(position);


        userPic.setImageUrl(this.activity.getResources().getString(R.string.base_url) + "/image/get-mini-image-by-id/" + item.getId(), imageLoader);

        return convertView;
    }
}