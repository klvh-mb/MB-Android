package miniBean.fragement;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import miniBean.InfiniteScrollListener;
import miniBean.MyApi;
import miniBean.R;
import miniBean.adapter.FeedListAdapter;
import miniBean.app.AppController;
import miniBean.data.FeedItem;
import retrofit.RestAdapter;


/**
 * Created by Tobias on 30.12.2014.
 */
public class NewsFeedFragement extends AbstractFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = NewsFeedFragement.class.getName();

    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "/get-newsfeeds";
    public SharedPreferences session = null;
    public MyApi api ;

    /**
     * Generating the fragment
     *
     * @author huber
     * @since Dec 30, 2014
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //getMaEventsSao().addCacheChangedListener(this);
        View view = inflater.inflate(R.layout.activity_main, container, false);
        session = getActivity().getSharedPreferences("prefs", 0);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://192.168.2.7:9000")
                .build();

        api = restAdapter.create(MyApi.class);

        listView = (ListView) view.findViewById(R.id.list);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        listView.setOnScrollListener(new InfiniteScrollListener(5) {

            @Override
            public void loadMore(int page, int totalItemsCount) {
                // TODO Auto-generated method stub
                System.out.println("Page ::::::::::: "+(page-1));
                getNewsFeed(page-1);
            }
        });

        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        /*getActionBar().setIcon(
                   new ColorDrawable(getResources().getColor(android.R.color.transparent)));*/

        // We first check for cached request
        getNewsFeed(0);

        return view;
    }

    private void getNewsFeed(int offset){
        URL_FEED = URL_FEED +"/"+0L;
        String url = null;
        try {
            url = getResources().getString(R.string.base_url)+URL_FEED+"?key="+ URLEncoder.encode(session.getString("sessionID", null), "UTF-8");
        } catch (Resources.NotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        // making fresh volley request and getting json
        System.out.println(" :::::::::: "+url);
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                System.out.println(" :::::::::: Response: " + response.toString());
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
                item.setProfilePic(getResources().getString(R.string.base_url)+"/image/get-mini-image-by-id/"+feedObj.getString("oid"));
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

    /**
     * Implementation of OnClickListener e.g. the logout button has been clicked
     *
     * @author huber
     * @since Dec 30, 2014
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
           /* case R.id.btnLogout:
                Session.getActiveSession().closeAndClearTokenInformation();
                listener.onUserLoggedOut();
                break;
            case R.id.btnGoogle:
                new ContactGoogleTask().executeMultiThreaded();
                break;*/
        }
    }


    /**
     * Implementation of OnItemClickListner e.g. the when item from list is clicked
     *
     * @author jagbirs
     * @since Jan 27, 2015
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
       /* listAdapter.Holder holder = (MaEventsListAdapter.Holder) view.getTag();
        listener.onEventSelected(holder.eventId);*/
    }



    /**
     * /****************************************************************************************
     * listeners that are implemented by the AbstractActionBar Activity
     * <p/>
     * Although it’s possible for Fragments to communicate directly using the host Activity’s Fragment Manager, it’s generally considered better practice to
     * use the Activity as an intermediary. This allows the Fragments to be as independent and loosely coupled as possible, with the responsibility for
     * deciding how an event in one Fragment should affect the overall UI falling to the host Activity.
     * <p/>
     * Where your Fragment needs to share events with its host Activity (such as signaling UI selections), it’s good practice to create a callback interface
     * within the Fragment that a host Activity must implement.
     * <p/>
     * We use code from within a Fragment class that defines a public event listener interface. The onAttach handler is overridden to obtain a
     * reference to the host Activity, confirming that it implements the required interface.
     * <p/>
     * Example: Use the function abstractListener.onLoadingFinished() inside a fragment class to signal the activity, that the loading is finished. The
     * activity has to decide how to handle this event.
     *
     * @author huber
     * @since Dec 30, 2014
     */
    private Listener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Listener) {
            listener = (Listener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implemenet MaEventsFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface Listener {
        /*public void onUserLoggedOut();
        public void onEventSelected(Long eventId);*/
    }

    /**
     * Listener - END
     ****************************************************************************************/


    /**
     * *************************************************************************************
     * CacheListener - BEGIN listen to the cache, if sth. got changed. Maybe the
     * fragment has to be refreshed.
     *
     * @author huber
     * @since Jan 17, 2014
     */
  /*  @Override
    public void cacheChangedEvent(CacheChangedEvent event) {
        final MaEventsObject data = (MaEventsObject) event.getData();
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                refreshGui(data);
            }
        });
    }*/

    /**
     * CacheListener - END
     ****************************************************************************************/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //getMaEventsSao().removeCacheChangedListener(this);
    }


}
