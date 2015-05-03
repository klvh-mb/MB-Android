package miniBean.fragment;

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
import miniBean.util.DefaultValues;
import miniBean.viewmodel.KindergartenVM;
import miniBean.viewmodel.LocationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class KindyListFragment extends Fragment {

    private static final String TAG = KindyListFragment.class.getName();
    private GridView districtGrid;
    private List<String> locations;
    private List<LocationVM> locationVMList;
    private List<KindergartenVM> kindergartenVMList;
    private TextView districtText,distName,searchKey,totalResultText,noOfSchools;
    private ArrayAdapter<String> districtListAdapter;
    private KindyListAdapter listAdapter;
    private ListView listView;
    private Spinner couponSpinner,typeSpinner,timeSpinner,curriculumSpinner;
    private RelativeLayout nurseryLayout,boxLayout,searchResultLayout;
    private LinearLayout cancelLayout;
    private SearchView searchText;
    private View listHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.school_list_fragment, container, false);

        // header
        listHeader = inflater.inflate(R.layout.school_list_fragment_header, null);
        couponSpinner = (Spinner) listHeader.findViewById(R.id.couponSpinner);
        typeSpinner = (Spinner) listHeader.findViewById(R.id.typeSpinner);
        timeSpinner = (Spinner) listHeader.findViewById(R.id.timeSpinner);
        curriculumSpinner = (Spinner) listHeader.findViewById(R.id.curriculumSpinner);
        searchKey = (TextView) listHeader.findViewById(R.id.searchText);
        totalResultText = (TextView) listHeader.findViewById(R.id.searchCountText);
        searchResultLayout = (RelativeLayout) listHeader.findViewById(R.id.searchResultLayout);
        cancelLayout = (LinearLayout) listHeader.findViewById(R.id.cancelLayout);
        noOfSchools = (TextView) listHeader.findViewById(R.id.noOfSchools);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, DefaultValues.FILTER_SCHOOLS_COUPON);
        couponSpinner.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, DefaultValues.FILTER_SCHOOLS_CURRICULUM);
        curriculumSpinner.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, DefaultValues.FILTER_SCHOOLS_TYPE);
        typeSpinner.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, DefaultValues.FILTER_SCHOOLS_TIME);
        timeSpinner.setAdapter(adapter3);

        districtGrid = (GridView)listHeader.findViewById(R.id.districtGrid);
        districtGrid.setDrawSelectorOnTop(false);
        districtText = (TextView) listHeader.findViewById(R.id.districtNameText);
        distName = (TextView) listHeader.findViewById(R.id.districtNameText);
        searchText = (SearchView) listHeader.findViewById(R.id.searchWindow);

        locationVMList = new ArrayList<LocationVM>();
        kindergartenVMList = new ArrayList<KindergartenVM>();

        nurseryLayout = (RelativeLayout) view.findViewById(R.id.nurseryLayout);
        boxLayout = (RelativeLayout) view.findViewById(R.id.boxLayout);

        districtText.setText(AppController.getUserLocation().getDisplayName());
        distName.setText(AppController.getUserLocation().getDisplayName());

        // list
        listView = (ListView) view.findViewById(R.id.schoolList);
        listAdapter = new KindyListAdapter(getActivity(),kindergartenVMList);

        listView.addHeaderView(listHeader);
        listView.setAdapter(listAdapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        getKGsByDistrict(AppController.getUserLocation().getId());

        setDistricts();

        districtGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String loc = districtListAdapter.getItem(i);
                districtText.setText(loc);
                distName.setText(loc);
                getKGsByDistrict(locationVMList.get(i).getId());
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

    private void setDistricts(){
        AppController.getApi().getAllDistricts(AppController.getInstance().getSessionId(),new Callback< List< LocationVM>>(){
            @Override
            public void success(List<LocationVM> locationVMs, Response response) {
                locations = new ArrayList<String>();

                for(int i=0; i<locationVMs.size(); i++){
                    locations.add(locationVMs.get(i).getDisplayName());
                    locationVMList.addAll(locationVMs);
                }
                districtListAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,locations);
                districtGrid.setAdapter(districtListAdapter);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }

    private void getKGsByDistrict(Long id) {
        AppController.getApi().getKGsByDistricts(id, AppController.getInstance().getSessionId(), new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> kindergartenVMs, Response response) {
                System.out.println("url kind:::::" + response.getUrl());
                kindergartenVMList.addAll(kindergartenVMs);
                System.out.println("size:::::" + kindergartenVMList.size());
                noOfSchools.setText("" + kindergartenVMList.size());
                listAdapter.notifyDataSetChanged();
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
            listAdapter = new KindyListAdapter(getActivity(), vms);
        }else {
            listAdapter = new KindyListAdapter(getActivity(), kindergartenVMList);
        }
        listView.setAdapter(listAdapter);
    }

    private void searchByName(final String query){
        AppController.getApi().searchKGByName(query,AppController.getInstance().getSessionId(),new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> kindergartenVMs, Response response) {
                searchKey.setText("["+query+"]");
                totalResultText.setText(""+kindergartenVMs.size());
                KindyListAdapter resultListAdapter;
                resultListAdapter = new KindyListAdapter(getActivity(), kindergartenVMs);
                listView.setAdapter(resultListAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
