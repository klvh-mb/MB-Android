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
import miniBean.util.CommunityIconUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PNListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<PreNurseryVM> items;
    private TextView schoolName,enName,commentNoText,curriculumValue,typeValue,timeValue,distName;
    private ImageView icon,couponImage,pnImage,bookmarkImage;
    private RelativeLayout schoolMainLayout;
    private LinearLayout infoRow3Layout,commentLayout;

    public PNListAdapter(Activity activity, List<PreNurseryVM> items) {
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
            convertView = inflater.inflate(R.layout.school_list_item, null);

        icon = (ImageView) convertView.findViewById(R.id.schoolImage);
        schoolMainLayout = (RelativeLayout) convertView.findViewById(R.id.schoolMainLayout);
        schoolName = (TextView) convertView.findViewById(R.id.nameText);
        enName = (TextView) convertView.findViewById(R.id.enNameText);
        commentNoText = (TextView) convertView.findViewById(R.id.totalCommentText);
        couponImage = (ImageView) convertView.findViewById(R.id.couponImage);
        curriculumValue = (TextView) convertView.findViewById(R.id.curriculumValue);
        typeValue = (TextView) convertView.findViewById(R.id.typeValue);
        timeValue = (TextView) convertView.findViewById(R.id.timeValue);
        distName = (TextView) convertView.findViewById(R.id.distName);
        bookmarkImage = (ImageView) convertView.findViewById(R.id.bookmarkImage);
        commentLayout = (LinearLayout) convertView.findViewById(R.id.commentLayout);
        pnImage = (ImageView) convertView.findViewById(R.id.pnImage);

        infoRow3Layout = (LinearLayout) convertView.findViewById(R.id.infoRow3Layout);
        infoRow3Layout.setVisibility(View.GONE);

        final PreNurseryVM item = items.get(position);

        int iconMapped = CommunityIconUtil.map(item.getIcon());
        if (iconMapped != -1) {
            icon.setImageDrawable(convertView.getResources().getDrawable(iconMapped));
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }

        schoolName.setText(item.getN());
        if (StringUtils.isEmpty(item.getNe())) {
            enName.setVisibility(View.GONE);
        } else {
            enName.setText(item.getNe());
            enName.setVisibility(View.VISIBLE);
        }
        commentNoText.setText(item.getNop()+"");

        curriculumValue.setText(item.getCurt());
        timeValue.setText(ViewUtil.translateClassTime(item.getCt(), this.activity.getResources()));
        typeValue.setText(item.getOrgt());
        distName.setText(item.getDis());

        if(item.isBookmarked()){
            bookmarkImage.setImageResource(R.drawable.ic_bookmarked);
        } else {
            bookmarkImage.setImageResource(R.drawable.ic_bookmark_white);
        }

        if(item.isCp()){
            couponImage.setImageResource(R.drawable.value_yes);
        } else {
            couponImage.setImageResource(R.drawable.value_no);
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
                activity.startActivity(intent);
            }
        });

        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PNCommunityActivity.class);
                intent.putExtra("commId",item.getCommId());
                intent.putExtra("id",item.getId());
                intent.putExtra("flag","FromCommentImage");
                activity.startActivity(intent);
            }
        });

        bookmarkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView image = (ImageView)view;
                PreNurseryVM vm = (PreNurseryVM)image.getTag();

                if (!vm.isBookmarked) {
                    bookmark(vm.getId());
                    image.setImageResource(R.drawable.ic_bookmarked);
                    vm.setBookmarked(true);
                } else {
                    unbookmark(vm.getId());
                    image.setImageResource(R.drawable.ic_bookmark_white);
                    vm.setBookmarked(false);
                }
            }
        });

        convertView.setTag(items);
        bookmarkImage.setTag(item);

        return convertView;
    }

    public void refresh(List<PreNurseryVM> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private void bookmark(Long id) {
        AppController.getApi().bookmarkPN(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {}
            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void unbookmark(Long id) {
        AppController.getApi().unbookmarkPN(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {}

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}


