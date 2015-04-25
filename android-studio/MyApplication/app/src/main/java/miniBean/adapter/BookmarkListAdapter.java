package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.viewmodel.PreNurseryVM;

public class BookmarkListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<PreNurseryVM> PNlistItem;
    private ImageView icon;
    private TextView noOfComment,title,district;

    public BookmarkListAdapter(Activity activity, List<PreNurseryVM> PNlistItem) {
        this.activity = activity;
        this.PNlistItem = PNlistItem;
    }

    @Override
    public int getCount() {
        return PNlistItem.size();
    }

    @Override
    public PreNurseryVM getItem(int i) {
        return PNlistItem.get(i);
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
            view = inflater.inflate(R.layout.school_bookmark_item, null);


        icon= (ImageView) view.findViewById(R.id.bookmarkPic);
        title= (TextView) view.findViewById(R.id.bookmarkName);
        district= (TextView) view.findViewById(R.id.textDistrict);
        noOfComment= (TextView) view.findViewById(R.id.totalCommentText);
        System.out.println("in book adapter:::::::::");

        PreNurseryVM item=PNlistItem.get(i);

        icon.setImageResource(R.drawable.schools_green_book);

        title.setText(item.getN());
        district.setText(item.getDis());
        noOfComment.setText(""+item.getNop());

        return view;
    }
}
