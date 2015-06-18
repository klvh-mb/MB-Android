package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.app.MyImageGetter;
import miniBean.util.DateTimeUtil;
import miniBean.util.ImageUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.ConversationVM;

public class ConversationListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ConversationVM> conversationVMs;
    private ImageView userPicture;
    private TextView senderText,firstMessageText,dateText;
    private MyImageGetter imageGetter;

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversationVMs) {
        this.activity = activity;
        this.conversationVMs = conversationVMs;
        this.imageGetter = new MyImageGetter(activity);
    }

    @Override
    public int getCount() {
        return conversationVMs.size();
    }

    @Override
    public ConversationVM getItem(int i) {
        return conversationVMs.get(i);
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
            view = inflater.inflate(R.layout.conversation_list_item, null);

        senderText = (TextView) view.findViewById(R.id.senderText);
        dateText = (TextView) view.findViewById(R.id.dateText);
        firstMessageText = (TextView) view.findViewById(R.id.firstMessageText);
        userPicture = (ImageView) view.findViewById(R.id.userPicture);

        ConversationVM item = conversationVMs.get(i);

        ImageUtil.displayMiniProfileImage(item.getUid(), userPicture);

        senderText.setText(item.getNm());
        dateText.setText(DateTimeUtil.getTimeAgo(item.getLmd()));
        ViewUtil.setHtmlText(item.getLm(), imageGetter, firstMessageText);

        Log.d(this.getClass().getSimpleName(), item.getLm());

        return  view;
    }

}
