package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import miniBean.R;

public class PageListAdapter extends BaseAdapter {
    ArrayList<String> list;
    TextView username, message, page;
    ImageView userPhoto;
    Button acceptButton, ignoreButton;
    private Activity activity;

    private LayoutInflater inflater;

    public PageListAdapter(Activity activity, ArrayList<String> list) {
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
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.page_item, null);

        message = (TextView) convertView.findViewById(R.id.requestText);
        userPhoto = (ImageView) convertView.findViewById(R.id.userImage);
        acceptButton = (Button) convertView.findViewById(R.id.acceptButton);
        ignoreButton = (Button) convertView.findViewById(R.id.ignoreButton);
        page= (TextView) convertView.findViewById(R.id.pageText);

        String item = list.get(position);

        page.setText(item);
        return convertView;

    }

}