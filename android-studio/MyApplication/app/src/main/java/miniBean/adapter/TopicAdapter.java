package miniBean.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.LocalCache;
import miniBean.util.CommunityIconUtil;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopicAdapter extends BaseAdapter {
    ImageView imageAction;
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommunitiesWidgetChildVM> communities;

    public TopicAdapter(Activity activity, List<CommunitiesWidgetChildVM> communities) {
        this.activity = activity;
        this.communities = communities;
    }

    @Override
    public int getCount() {
        if (communities == null)
            return 0;
        return communities.size();
    }

    @Override
    public CommunitiesWidgetChildVM getItem(int location) {
        return communities.get(location);
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
            convertView = inflater.inflate(R.layout.topic_item, null);

        TextView commName = (TextView) convertView.findViewById(R.id.commName);
        TextView noMembers = (TextView) convertView.findViewById(R.id.noMember);
        imageAction = (ImageView) convertView.findViewById(R.id.mem_join);

        ImageView communityPic = (ImageView) convertView.findViewById(R.id.communityImg);

        final CommunitiesWidgetChildVM item = communities.get(position);
        commName.setText(item.getDn());
        noMembers.setText(item.getMm().toString());

        int iconMapped = CommunityIconUtil.map(item.gi);
        if (iconMapped != -1) {
            //Log.d(this.getClass().getSimpleName(), "getView: replace source with local comm icon - " + item.gi);
            communityPic.setImageDrawable(activity.getResources().getDrawable(iconMapped));
        } else {
            Log.d(this.getClass().getSimpleName(), "getView: load comm icon from background - " + item.gi);
            ImageLoader.getInstance().displayImage(
                    activity.getResources().getString(R.string.base_url) + item.gi,
                    communityPic,
                    AppController.ROUNDED_CORNERS_IMAGE_OPTIONS);
        }

        if (item.getIsM()) {
            imageAction.setImageResource(R.drawable.add);
        } else {
            imageAction.setImageResource(R.drawable.check);
        }

        imageAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!item.getIsM()) {
                    ImageView joinImageView = (ImageView) v.findViewById(R.id.mem_join);
                    joinCommunity(item, joinImageView);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                    alertDialogBuilder.setMessage(TopicAdapter.this.activity.getString(R.string.community_leave_confirm));
                    alertDialogBuilder.setPositiveButton(TopicAdapter.this.activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ImageView leaveImageView = (ImageView) v.findViewById(R.id.mem_join);
                            leaveCommunity(item, leaveImageView);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(TopicAdapter.this.activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        return convertView;
    }

    public void joinCommunity(final CommunitiesWidgetChildVM communityVM, final ImageView joinImageView) {
        AppController.api.sendJoinRequest(communityVM.id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), TopicAdapter.this.activity.getString(R.string.community_join_success), Toast.LENGTH_SHORT).show();
                communityVM.setIsM(true);
                joinImageView.setImageResource(R.drawable.add);

                LocalCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(inflater.getContext(), TopicAdapter.this.activity.getString(R.string.community_join_failed), Toast.LENGTH_SHORT).show();
                retrofitError.printStackTrace();
            }
        });
    }

    public void leaveCommunity(final CommunitiesWidgetChildVM communityVM, final ImageView joinImageView) {
        AppController.api.sendLeaveRequest(communityVM.id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), TopicAdapter.this.activity.getString(R.string.community_leave_success), Toast.LENGTH_SHORT).show();
                communityVM.setIsM(false);
                joinImageView.setImageResource(R.drawable.check);

                LocalCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(inflater.getContext(), TopicAdapter.this.activity.getString(R.string.community_leave_failed), Toast.LENGTH_SHORT).show();
                retrofitError.printStackTrace();
            }
        });
    }
}
