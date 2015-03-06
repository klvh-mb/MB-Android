package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.NotificationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestListAdapter extends BaseAdapter {
    public List<NotificationVM> requestItems;
    public SharedPreferences session;
    TextView username, message, date;
    ImageView userPhoto;
    Button acceptButton, ignoreButton;
    private Activity activity;
    ProgressBar spinner;
    private LayoutInflater inflater;


    public RequestListAdapter(Activity activity, List<NotificationVM> requestItems) {
        this.activity = activity;
        this.requestItems = requestItems;
    }

    @Override
    public int getCount() {
        return requestItems.size();
    }

    @Override
    public NotificationVM getItem(int location) {
        return requestItems.get(location);
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
            convertView = inflater.inflate(R.layout.request_item, null);


        final NotificationVM item = requestItems.get(position);

        message = (TextView) convertView.findViewById(R.id.requestText);
        userPhoto = (ImageView) convertView.findViewById(R.id.userImage);
        acceptButton = (Button) convertView.findViewById(R.id.acceptButton);
        spinner = (ProgressBar) convertView.findViewById(R.id.imageLoader);
        ignoreButton = (Button) convertView.findViewById(R.id.ignoreButton);
        session = this.activity.getSharedPreferences("prefs", 0);

        if (item.getTp().equals("COMM_JOIN_APPROVED") || item.getTp().equals("FRD_ACCEPTED")) {
            acceptButton.setVisibility(View.INVISIBLE);
            ignoreButton.setVisibility(View.INVISIBLE);
        } else {
            acceptButton.setVisibility(View.VISIBLE);
            ignoreButton.setVisibility(View.VISIBLE);
        }
        AppController.mImageLoader.displayImage(activity.getResources().getString(R.string.base_url) + item.getUrl().getPhoto(), userPhoto);
        message.setText(item.getMsg());


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (item.getTp()) {
                    case "COMM_JOIN_REQUEST":
                        acceptCommJoinRequest(item.getUrl().getActor(), item.getUrl().getTarget(), item.getNid(), v);
                        break;

                    case "COMM_INVITE_REQUEST":
                        acceptCommInviteRequest(item.getUrl().getActor(), item.getUrl().getTarget(), item.getNid(), v);
                        break;

                    case "FRD_REQUEST":
                        acceptFriendRequest(item.getUrl().getActor(), item.getNid(), v);
                        break;
                }

            }
        });

        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ignoreIt(item);
            }

        });


        return convertView;
    }

    public void acceptCommJoinRequest(Long member_id, Long group_id, Long notif_id, final View v) {
        AppController.api.acceptCommJoinRequest(member_id, group_id, notif_id, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response user, retrofit.client.Response response) {
                ((Button) v.findViewById(R.id.acceptButton)).setVisibility(View.INVISIBLE);
                ((Button) v.findViewById(R.id.ignoreButton)).setVisibility(View.INVISIBLE);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });

    }

    public void acceptFriendRequest(Long friend_id, Long notif_id, final View v) {
        AppController.api.acceptFriendRequest(friend_id, notif_id, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response user, retrofit.client.Response response) {
                ((Button) v.findViewById(R.id.acceptButton)).setVisibility(View.INVISIBLE);
                ((Button) v.findViewById(R.id.ignoreButton)).setVisibility(View.INVISIBLE);

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void acceptCommInviteRequest(Long member_id, Long group_id, Long notif_id, final View v) {
        AppController.api.acceptCommInviteRequest(member_id, group_id, notif_id, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response user, retrofit.client.Response response) {
                ((Button) v.findViewById(R.id.acceptButton)).setVisibility(View.INVISIBLE);
                ((Button) v.findViewById(R.id.ignoreButton)).setVisibility(View.INVISIBLE);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });

    }

    public void ignoreIt(final NotificationVM item) {
        AppController.api.ignoreIt(item.getNid(), session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response user, retrofit.client.Response response) {
                requestItems.remove(item);
                notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });

    }

}