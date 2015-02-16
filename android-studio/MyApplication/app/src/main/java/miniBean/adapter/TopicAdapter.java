package miniBean.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;
import miniBean.MyApi;
import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class TopicAdapter extends BaseAdapter {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Activity activity;
    public SharedPreferences session = null;
    private LayoutInflater inflater;
    private List<CommunitiesWidgetChildVM> communities;
 
    private boolean booleanFlag = true;
    public MyApi api;
    Long id;
    int statusCode = 0;

    ImageView imageAction;

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
        imageAction = (ImageView)convertView.findViewById(R.id.mem_join);


    //    imageAction.setImageResource(R.drawable.request_send);
     //   imageAction.setOnClickListener(new View.OnClickListener() {
    /*        int button01pos = 0;
            public void onClick(View v) {
                ImageView imageView = (ImageView)v.findViewById(R.id.mem_join);
                if (button01pos == 0) {
                    imageView.setImageResource(R.drawable.check_community);
                    button01pos = 1;
                } else if (button01pos == 1) {
                    imageView.setImageResource(R.drawable.request_send);
                    imageView.setImageResource(R.drawable.request_send);
                    button01pos = 0;
                }
            }
        });*/
       // TextView noTopics = (TextView) convertView.findViewById(R.id.noTopics);
        NetworkImageView communityPic = (NetworkImageView) convertView
                .findViewById(R.id.communityImg);


        final CommunitiesWidgetChildVM item = communities.get(position);
        booleanFlag = item.getIsM();

        commName.setText(item.getDn());
        noMembers.setText(item.getMm().toString());

        id = item.getId();
        session = activity.getSharedPreferences("prefs", 0);
        AppController.api.sendJoinRequest(Long.valueOf(id),session.getString("sessionID",null),new Callback<Response>(){
            @Override
            public void success(Response response, Response response2) {

                   int statusCode = response.getStatus();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors


            }
        });


        //noTopics.setText("11");

        communityPic.setImageUrl(this.activity.getResources().getString(R.string.base_url) + item.getGi(), imageLoader);





           imageAction.setOnClickListener(new View.OnClickListener() {
              @Override
                public void onClick(final View v) {

                     if(booleanFlag == true)
                     {
                           AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                           alertDialogBuilder.setMessage("Are You Want Remove This Member?");
                           alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                           @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                    if(statusCode == 200)
                                    {
                                        Toast.makeText(inflater.getContext(), "OK", Toast.LENGTH_LONG).show();

                                    }
                            }
                    });

                            alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                    Toast.makeText(inflater.getContext(), "CANCEL", Toast.LENGTH_LONG).show();
                            }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
               else
               {
                  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                  alertDialogBuilder.setMessage("Are You Want To Join This Member?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                      @Override
                        public void onClick(DialogInterface dialog, int which) {


                            if(statusCode == 0) {

                                ImageView changeImage = (ImageView)v.findViewById(R.id.mem_join);
                                changeImage.setImageResource(R.drawable.request_send);



                          }
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
}