package miniBean.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.DistrictListAdapter;
import miniBean.adapter.PNListAdapter;
import miniBean.app.AppController;
import miniBean.app.DistrictCache;
import miniBean.util.ActivityUtil;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.LocationVM;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SchoolListFragment extends MyFragment {

    private static final String TAG = SchoolListFragment.class.getName();
    private GridView districtGrid;
    private List<LocationVM> locationVMList;
    private List<PreNurseryVM> pnVMList;
    private List<PreNurseryVM> searchVMList;
    private TextView yourDistrictNameText,districtNameText,searchKey,totalResultText,noOfSchools,tooManyResultsText;
    private DistrictListAdapter districtListAdapter;
    private ListView listView;
    private Spinner couponSpinner,typeSpinner,timeSpinner,curriculumSpinner;
    private RelativeLayout nurseryLayout,boxLayout,searchResultLayout,searchLayout;
    private LinearLayout cancelLayout;
    private SearchView searchWindow;
    private View listHeader;
    private String currValue,cpValue,typeValue,timeValue;

    private int dismissSearchPressCount = 0;

    private PNListAdapter listAdapter;

    private ActivityUtil activityUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.school_list_fragment, container, false);

        activityUtil = new ActivityUtil(getActivity());

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
        tooManyResultsText = (TextView) listHeader.findViewById(R.id.tooManyResultsText);
        searchLayout = (RelativeLayout) listHeader.findViewById(R.id.searchView);

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
        searchWindow = (SearchView) listHeader.findViewById(R.id.searchWindow);

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
            public void onClick(View v) {
                /*Fragment fragment = new SchoolListFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.children_fragement, fragment).addToBackStack(null).commit();
                searchResultLayout.setVisibility(View.GONE);
                */

                dismissSearchMode();
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchWindow.setIconified(false);

                /*
                // don't need this anymore, set android:windowSoftInputMode="adjustPan"
                // for MainActivity in AdnroidManifesh.xml
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //listHeader.scrollTo(0, 0);
                        //listView.smoothScrollToPosition(0);
                        listView.setSelection(0);
                    }
                }, 500);
                */
            }
        });

        /*
        searchWindow.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("===>", "keyCode=" + keyCode);
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!searchWindow.isIconified()) {
                        activityUtil.hideInputMethodWindow(searchWindow);

                        if (dismissSearchPressCount > 0) {
                            dismissSearchMode();
                        } else {
                            dismissSearchPressCount++;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        */

        searchWindow.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                boxLayout.setVisibility(View.GONE);
                nurseryLayout.setVisibility(View.GONE);
                searchResultLayout.setVisibility(View.VISIBLE);
                if (!StringUtils.isEmpty(s)) {
                    searchByName(s);
                } else {
                    Toast.makeText(getActivity(), "Enter name to search....", Toast.LENGTH_LONG).show();
                }
                activityUtil.hideInputMethodWindow(searchWindow);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("===>","s="+s);
                if (s.length() > 1) {   // start to search with 2 chars
                    boxLayout.setVisibility(View.GONE);
                    nurseryLayout.setVisibility(View.GONE);
                    searchResultLayout.setVisibility(View.VISIBLE);
                    if (!StringUtils.isEmpty(s)) {
                        searchByName(s);
                    }
                }
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

    @Override
    public boolean allowBackPressed() {
        if (!searchWindow.isIconified()) {
            activityUtil.hideInputMethodWindow(searchWindow);

            if (dismissSearchPressCount > 0) {
                dismissSearchMode();
            } else {
                dismissSearchPressCount++;
            }
            return false;
        }
        return true;
    }

    private void dismissSearchMode() {
        boxLayout.setVisibility(View.VISIBLE);
        nurseryLayout.setVisibility(View.VISIBLE);
        searchResultLayout.setVisibility(View.GONE);
        searchWindow.setIconified(true);
        activityUtil.hideInputMethodWindow(searchWindow);

        searchVMList = new ArrayList<PreNurseryVM>();
        listAdapter.refresh(pnVMList);
        dismissSearchPressCount = 0;
    }

    private void selectDistrict(int index) {
        String district = districtListAdapter.getItem(index).getDisplayName();
        districtNameText.setText(district);
        districtListAdapter.setSelectedItem(index);
        districtListAdapter.notifyDataSetChanged();
        getPNsByDistrict(locationVMList.get(index).getId(), district);
        searchWindow.setIconified(true);
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
                Log.d(SchoolListFragment.class.getSimpleName(), "[" + district + "] returned " + preNurseryVMs.size() + " pns");
                pnVMList = preNurseryVMs;
                applyFilters();
                noOfSchools.setText(preNurseryVMs.size() + "");
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void applyFilters(){
        if(cpValue!=null) {
            pnVMList = cpFilter(cpValue, pnVMList);
            Log.d(this.getClass().getSimpleName(), "Filter: coupon="+cpValue+" size="+pnVMList.size());
        }

        if(currValue!=null) {
            pnVMList = currFilter(currValue, pnVMList);
            Log.d(this.getClass().getSimpleName(), "Filter: currValue="+currValue+" size="+pnVMList.size());
        }

        if(timeValue!=null) {
            pnVMList = timeFilter(timeValue, pnVMList);
            Log.d(this.getClass().getSimpleName(), "Filter: timeValue="+timeValue+" size="+pnVMList.size());
        }

        if(typeValue!=null) {
            pnVMList = typeFilter(typeValue, pnVMList);
            Log.d(this.getClass().getSimpleName(), "Filter: typeValue="+typeValue+" size="+pnVMList.size());
        }

        listAdapter = new PNListAdapter(getActivity(),pnVMList);
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

    private List<PreNurseryVM> typeFilter(String orgtValue,List<PreNurseryVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(orgtValue)) {
            return filteredVMList;
        }

        List<PreNurseryVM> vm = new ArrayList<PreNurseryVM>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            if (orgtValue.equalsIgnoreCase(filteredVMList.get(i).getOrgt())) {
                vm.add(filteredVMList.get(i));
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
            if (currValue.equalsIgnoreCase(filteredVMList.get(i).getCurt())) {
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
            if (!StringUtils.isEmpty(filteredVMList.get(i).getCt()) &&
                    filteredVMList.get(i).getCt().contains(ctValue)) {
                vm.add(filteredVMList.get(i));
            }
        }
        return vm;
    }

    private void searchByName(final String query){
        dismissSearchPressCount = 0;
        AppController.getApi().searchPnByName(query,AppController.getInstance().getSessionId(),new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> preNurseryVMs, Response response) {
                Log.d(SchoolListFragment.class.getSimpleName(), "searchByName: returns " + preNurseryVMs.size() + " schools");
                searchKey.setText("[ "+query+" ]");
                totalResultText.setText("" + preNurseryVMs.size());

                // too many results
                searchVMList = new ArrayList<PreNurseryVM>();
                if (preNurseryVMs.size() > DefaultValues.MAX_SCHOOLS_SEARCH_COUNT) {
                    tooManyResultsText.setVisibility(View.VISIBLE);
                } else {
                    tooManyResultsText.setVisibility(View.GONE);
                    searchVMList = preNurseryVMs;
                }

                //PNListAdapter resultListAdapter = new PNListAdapter(getActivity(), resultList);
                //listView.setAdapter(resultListAdapter);
                listAdapter.refresh(searchVMList);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}