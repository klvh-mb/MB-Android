package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.EmoticonVM;

public class EmoListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ImageView imageView;
    List<EmoticonVM> emoticonVMList;
    ImageLoader mImageLoader;
    public EmoListAdapter(Activity activity, List<EmoticonVM> emoticonVMList) {
        this.activity = activity;
        this.emoticonVMList = emoticonVMList;
    }

    @Override
    public int getCount() {
        return emoticonVMList.size();
    }

    @Override
    public Object getItem(int i) {
        EmoticonVM temp = emoticonVMList.get(i);
        return temp;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.emo_grid_item, null);

        imageView= (ImageView) view.findViewById(R.id.emoImage);
        mImageLoader=ImageLoader.getInstance();
        mImageLoader.displayImage(AppController.BASE_URL + emoticonVMList.get(i).getUrl(), imageView);

        return view;
    }
}
