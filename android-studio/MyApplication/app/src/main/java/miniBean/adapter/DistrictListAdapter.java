package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.viewmodel.LocationVM;

public class DistrictListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<LocationVM> locationVMList;
    private TextView distName;

    public DistrictListAdapter(Activity activity, List<LocationVM> locationVMList) {
        this.activity = activity;
        this.locationVMList = locationVMList;
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
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null)
            view = inflater.inflate(R.layout.district_list_item, null);


        distName= (TextView) view.findViewById(R.id.distItemText);

        final LocationVM item=locationVMList.get(i);

        distName.setText(item.getDisplayName());


        return view;
    }
}
