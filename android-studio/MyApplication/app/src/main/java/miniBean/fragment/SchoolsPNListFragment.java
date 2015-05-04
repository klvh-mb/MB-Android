package miniBean.fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.PNListAdapter;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SchoolsPNListFragment extends AbstractSchoolsListFragment {

    private static final String TAG = SchoolsPNListFragment.class.getName();

    protected PNListAdapter listAdapter;
    protected List<PreNurseryVM> schoolVMList;
    protected List<PreNurseryVM> searchVMList;

    @Override
    protected boolean isPN() {
        return true;
    }

    @Override
    protected void init() {
        schoolVMList = new ArrayList<>();
    }

    @Override
    protected View getHeaderView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.schools_pn_list_fragment_header, null);
    }

    @Override
    protected void dismissSearchMode() {
        super.dismissSearchMode();
        searchVMList = new ArrayList<>();
        listAdapter.refresh(schoolVMList);
    }

    @Override
    protected void getSchoolsByDistrict(final Long id, final String district){
        AppController.getApi().getPNsByDistricts(id, AppController.getInstance().getSessionId(), new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> vms, Response response) {
                Log.d(AbstractSchoolsListFragment.class.getSimpleName(), "[" + district + "] returned " + vms.size() + " schools");
                schoolVMList = vms;
                applyFilters();
                noOfSchools.setText(vms.size() + "");
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    protected void applyFilters(){
        List<PreNurseryVM> filteredVMList = new ArrayList<>();
        filteredVMList.addAll(schoolVMList);
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

        listAdapter = new PNListAdapter(getActivity(),filteredVMList);
        listView.setAdapter(listAdapter);
    }

    protected List<PreNurseryVM> cpFilter(String cpValue,List<PreNurseryVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(cpValue)) {
            return filteredVMList;
        }

        List<PreNurseryVM> vm = new ArrayList<>();
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

    protected List<PreNurseryVM> typeFilter(String orgtValue,List<PreNurseryVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(orgtValue)) {
            return filteredVMList;
        }

        List<PreNurseryVM> vm = new ArrayList<>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            if (orgtValue.equalsIgnoreCase(filteredVMList.get(i).getOrgt())) {
                vm.add(filteredVMList.get(i));
            }
        }
        return vm;
    }

    protected List<PreNurseryVM> currFilter(String currValue,List<PreNurseryVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(currValue)) {
            return filteredVMList;
        }

        List<PreNurseryVM> vm = new ArrayList<>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            if (currValue.equalsIgnoreCase(filteredVMList.get(i).getCurt())) {
                vm.add(filteredVMList.get(i));
            }
        }
        return vm;
    }

    protected List<PreNurseryVM> timeFilter(String ctValue,List<PreNurseryVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(ctValue)) {
            return filteredVMList;
        }

        List<PreNurseryVM> vm = new ArrayList<>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            if (!StringUtils.isEmpty(filteredVMList.get(i).getCt()) &&
                    filteredVMList.get(i).getCt().contains(ctValue)) {
                vm.add(filteredVMList.get(i));
            }
        }
        return vm;
    }

    @Override
    protected void searchByName(final String query){
        dismissSearchPressCount = 0;
        AppController.getApi().searchPNsByName(query, AppController.getInstance().getSessionId(), new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> vms, Response response) {
                Log.d(AbstractSchoolsListFragment.class.getSimpleName(), "searchByName: returns " + vms.size() + " schools");
                searchKey.setText("[ " + query + " ]");
                totalResultText.setText("" + vms.size());

                // too many results
                searchVMList = new ArrayList<>();
                if (vms.size() > DefaultValues.MAX_SCHOOLS_SEARCH_COUNT) {
                    tooManyResultsText.setVisibility(View.VISIBLE);
                } else {
                    tooManyResultsText.setVisibility(View.GONE);
                    searchVMList = vms;
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