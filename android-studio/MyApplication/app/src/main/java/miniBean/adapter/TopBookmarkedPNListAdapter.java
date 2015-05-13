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
import miniBean.viewmodel.PreNurseryVM;

public class TopBookmarkedPNListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<PreNurseryVM> items;
    private TextView pnName,distName,noOfViews,serialNoText;

    public TopBookmarkedPNListAdapter(Activity activity, List<PreNurseryVM> items) {
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
    public PreNurseryVM getItem(int location) {
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
            convertView = inflater.inflate(R.layout.top_bookmarked_schools_item, null);

        pnName= (TextView) convertView.findViewById(R.id.pnNameText);
        distName= (TextView) convertView.findViewById(R.id.pnDistName);
        noOfViews= (TextView) convertView.findViewById(R.id.viewText);
        serialNoText= (TextView) convertView.findViewById(R.id.serialNoText);

        PreNurseryVM item = items.get(position);

        pnName.setText(item.getN());
        distName.setText(item.getDis());
       noOfViews.setText(item.getNob()+"");
        serialNoText.setText(position+1+"");


        return convertView;
    }

}


