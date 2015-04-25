package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.activity.PNCommunityActivity;
import miniBean.viewmodel.KindergartenVM;

public class KindyListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<KindergartenVM> kindergartenVMList;
    private TextView pnName,pnAddress,commentNoText,curriculumValue,typeValue,timeValue,distName;
    private ImageView couponValue,bookmarkImage;

    public KindyListAdapter(Activity activity, List<KindergartenVM> kindergartenVMList) {
        this.activity = activity;
        this.kindergartenVMList = kindergartenVMList;
    }

    @Override
    public int getCount() {
        if (kindergartenVMList == null)
            return 0;
        return kindergartenVMList.size();
    }

    @Override
    public KindergartenVM getItem(int location) {
        if (kindergartenVMList == null || location > kindergartenVMList.size()-1)
            return null;
        return kindergartenVMList.get(location);
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

         pnName= (TextView) convertView.findViewById(R.id.nameText);
         pnAddress= (TextView) convertView.findViewById(R.id.pnAddressText);
        commentNoText= (TextView) convertView.findViewById(R.id.totalCommentText);
        couponValue= (ImageView) convertView.findViewById(R.id.couponValue);
        curriculumValue= (TextView) convertView.findViewById(R.id.curriculumValue);
        typeValue= (TextView) convertView.findViewById(R.id.typeValue);
        timeValue= (TextView) convertView.findViewById(R.id.timeValue);
        distName= (TextView) convertView.findViewById(R.id.pnDistName);
        bookmarkImage= (ImageView) convertView.findViewById(R.id.bookmarkImage);

        final KindergartenVM item=kindergartenVMList.get(position);


        pnName.setText(item.getN());
        pnAddress.setText(item.getNe());
        commentNoText.setText(item.getNop()+"");



        curriculumValue.setText(item.getCurt());
        timeValue.setText(item.getCt());
        typeValue.setText(item.getOrgt());
        distName.setText(item.getDis());



        if(item.isBookmarked()){
            bookmarkImage.setImageResource(R.drawable.ic_bookmarked);
        }else{
            bookmarkImage.setImageResource(R.drawable.ic_bookmark);
        }


        if(item.isCp()){
            couponValue.setImageResource(R.drawable.value_yes);

        }else {
            couponValue.setImageResource(R.drawable.value_no);
        }

        pnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(activity, PNCommunityActivity.class);

                intent.putExtra("commid",item.getCommId());
                intent.putExtra("id",item.getId());

                activity.startActivity(intent);

            }
        });

        pnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, PNCommunityActivity.class);

                intent.putExtra("id",item.getId());
                intent.putExtra("id",item.getId());

                activity.startActivity(intent);
            }
        });
        return convertView;
    }

}


