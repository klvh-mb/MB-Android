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

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.LocalCommunityTabCache;
import miniBean.util.CommunityIconUtil;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopicCommunityListAdapter extends BaseAdapter {
    private ImageView joinButton;
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommunitiesWidgetChildVM> communities;

    public TopicCommunityListAdapter(Activity activity, List<CommunitiesWidgetChildVM> communities) {
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
        if (communities == null || location > communities.size()-1)
            return null;
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
            convertView = inflater.inflate(R.layout.topic_community_list_item, null);

        TextView commName = (TextView) convertView.findViewById(R.id.commName);
        TextView noMembers = (TextView) convertView.findViewById(R.id.noMember);
        joinButton = (ImageView) convertView.findViewById(R.id.joinButton);

        ImageView commImage = (ImageView) convertView.findViewById(R.id.commImage);

        final CommunitiesWidgetChildVM item = communities.get(position);
        commName.setText(item.getDn());
        noMembers.setText(item.getMm().toString());

        int iconMapped = CommunityIconUtil.map(item.gi);
        if (iconMapped != -1) {
            //Log.d(this.getClass().getSimpleName(), "getView: replace source with local comm icon - " + item.gi);
            commImage.setImageDrawable(activity.getResources().getDrawable(iconMapped));
        } else {
            Log.d(this.getClass().getSimpleName(), "getView: load comm icon from background - " + item.gi);
            ImageUtil.displayRoundedCornersImage(item.gi, commImage);
        }

        if (item.getIsM()) {
            joinButton.setImageResource(R.drawable.ic_check);
        } else {
            joinButton.setImageResource(R.drawable.ic_add);
        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!item.getIsM()) {
                    joinCommunity(item, joinButton);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                    alertDialogBuilder.setMessage(TopicCommunityListAdapter.this.activity.getString(R.string.community_leave_confirm));
                    alertDialogBuilder.setPositiveButton(TopicCommunityListAdapter.this.activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            leaveCommunity(item, joinButton);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(TopicCommunityListAdapter.this.activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

    private void joinCommunity(final CommunitiesWidgetChildVM communityVM, final ImageView joinButton) {
        AppController.getApi().sendJoinRequest(communityVM.id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), TopicCommunityListAdapter.this.activity.getString(R.string.community_join_success), Toast.LENGTH_SHORT).show();
                communityVM.setIsM(true);
                joinButton.setImageResource(R.drawable.ic_check);

                LocalCommunityTabCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(inflater.getContext(), TopicCommunityListAdapter.this.activity.getString(R.string.community_join_failed), Toast.LENGTH_SHORT).show();
                retrofitError.printStackTrace();
            }
        });
    }

    private void leaveCommunity(final CommunitiesWidgetChildVM communityVM, final ImageView joinImageView) {
        AppController.getApi().sendLeaveRequest(communityVM.id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), TopicCommunityListAdapter.this.activity.getString(R.string.community_leave_success), Toast.LENGTH_SHORT).show();
                communityVM.setIsM(false);
                joinImageView.setImageResource(R.drawable.ic_add);

                LocalCommunityTabCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(inflater.getContext(), TopicCommunityListAdapter.this.activity.getString(R.string.community_leave_failed), Toast.LENGTH_SHORT).show();
                retrofitError.printStackTrace();
            }
        });
    }
}
