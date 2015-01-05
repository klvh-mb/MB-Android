package com.androidexample.customlistview;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import URLParsing.JSONParser;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.samples.hellofacebook.R;


public class CustomListViewAndroidExample extends Activity {

	
	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_list_view_android_example);
		session = getSharedPreferences("prefs", 0);
		CustomListView = this;
		
		*//******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********//*
		setListData();
		
		Resources res =getResources(); 
        list=(ListView)findViewById(R.id.list);
        
        *//**************** Create Custom Adapter *********//*
        adapter=new CustomAdapter(CustomListView, CustomListViewValuesArr,res);
        list.setAdapter(adapter);
		
	}

	*//****** Function to set data in ArrayList *************//*
    public void setListData()
    {
    	
    	JSONArray array = null;
	    try {
	    	String url = "http://192.168.2.7:9000/mobile/get-newsfeeds?key="+URLEncoder.encode(session.getString("sessionID", null), "UTF-8");
			 
			JSONParser jsonParser = new JSONParser();
			String json = jsonParser.getJSONFromUrlGET(url, null);
		
			JSONObject jsonObj = new JSONObject(json);
			array = new JSONArray(jsonObj.getString("posts"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		for (int i = 0; i < array.length(); i++) {
			try {
				final ListModel sched = new ListModel();

				*//******* Firstly take data in model object ******//*

				sched.setCompanyName("Company "+array.getJSONObject(i).getString("p"));

				sched.setImage("image"+i);
				sched.setUrl(array.getJSONObject(i).getString("pt"));

				*//******** Take Model Object in ArrayList **********//*
				CustomListViewValuesArr.add(sched);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
    }
    
    public void onItemClick(int mPosition)
    {
    	ListModel tempValues = (ListModel) CustomListViewValuesArr.get(mPosition);
    	
    	Toast.makeText(CustomListView, 
    			""+tempValues.getCompanyName()+" \nImage:"+tempValues.getImage()+" \nUrl:"+tempValues.getUrl(), 
    			Toast.LENGTH_LONG)
    	.show();
    }
   

}
*/
	
	public static SharedPreferences session = null;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    public CustomListViewAndroidExample(Context baseContext) {
		// TODO Auto-generated constructor stub
	}
    
    public CustomListViewAndroidExample() {
		
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list_view_android_example);
        session = getSharedPreferences("prefs", 0);
        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        ///getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_websearch:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            //intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        //getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends ListFragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";
        ListView list;
    	CustomAdapter adapter;
    	public  PlanetFragment CustomListView = null;
    	public  ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();
    	

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            //int i = getArguments().getInt(ARG_PLANET_NUMBER);
            setListData();
            CustomListView = this;
            Resources res =getResources(); 
            list=(ListView)rootView.findViewById(R.id.list);
            adapter= new CustomAdapter(getActivity(), CustomListViewValuesArr,res);
            list.setAdapter(adapter);
            /*String planet = getResources().getStringArray(R.array.planets_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                            "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);*/
            return rootView;
            
           /* setContentView(R.layout.activity_custom_list_view_android_example);
    		session = getSharedPreferences("prefs", 0);
    		CustomListView = this;
    		
    		*//******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********//*
    		setListData();
    		
    		Resources res =getResources(); 
            list=(ListView)findViewById(R.id.list);
            
            *//**************** Create Custom Adapter *********//*
            adapter=new CustomAdapter(CustomListView, CustomListViewValuesArr,res);
            list.setAdapter(adapter);*/
        }

		private void setListData() {
			JSONArray array = null;
		    try {
		    	String url = "http://192.168.2.7:9000/mobile/get-newsfeeds?key="+URLEncoder.encode(session.getString("sessionID", null), "UTF-8");
				 
				JSONParser jsonParser = new JSONParser();
				String json = jsonParser.getJSONFromUrlGET(url, null);
			
				JSONObject jsonObj = new JSONObject(json);
				array = new JSONArray(jsonObj.getString("posts"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
			for (int i = 0; i < array.length(); i++) {
				try {
					final ListModel sched = new ListModel();

					//******* Firstly take data in model object ******//*

					sched.setCompanyName("Company "+array.getJSONObject(i).getString("p"));

					sched.setImage("image"+i);
					sched.setUrl(array.getJSONObject(i).getString("pt"));

					//******** Take Model Object in ArrayList **********//*
					CustomListViewValuesArr.add(sched);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
    }
}