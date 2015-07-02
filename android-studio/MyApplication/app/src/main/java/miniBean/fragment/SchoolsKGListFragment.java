package miniBean.fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.KGListAdapter;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.KindergartenVM;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SchoolsKGListFragment extends AbstractSchoolsListFragment {

    private static final String TAG = SchoolsKGListFragment.class.getName();

    protected KGListAdapter listAdapter;
    protected List<KindergartenVM> schoolVMList;
    protected List<KindergartenVM> searchVMList;

    @Override
    protected boolean isPN() {
        return false;
    }

    @Override
    protected void init() {
        schoolVMList = new ArrayList<>();
    }

    @Override
    protected View getHeaderView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.schools_kg_list_fragment_header, null);
    }

    @Override
    protected void dismissSearchMode() {
        super.dismissSearchMode();
        searchVMList = new ArrayList<>();
        listAdapter.refresh(schoolVMList);
    }

    @Override
    protected void getSchoolsByDistrict(final Long id, final String district) {
        ViewUtil.showSpinner(getActivity());
        AppController.getApi().getKGsByDistricts(id, AppController.getInstance().getSessionId(), new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> vms, Response response) {
                Log.d(AbstractSchoolsListFragment.class.getSimpleName(), "[" + district + "] returned " + vms.size() + " schools");
                schoolVMList = vms;
                applyFilters();
                noOfSchools.setText(vms.size() + "");
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                error.printStackTrace();
            }
        });
    }

    @Override
    protected void applyFilters(){
        List<KindergartenVM> filteredVMList = new ArrayList<>();
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

        listAdapter = new KGListAdapter(getActivity(),filteredVMList);
        listView.setAdapter(listAdapter);
    }

    protected List<KindergartenVM> cpFilter(String cpValue,List<KindergartenVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(cpValue)) {
            return filteredVMList;
        }

        List<KindergartenVM> vm = new ArrayList<>();
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

    protected List<KindergartenVM> typeFilter(String orgtValue,List<KindergartenVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(orgtValue)) {
            return filteredVMList;
        }

        List<KindergartenVM> vm = new ArrayList<>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            if (orgtValue.equalsIgnoreCase(filteredVMList.get(i).getOrgt())) {
                vm.add(filteredVMList.get(i));
            }
        }
        return vm;
    }

    protected List<KindergartenVM> currFilter(String currValue,List<KindergartenVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(currValue)) {
            return filteredVMList;
        }

        List<KindergartenVM> vm = new ArrayList<>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            if (currValue.equalsIgnoreCase(filteredVMList.get(i).getCurt())) {
                vm.add(filteredVMList.get(i));
            }
        }
        return vm;
    }

    protected List<KindergartenVM> timeFilter(String ctValue,List<KindergartenVM> filteredVMList) {
        if(DefaultValues.FILTER_SCHOOLS_ALL.equals(ctValue)) {
            return filteredVMList;
        }

        List<KindergartenVM> vm = new ArrayList<>();
        for (int i = 0; i < filteredVMList.size(); i++) {
            String classTime = ViewUtil.translateClassTime(filteredVMList.get(i).getCt(), this.getResources());
            if (!StringUtils.isEmpty(classTime) &&
                    classTime.contains(ctValue)) {
                vm.add(filteredVMList.get(i));
            }
        }
        return vm;
    }

    @Override
    protected void searchByName(final String query) {
        ViewUtil.showSpinner(getActivity());
        dismissSearchPressCount = 0;
        AppController.getApi().searchKGsByName(query, AppController.getInstance().getSessionId(), new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> vms, Response response) {
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

                //KGListAdapter resultListAdapter = new KGListAdapter(getActivity(), resultList);
                //listView.setAdapter(resultListAdapter);
                listAdapter.refresh(searchVMList);

                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                error.printStackTrace();
            }
        });
    }
}