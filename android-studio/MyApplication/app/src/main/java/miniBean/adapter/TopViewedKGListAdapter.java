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
import miniBean.viewmodel.KindergartenVM;

public class TopViewedKGListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<KindergartenVM> items;
    private TextView schoolName,district,noOfViews,serialNoText;

    public TopViewedKGListAdapter(Activity activity, List<KindergartenVM> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        if (items == null)
            return 0;
        return items.size();
    }

    @Override
    public KindergartenVM getItem(int location) {
        if (items == null || location > items.size()-1)
            return null;
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.top_viewed_schools_item, null);

        schoolName = (TextView) convertView.findViewById(R.id.schoolNameText);
        district = (TextView) convertView.findViewById(R.id.districtText);
        noOfViews = (TextView) convertView.findViewById(R.id.viewText);
        serialNoText = (TextView) convertView.findViewById(R.id.serialNoText);

        KindergartenVM item = items.get(position);

        schoolName.setText(item.getN());
        district.setText(item.getDis());
        noOfViews.setText(item.getNov()+"");
        serialNoText.setText(position+1+"");

        return convertView;
    }
}