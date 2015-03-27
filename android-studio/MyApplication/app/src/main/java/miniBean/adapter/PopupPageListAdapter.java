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

public class PopupPageListAdapter extends BaseAdapter {
    private List<String> list;
    private TextView page;
    private Activity activity;

    private LayoutInflater inflater;

    public PopupPageListAdapter(Activity activity, List<String> list) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public String getItem(int location) {
        if (list == null || location > list.size()-1)
            return null;
        return list.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.pagination_popup_item, null);

        String item = list.get(position);

        page = (TextView) convertView.findViewById(R.id.pageText);
        page.setText(item);

        return convertView;
    }
}