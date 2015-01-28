package info.androidhive.listviewfeed;

import info.androidhive.listviewfeed.adapter.FeedListAdapter;
import info.androidhive.listviewfeed.app.AppController;
import info.androidhive.listviewfeed.data.FeedItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
 
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "mobile/get-newsfeeds";
    public SharedPreferences session = null;
 
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = getSharedPreferences("prefs", 0);
 
        listView = (ListView) findViewById(R.id.list);
 
        feedItems = new ArrayList<FeedItem>();
 
        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter); 
        listView.setOnScrollListener(new InfiniteScrollListener(5) {
			
			@Override
			public void loadMore(int page, int totalItemsCount) {
				// TODO Auto-generated method stub
				System.out.println(page+":::::::::"+totalItemsCount);
				getNewsFeed();
			}
		});
         
        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        getActionBar().setIcon(
                   new ColorDrawable(getResources().getColor(android.R.color.transparent)));
 
        // We first check for cached request
        getNewsFeed();
 
    }
 
    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    
    private void getNewsFeed(){
    	System.out.println(":::::::::::::::::::: "+session.getString("sessionID", null));
    	String url = null;
		try {
			url = getResources().getString(R.string.base_url)+URL_FEED+"?key="+URLEncoder.encode(session.getString("sessionID", null), "UTF-8");
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	System.out.println(" :::::::::: "+url);
		
    	Cache cache = AppController.getInstance().getRequestQueue().getCache();
    		// making fresh volley request and getting json
    		JsonObjectRequest jsonReq = new JsonObjectRequest(Method.POST,
    				url, null, new Response.Listener<JSONObject>() {

    			@Override
    			public void onResponse(JSONObject response) {
    				VolleyLog.d(TAG, "Response: " + response.toString());
    				if (response != null) {
    					parseJsonFeed(response);
    				}
    			}
    		}, new Response.ErrorListener() {

    			@Override
    			public void onErrorResponse(VolleyError error) {
    				VolleyLog.d(TAG, "Error: " + error.getMessage());
    			}
    		});

    		// Adding request to volley request queue
    		AppController.getInstance().addToRequestQueue(jsonReq);
    } 
    
    private void parseJsonFeed(JSONObject response) {
    	try {
    		JSONArray feedArray = response.getJSONArray("posts");

    		for (int i = 0; i < feedArray.length(); i++) {
    			JSONObject feedObj = (JSONObject) feedArray.get(i);

    			FeedItem item = new FeedItem();
    			item.setId(feedObj.getInt("id"));
    			item.setName(feedObj.getString("p"));

    			// Image might be null sometimes
    			String image = feedObj.isNull("image") ? null : feedObj
    					.getString("image");
    			//item.setImge(image);
    			item.setStatus(feedObj.getString("pt"));
    			item.setProfilePic("http://192.168.2.7:9000/image/get-mini-image-by-id/"+feedObj.getString("oid"));
    			item.setTimeStamp(feedObj.getString("t"));
    			item.setComments(feedObj.getString("cs"));

    			// url might be null sometimes
    			/*String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);*/

    			feedItems.add(item);
    		}

    		// notify data changes to list adapater
    		listAdapter.notifyDataSetChanged();
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    }




}