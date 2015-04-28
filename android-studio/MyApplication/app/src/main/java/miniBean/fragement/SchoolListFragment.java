package miniBean.fragement;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    private List<PreNurseryVM> preNurseryVMList;
    private List<PreNurseryVM> tempVMList;
    private TextView districtText,distName,searchKey,totalResultText,noOfSchools;
    private ArrayAdapter<String> locationAdapter;
    private DistrictListAdapter districtListAdapter;
    private PNListAdapter listAdapter;
    private ListView listView;
    private Spinner couponSpinner,typeSpinner,timeSpinner,curriculumSpinner;
    private RelativeLayout nurseryLayout,boxLayout,searchResultLayout;
    private LinearLayout cancelLayout;
    private SearchView searchText;
    private View listHeader;
    private int temp ;


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
        distName = (TextView) listHeader.findViewById(R.id.distName);
        searchText = (SearchView) listHeader.findViewById(R.id.searchWindow);

        locationVMList = new ArrayList<LocationVM>();
        preNurseryVMList = new ArrayList<PreNurseryVM>();
        tempVMList = new ArrayList<PreNurseryVM>();

        nurseryLayout = (RelativeLayout) listHeader.findViewById(R.id.nurseryLayout);
        boxLayout = (RelativeLayout) listHeader.findViewById(R.id.boxLayout);

        districtText.setText(AppController.getUserLocation().getDisplayName());
        distName.setText(AppController.getUserLocation().getDisplayName());

        // list
        listView = (ListView) view.findViewById(R.id.schoolList);
        listAdapter = new PNListAdapter(getActivity(),preNurseryVMList);

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

        getPNsByDistrict(AppController.getUserLocation().getId());

        setDistricts();

        districtGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                districtGrid.getChildAt(temp).setBackgroundColor(Color.parseColor("#FFFFFF"));
                String loc = districtListAdapter.getItem(i).getDisplayName();
                getPNsByDistrict(locationVMList.get(i).getId());
                districtGrid.getChildAt(i).setBackgroundColor(Color.parseColor("#57B154"));
                districtText.setText(loc);
                distName.setText(loc);
                temp=i;
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

        curriculumSpinner.post(new Runnable() {
            public void run() {
                curriculumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        //curtValue = curriculumSpinner.getSelectedItem().toString();
                        System.out.println("clicked::::");
                        filterControl(curriculumSpinner.getSelectedItem().toString(),timeSpinner.getSelectedItem().toString(),typeSpinner.getSelectedItem().toString());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });

        timeSpinner.post(new Runnable() {
            public void run() {
                timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        filterControl(curriculumSpinner.getSelectedItem().toString(),timeSpinner.getSelectedItem().toString(),typeSpinner.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });

        typeSpinner.post(new Runnable() {
            public void run() {
                typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        filterControl(curriculumSpinner.getSelectedItem().toString(),timeSpinner.getSelectedItem().toString(),typeSpinner.getSelectedItem().toString());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }
        });

        couponSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    private void setDistricts(){
        AppController.getApi().getAllDistricts(AppController.getInstance().getSessionId(),new Callback<List< LocationVM>>(){
            @Override
            public void success(List<LocationVM> locationVMs, Response response) {
                locationVMList.clear();
                locationVMList.addAll(locationVMs);

                // locationAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,locations);
                districtListAdapter = new DistrictListAdapter(getActivity(),locationVMs);
                districtGrid.setAdapter(districtListAdapter);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }

    private void getPNsByDistrict(Long id){
        AppController.getApi().getPNsByDistricts(id, AppController.getInstance().getSessionId(), new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> preNurseryVMs, Response response) {
                preNurseryVMList.clear();
                preNurseryVMList.addAll(preNurseryVMs);
                tempVMList.clear();
                tempVMList.addAll(preNurseryVMs);
                noOfSchools.setText(preNurseryVMList.size() + "");
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void filterControl(String curt,String ct,String orgt){

        if(curt!=null){
            tempVMList=curtFilter(curt,tempVMList);
        }
        if(ct!=null){
            tempVMList=timeFilter(ct,tempVMList);
        }
        if(orgt!=null){
            tempVMList=typeFilter(orgt,tempVMList);
        }

    }

    private List<PreNurseryVM> curtFilter(String curtValue,List<PreNurseryVM> tempVMList) {
        List<PreNurseryVM> vm=new ArrayList<PreNurseryVM>();
        System.out.println("curt" + curtValue);
        for (int j = 0; j <= (tempVMList.size() - 1); j++) {
            if (tempVMList.get(j).getCurt().toLowerCase().equals(curtValue.toLowerCase())) {
                vm.add(tempVMList.get(j));
            }
        }
        return vm;
    }

    private List<PreNurseryVM> timeFilter(String ctValue,List<PreNurseryVM> tempVMList) {
        List<PreNurseryVM> vms = new ArrayList<PreNurseryVM>();
        System.out.println("ctValue" + ctValue);
        for (int j = 0; j <= (tempVMList.size() - 1); j++) {
            if (tempVMList.get(j).getCt().toLowerCase().equals(ctValue.toLowerCase())) {
                vms.add(tempVMList.get(j));
            }
        }
        return vms;
    }

    private List<PreNurseryVM> typeFilter(String orgtValue,List<PreNurseryVM> tempVMList) {
        List<PreNurseryVM> vms = new ArrayList<PreNurseryVM>();
        System.out.println("orgtValue" + orgtValue);
        for (int j = 0; j <= (tempVMList.size() - 1); j++) {
            if (tempVMList.get(j).getOrgt().toLowerCase().equals(orgtValue.toLowerCase())) {
                vms.add(tempVMList.get(j));
            }
        }
        return vms;
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
