package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.List;

import miniBean.R;
import miniBean.util.ImageMapping;
import miniBean.viewmodel.KindergartenVM;

public class KGBookmarkListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<KindergartenVM> schoolVMList;
    private ImageView icon;
    private TextView noOfComment,title,district,appDateText;

    public KGBookmarkListAdapter(Activity activity, List<KindergartenVM> schoolVMList) {
        this.activity = activity;
        this.schoolVMList = schoolVMList;
    }

    @Override
    public int getCount() {
        return schoolVMList.size();
    }

    @Override
    public KindergartenVM getItem(int i) {
        return schoolVMList.get(i);
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
            view = inflater.inflate(R.layout.school_bookmark_item, null);

        icon = (ImageView) view.findViewById(R.id.schoolImage);
        title = (TextView) view.findViewById(R.id.schoolName);
        district = (TextView) view.findViewById(R.id.districtText);
        noOfComment = (TextView) view.findViewById(R.id.totalCommentText);
        appDateText = (TextView) view.findViewById(R.id.appDateText);

        KindergartenVM item = schoolVMList.get(i);

        int iconMapped = ImageMapping.map(item.getIcon());
        if (iconMapped != -1) {
            icon.setImageDrawable(view.getResources().getDrawable(iconMapped));
        } else {
            icon.setImageResource(R.drawable.schools_green_book);
        }

        title.setText(item.getN());
        district.setText(item.getDis());
        noOfComment.setText(item.getNop()+"");

        if (StringUtils.isEmpty(item.appDateTxt)) {
            appDateText.setVisibility(View.GONE);
        } else {
            appDateText.setText(activity.getString(R.string.schools_application_date_prefix)+" "+item.appDateTxt);
            appDateText.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
