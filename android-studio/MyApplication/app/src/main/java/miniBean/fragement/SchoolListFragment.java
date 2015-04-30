package miniBean.fragement;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import miniBean.adapter.DistrictListAdapter;
import miniBean.adapter.PNListAdapter;
import miniBean.app.AppController;
import miniBean.app.DistrictCache;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.LocationVM;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SchoolListFragment extends Fragment {

    private static final String TAG = SchoolListFragment.class.getName();
    private GridView districtGrid;
    private List<LocationVM> locationVMList;
    private List<PreNurseryVM> pnVMList;
    private TextView yourDistrictNameText,districtNameText,searchKey,totalResultText,noOfSchools;
    private DistrictListAdapter districtListAdapter;
    private ListView listView;
    private Spinner couponSpinner,typeSpinner,timeSpinner,curriculumSpinner;
    private RelativeLayout nurseryLayout,boxLayout,searchResultLayout,searchLayout;
    private LinearLayout cancelLayout;
    private SearchView searchText;
    private View listHeader;
    private String currValue,cpValue,typeValue,timeValue;

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
        searchLayout= (RelativeLayout) listHeader.findViewById(R.id.searchView);

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
        yourDistrictNameText = (TextView) listHeader.findViewById(R.id.yourDistrictNameText);
        districtNameText = (TextView) listHeader.findViewById(R.id.districtNameText);
        searchText = (SearchView) listHeader.findViewById(R.id.searchWindow);

        locationVMList = new ArrayList<>();
        pnVMList = new ArrayList<>();

        nurseryLayout = (RelativeLayout) listHeader.findViewById(R.id.nurseryLayout);
        boxLayout = (RelativeLayout) listHeader.findViewById(R.id.boxLayout);

        yourDistrictNameText.setText(AppController.getUserLocation().getDisplayName());
        districtNameText.setText(AppController.getUserLocation().getDisplayName());

        // list
        listView = (ListView) view.findViewById(R.id.schoolList);
        listView.addHeaderView(listHeader);

        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        getPNsByDistrict(AppController.getUserLocation().getId(), AppController.getUserLocation().getDisplayName());

        setDistricts();

        districtGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectDistrict(i);
            }
        });

        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SchoolListFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.children_fragement, fragment).addToBackStack(null).commit();
                searchResultLayout.setVisibility(View.GONE);
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setIconified(false);
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
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        curriculumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currValue = curriculumSpinner.getSelectedItem().toString();
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeValue = typeSpinner.getSelectedItem().toString();
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeValue = timeSpinner.getSelectedItem().toString();
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        couponSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cpValue = couponSpinner.getSelectedItem().toString();
                applyFilters();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    private void selectDistrict(int index) {
        String district = districtListAdapter.getItem(index).getDisplayName();
        districtNameText.setText(district);
        districtListAdapter.setSelectedItem(index);
        districtListAdapter.notifyDataSetChanged();
        getPNsByDistrict(locationVMList.get(index).getId(), district);
    }

    private void setDistricts(){
        List<LocationVM> districts = DistrictCache.getDistricts();
        locationVMList.clear();
        locationVMList.addAll(districts);
        districtListAdapter = new DistrictListAdapter(getActivity(),districts);
        districtGrid.setAdapter(districtListAdapter);
    }

    private void getPNsByDistrict(final Long id, final String district){
        AppController.getApi().getPNsByDistricts(id, AppController.getInstance().getSessionId(), new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> preNurseryVMs, Response response) {
                Log.d(SchoolListFragment.class.getSimpleName(), "["+district+"] returned "+preNurseryVMs.size()+" pns");
                pnVMList.clear();
                pnVMList.addAll(preNurseryVMs);
                applyFilters();
                noOfSchools.setText(preNurseryVMs.size()+"");
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void applyFilters(){
        List<PreNurseryVM> filteredVMList = new ArrayList<PreNurseryVM>();
        filteredVMList.addAll(pnVMList);

        if(cpValue!=null) {
            filteredVMList = cpFilter(cpValue, filteredVMList);
            Log.d(this.getClass().getSimpleName(), "Filter: coupon="+cpValue+" size="+filteredVMList.size());
        }

        if(currValue!=null) {
            filteredVMList = currFilter(currValue, filteredVMList);
            Log.d(this.getClass().getSimpleName(), "Filter: currValue="+currValue+" size="+filteredVMList.size());
        }
        if(timeValue!=null) {
            filteredVMList = timeFilter(timeValue, filteredVMList);
            Log.d(this.getClass().getSimpleName(), "Filter: timeValue="+timeValue+" size="+filteredVMList.size());
        }

        if(typeValue!=null) {
            filteredVMList = typeFilter(typeValue, filteredVMList);
            Log.d(this.getClass().getSimpleName(), "Filter: typeValue="+typeValue+" size="+filteredVMList.size());
        }
        PNListAdapter listAdapter = new PNListAdapter(getActivity(),filteredVMList);
        listView.setAdapter(listAdapter);
    }

    private List<PreNurseryVM> cpFilter(String cpValue,List<PreNurseryVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(cpValue)) {
            return filteredVMList;
        }

        List<PreNurseryVM> vm = new ArrayList<PreNurseryVM>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            if (DefaultValues.FILTER_SCHOOLS_YES.equals(cpValue)) {
                if(filteredVMList.get(i).isCp()) {
                    vm.add(filteredVMList.get(i));
                }
            } else if (DefaultValues.FILTER_SCHOOLS_NO.equals(cpValue)) {
                if(!filteredVMList.get(i).isCp()){
                    vm.add(filteredVMList.get(i));
                }
            }
        }
        return vm;
    }


    private List<PreNurseryVM> currFilter(String currValue,List<PreNurseryVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(currValue)) {
            return filteredVMList;
        }

        List<PreNurseryVM> vm = new ArrayList<PreNurseryVM>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            if (filteredVMList.get(i).getCurt().contains(currValue)) {
                vm.add(filteredVMList.get(i));
            }
        }
        return vm;
    }

    private List<PreNurseryVM> timeFilter(String ctValue,List<PreNurseryVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(ctValue)) {
            return filteredVMList;
        }

        List<PreNurseryVM> vm = new ArrayList<PreNurseryVM>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            if (filteredVMList.get(i).getCt().contains(ctValue)) {
                vm.add(filteredVMList.get(i));
            }
        }
        return vm;
    }

    private List<PreNurseryVM> typeFilter(String orgtValue,List<PreNurseryVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(orgtValue)) {
            return filteredVMList;
        }

        List<PreNurseryVM> vm = new ArrayList<PreNurseryVM>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            if (filteredVMList.get(i).getOrgt().contains(orgtValue)) {
                vm.add(filteredVMList.get(i));
            }
        }
        return vm;
    }

    private void searchByName(final String query){
        AppController.getApi().searchPnByName(query,AppController.getInstance().getSessionId(),new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> preNurseryVMs, Response response) {
                searchKey.setText("[ "+query+" ]");
                totalResultText.setText(""+preNurseryVMs.size());
                PNListAdapter resultListAdapter;
                resultListAdapter = new PNListAdapter(getActivity(), preNurseryVMs);
                listView.setAdapter(resultListAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}