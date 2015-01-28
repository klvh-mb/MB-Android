package info.androidhive.listviewfeed.adapter;

import info.androidhive.listviewfeed.FeedImageView;
import info.androidhive.listviewfeed.R;
import info.androidhive.listviewfeed.app.AppController;
import info.androidhive.listviewfeed.data.CommentItem;
import info.androidhive.listviewfeed.data.FeedItem;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
 
public class FeedListAdapter extends BaseAdapter {  
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    private List<CommentItem> commentItems;
    private CommentListAdapter commentListAdapter;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
 
    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }
 
    @Override
    public int getCount() {
        return feedItems.size();
    }
 
    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
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
            convertView = inflater.inflate(R.layout.feed_item, null);
 
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
 
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);
 
        final FeedItem item = feedItems.get(position);
 
        name.setText(item.getName());
        
        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);
 
        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }
 
        // Checking for null feed url
        if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));
 
            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }
 
        // user profile pic
        profilePic.setImageUrl(item.getProfilePic(), imageLoader);
 
        // Feed image
        if (item.getImge() != null) {
            feedImageView.setImageUrl(item.getImge(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }
 
                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }
        
        final Button likeButton = (Button) convertView.findViewById(R.id.like);
        likeButton.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
	                     "http://localhost:9000/like-post/37?key="+URLEncoder.encode("bw/PbVaYQA8Y6K%2blL26zACaAXZ/HnK6YaEuVcgk4MZY="), null, new Response.Listener<JSONObject>() {
	  
	                         @Override
	                         public void onResponse(JSONObject response) {
	                             if (response != null) {
	                                 System.out.println("::::: "+response);
	                             }
	                         }
	                     }, new Response.ErrorListener() {
	  
	                         @Override
	                         public void onErrorResponse(VolleyError error) {
	                             //VolleyLog.d(TAG, "Error: " + error.getMessage());
	                         }
	                     });
	  
	             // Adding request to volley request queue
	             AppController.getInstance().addToRequestQueue(jsonReq);
			}
        	
        });
        
        final Button commentButton = (Button) convertView.findViewById(R.id.comment);
        
        commentButton.setOnClickListener(new Button.OnClickListener(){

        	   @Override
        	   public void onClick(View arg0) {
        		   LayoutInflater layoutInflater 
        		   = (LayoutInflater)AppController.getInstance()
        		   .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        		   View popupView = layoutInflater.inflate(R.layout.comment_popup, null);  
        		   final PopupWindow popupWindow = new PopupWindow(
        				   popupView, 
        				   ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT, true);

        		   ListView list=(ListView)popupView.findViewById(R.id.commentList);
        		   commentItems = new ArrayList<CommentItem>();
        		   commentListAdapter = new CommentListAdapter(activity, commentItems);
        		   list.setAdapter(commentListAdapter);
        		   JSONArray obj;
        		   try {
        			   obj = new JSONArray(item.getComments());
        			   parseJsoncomment(obj);
        		   } catch (JSONException e) {
        			   // TODO Auto-generated catch block
        			   e.printStackTrace();
        		   }

        		   popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

        		   Button btnDismiss = (Button)popupView.findViewById(R.id.btn_close_popup);
        		   btnDismiss.setOnClickListener(new Button.OnClickListener(){

        			   @Override
        			   public void onClick(View v) {
        				   // TODO Auto-generated method stub
        				   popupWindow.dismiss();
        			   }});

        		   popupWindow.setBackgroundDrawable(new BitmapDrawable());
        		   popupWindow.setOutsideTouchable(true);
        		   //popupWindow.showAsDropDown(commentButton, 50, -30);



        	   }});
        
        
 
        return convertView;
    }
    
    private void parseJsoncomment(JSONArray obj) {
        try {
            JSONArray feedArray = obj;
 
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
 
                CommentItem item = new CommentItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("on"));
 
                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                //item.setImge(image);
                item.setStatus(feedObj.getString("d"));
                item.setProfilePic("http://192.168.1.2:9000/image/get-mini-image-by-id/"+feedObj.getString("oid"));
                item.setTimeStamp(feedObj.getString("cd"));
 
                // url might be null sometimes
                /*String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);*/
 
                commentItems.add(item);
            }
 
            // notify data changes to list adapater
            commentListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    
 
}