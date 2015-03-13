package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.NotificationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PageListAdapter extends BaseAdapter {
    public SharedPreferences session;
    ArrayList<String> List;
    TextView username, message, page;
    ImageView userPhoto;
    Button acceptButton, ignoreButton;
    private Activity activity;

    private LayoutInflater inflater;


    public PageListAdapter(Activity activity, ArrayList<String> List) {
        this.activity = activity;
        this.List=List;
    }


    @Override
    public int getCount() {
        return List.size();
    }

    @Override
    public String getItem(int location) {
        return List.get(location);
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
        session = this.activity.getSharedPreferences("prefs", 0);
        page= (TextView) convertView.findViewById(R.id.pageText);

        String item=List.get(position);

        page.setText(item);
        return convertView;

    }

}