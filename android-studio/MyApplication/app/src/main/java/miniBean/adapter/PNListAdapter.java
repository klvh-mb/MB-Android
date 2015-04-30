package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.List;

import miniBean.R;
import miniBean.activity.PNCommunityActivity;
import miniBean.app.AppController;
import miniBean.viewmodel.PreNurseryVM;

public class PNListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<PreNurseryVM> PNlistItem;
    private TextView pnName,enName,commentNoText,curriculumValue,typeValue,timeValue,distName;
    private ImageView couponValue,bookmarkImage,commentImage;
    private RelativeLayout schoolMainLayout;

    public PNListAdapter(Activity activity, List<PreNurseryVM> PNlistItem) {
        this.activity = activity;
        this.PNlistItem = PNlistItem;
    }

    @Override
    public int getCount() {
        if (PNlistItem == null)
            return 0;
        return PNlistItem.size();
    }

    @Override
    public PreNurseryVM getItem(int location) {
        if (PNlistItem == null || location > PNlistItem.size()-1)
            return null;
        return PNlistItem.get(location);
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
            convertView = inflater.inflate(R.layout.pn_list_item, null);

        schoolMainLayout = (RelativeLayout) convertView.findViewById(R.id.schoolMainLayout);
        pnName = (TextView) convertView.findViewById(R.id.nameText);
        enName = (TextView) convertView.findViewById(R.id.enNameText);
        commentNoText = (TextView) convertView.findViewById(R.id.totalCommentText);
        couponValue = (ImageView) convertView.findViewById(R.id.couponImage);
        curriculumValue = (TextView) convertView.findViewById(R.id.curriculumValue);
        typeValue = (TextView) convertView.findViewById(R.id.typeValue);
        timeValue = (TextView) convertView.findViewById(R.id.timeValue);
        distName = (TextView) convertView.findViewById(R.id.pnDistName);
        bookmarkImage = (ImageView) convertView.findViewById(R.id.bookmarkImage);
        commentImage= (ImageView) convertView.findViewById(R.id.commentImage);
        final PreNurseryVM item = PNlistItem.get(position);

        pnName.setText(item.getN());
        if (StringUtils.isEmpty(item.getNe())) {
            enName.setVisibility(View.GONE);
        } else {
            enName.setText(item.getNe());
            enName.setVisibility(View.VISIBLE);
        }
        commentNoText.setText(item.getNop()+"");

        curriculumValue.setText(item.getCurt());
        timeValue.setText(item.getCt());
        typeValue.setText(item.getOrgt());
        distName.setText(item.getDis());

        if(item.isBookmarked()){
            bookmarkImage.setImageResource(R.drawable.ic_bookmarked);
        } else {
            bookmarkImage.setImageResource(R.drawable.ic_bookmark_white);
        }

        if(item.isCp()){
            couponValue.setImageResource(R.drawable.value_yes);

        } else {
            couponValue.setImageResource(R.drawable.value_no);
        }

        // num views
        LinearLayout numViewsLayout = (LinearLayout) convertView.findViewById(R.id.numViewsLayout);
        if (AppController.isUserAdmin()) {
            TextView numViews = (TextView) convertView.findViewById(R.id.numViews);
            numViews.setText(item.getNov()+"");
            numViewsLayout.setVisibility(View.VISIBLE);
        } else {
            numViewsLayout.setVisibility(View.GONE);
        }

        schoolMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PNCommunityActivity.class);
                intent.putExtra("commId",item.getCommId());
                intent.putExtra("id",item.getId());
                intent.putExtra("flag","FromSchoolMainlayout");
                activity.startActivity(intent);
            }
        });

        commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PNCommunityActivity.class);
                intent.putExtra("commId",item.getCommId());
                intent.putExtra("id",item.getId());
                intent.putExtra("flag","FromCommentImage");
                activity.startActivity(intent);
            }
        });

        return convertView;
    }
}


