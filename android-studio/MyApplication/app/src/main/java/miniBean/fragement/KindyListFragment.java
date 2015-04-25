package miniBean.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.KindyListAdapter;
import miniBean.app.AppController;
import miniBean.app.MyApi;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.KindergartenVM;
import miniBean.viewmodel.LocationVM;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class KindyListFragment extends Fragment {

    private static final String TAG = KindyListFragment.class.getName();
    private GridView gridView;
    private List<String> locations;
    private List<LocationVM> locationVMList;
    private List<KindergartenVM> kindergartenVMList;
    private TextView districtText,distName,searchKey,totalResultText,noOfKindy;
    private ArrayAdapter<String> locationAdapter;
    private KindyListAdapter kindyListAdapter;
    private ListView PNList;
    private Spinner couponSpinner,typeSpinner,timeSpinner,curriculumSpinner;
    private RelativeLayout nurseryLayout,boxLayout,searchResultLayout;
    LinearLayout cancelLayout;
    private SearchView searchText;
    private MyApi api;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.school_list_fragment, container, false);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        api = restAdapter.create(MyApi.class);

        couponSpinner= (Spinner) view.findViewById(R.id.couponSpinner);
        typeSpinner= (Spinner) view.findViewById(R.id.typeSpinner);
        timeSpinner= (Spinner) view.findViewById(R.id.timeSpinner);
        curriculumSpinner= (Spinner) view.findViewById(R.id.curriculumSpinner);
        searchKey= (TextView) view.findViewById(R.id.searchText);
        totalResultText= (TextView) view.findViewById(R.id.searchCountText);
        searchResultLayout= (RelativeLayout) view.findViewById(R.id.searchResultLayout);
        cancelLayout= (LinearLayout) view.findViewById(R.id.cancelLayout);
        noOfKindy= (TextView) view.findViewById(R.id.noOfKindergartens);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, DefaultValues.FILTER_SCHOOLS_COUPON);
        couponSpinner.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, DefaultValues.FILTER_SCHOOLS_CURRICULUM);
        curriculumSpinner.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, DefaultValues.FILTER_SCHOOLS_TYPE);
        typeSpinner.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, DefaultValues.FILTER_SCHOOLS_TIME);
        timeSpinner.setAdapter(adapter3);


        gridView = (GridView)view.findViewById(R.id.gridView1);
        districtText= (TextView) view.findViewById(R.id.districtNameText);
        distName= (TextView) view.findViewById(R.id.distName);
        PNList= (ListView) view.findViewById(R.id.schoolList);
        gridView.setDrawSelectorOnTop(false);
        searchText= (SearchView) view.findViewById(R.id.searchWindow);

        locationVMList = new ArrayList<LocationVM>();
        kindergartenVMList=new ArrayList<KindergartenVM>();

        nurseryLayout= (RelativeLayout) view.findViewById(R.id.nurseryLayout);
        boxLayout= (RelativeLayout) view.findViewById(R.id.boxLayout);

        districtText.setText(AppController.getUserLocation().getDisplayName());
        distName.setText(AppController.getUserLocation().getDisplayName());

        getKGByDistrict(AppController.getUserLocation().getId());


        kindyListAdapter = new KindyListAdapter(getActivity(),kindergartenVMList);

        PNList.setAdapter(kindyListAdapter);



        PNList.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });



        setLocation();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String loc = locationAdapter.getItem(i);
                districtText.setText(loc);
                distName.setText(loc);
                getKGByDistrict(locationVMList.get(i).getId());
            }
        });

        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new KindyListFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.children_fragement, fragment).addToBackStack(null).commit();
                searchResultLayout.setVisibility(View.GONE);
            }
        });

        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                boxLayout.setVisibility(View.GONE);
                nurseryLayout.setVisibility(View.GONE);
                searchResultLayout.setVisibility(View.VISIBLE);
                if (!s.equals("")){
                    searchByName(s);
                }else{
                    Toast.makeText(getActivity(),"Enter name to search....",Toast.LENGTH_LONG).show();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        curriculumSpinner.post(new Runnable() {
            public void run() {
                curriculumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        filter(curriculumSpinner.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
        return view;
    }

    private void setLocation(){
        AppController.getApi().getAllDistricts(AppController.getInstance().getSessionId(),new Callback< List< LocationVM>>(){
            @Override
            public void success(List<LocationVM> locationVMs, Response response) {
                locations = new ArrayList<String>();

                for(int i=0; i<locationVMs.size(); i++){
                    locations.add(locationVMs.get(i).getDisplayName());
                    locationVMList.addAll(locationVMs);
                }
                locationAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,locations);
                gridView.setAdapter(locationAdapter);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }

    private void getKGByDistrict(Long id) {
        AppController.getApi().getKGByDistricts(id,AppController.getInstance().getSessionId(),new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> kindergartenVMs, Response response) {
                System.out.println("url kind:::::"+response.getUrl());
                kindergartenVMList.addAll(kindergartenVMs);
                System.out.println("size:::::"+kindergartenVMList.size());
                noOfKindy.setText(""+kindergartenVMList.size());
                kindyListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

    }

    private void filter(String value){
        List<KindergartenVM> vms=new ArrayList<KindergartenVM>();
        System.out.println("filter"+value);
        if(!value.equals("")) {
            for (int j = 0; j <= (kindergartenVMList.size() - 1); j++) {
                if (kindergartenVMList.get(j).getCurt().toLowerCase().equals(value.toLowerCase())) {
                    vms.add(kindergartenVMList.get(j));
                }
            }
            System.out.println("fsize:::::"+vms.size());
            kindyListAdapter = new KindyListAdapter(getActivity(), vms);
        }else {
            kindyListAdapter = new KindyListAdapter(getActivity(), kindergartenVMList);
        }
        PNList.setAdapter(kindyListAdapter);
    }

    private void searchByName(final String query){
        AppController.getApi().searchKGByName(query,AppController.getInstance().getSessionId(),new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> kindergartenVMs, Response response) {
                searchKey.setText("["+query+"]");
                totalResultText.setText(""+kindergartenVMs.size());
                KindyListAdapter resultListAdapter;
                resultListAdapter = new KindyListAdapter(getActivity(), kindergartenVMs);
                PNList.setAdapter(resultListAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
