package miniBean.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import miniBean.app.LocalCache;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopicAdapter extends BaseAdapter {
    public SharedPreferences session = null;
    Long id;
    int statusCode = 0;
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
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.topic_item, null);

        TextView commName = (TextView) convertView.findViewById(R.id.commName);
        TextView noMembers = (TextView) convertView.findViewById(R.id.noMember);
        imageAction = (ImageView) convertView.findViewById(R.id.mem_join);


        ImageView communityPic = (ImageView) convertView
                .findViewById(R.id.communityImg);


        final CommunitiesWidgetChildVM item = communities.get(position);

        commName.setText(item.getDn());
        noMembers.setText(item.getMm().toString());

        session = activity.getSharedPreferences("prefs", 0);

        AppController.mImageLoader.displayImage(activity.getResources().getString(R.string.base_url) + item.gi, communityPic);

        if (item.getIsM()) {
            imageAction.setImageResource(R.drawable.add);
        } else {
            imageAction.setImageResource(R.drawable.check);
        }


        imageAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (!item.getIsM()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                    alertDialogBuilder.setMessage("Do You Want Join This Community?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendJoinRequest(item.getId());

                            Toast.makeText(inflater.getContext(), "Community Joined", Toast.LENGTH_LONG).show();
                            item.setIsM(true);
                            ImageView image = (ImageView) v.findViewById(R.id.mem_join);
                            image.setImageResource(R.drawable.add);
                        }

                    });

                    alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(inflater.getContext(), "CANCEL", Toast.LENGTH_LONG).show();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                    alertDialogBuilder.setMessage("Are You Want To Leave This Community?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            leaveCommunity(item.getId());
                            Toast.makeText(inflater.getContext(), "Community Left", Toast.LENGTH_LONG).show();

                            item.setIsM(false);
                            ImageView image = (ImageView) v.findViewById(R.id.mem_join);
                            image.setImageResource(R.drawable.check);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(inflater.getContext(), "CANCEL", Toast.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        return convertView;
    }


    public void sendJoinRequest(Long id) {
        AppController.api.sendJoinRequest(id, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                LocalCache.setDirty(true);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void leaveCommunity(Long id) {
        AppController.api.sendLeaveRequest(id, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                LocalCache.setDirty(true);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }
}
