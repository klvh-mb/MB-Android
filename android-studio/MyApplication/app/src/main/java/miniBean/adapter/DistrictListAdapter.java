package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.LocationVM;

public class DistrictListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<LocationVM> locationVMList;
    private TextView distName;

    private int oldSelected = 0;
    private int selected = 0;

    public DistrictListAdapter(Activity activity, List<LocationVM> locationVMList) {
        this.activity = activity;
        this.locationVMList = locationVMList;
        for (int i=0; i<locationVMList.size(); i++) {
            LocationVM location = locationVMList.get(i);
            if (location.getId().equals(AppController.getUserLocation().getId())) {
                setSelectedItem(i);
            }
        }
    }

    public void setSelectedItem(int item) {
        oldSelected = selected;
        selected = item;
    }

    @Override
    public int getCount() {
        return locationVMList.size();
    }

    @Override
    public LocationVM getItem(int i) {
        LocationVM temp = locationVMList.get(i);
        return temp;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null)
            view = inflater.inflate(R.layout.schools_district_grid_item, null);

        RelativeLayout nameBtn = (RelativeLayout) view.findViewById(R.id.nameBtn);
        distName = (TextView) view.findViewById(R.id.distItemText);

        //Log.d(this.getClass().getSimpleName(), "i="+i+" selected="+selected);
        if (i == selected) {
            nameBtn.setBackgroundResource(R.drawable.rounded_corner_pn_grid_item);
            distName.setTextColor(view.getResources().getColor(R.color.white));
        } else if (i == oldSelected) {
            nameBtn.setBackgroundColor(view.getResources().getColor(R.color.white));
            distName.setTextColor(view.getResources().getColor(R.color.dark_gray_3));
        }

        final LocationVM item = locationVMList.get(i);
        distName.setText(item.getDisplayName());
        return view;
    }
}
