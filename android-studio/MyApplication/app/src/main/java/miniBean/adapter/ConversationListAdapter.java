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
import miniBean.util.DateTimeUtil;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.ConversationVM;

public class ConversationListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ConversationVM> conversationVMs;
    private ImageView userPicture;
    private TextView senderText,firstMessageText,dateText;

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversationVMs) {
        this.activity = activity;
        this.conversationVMs = conversationVMs;
    }

    @Override
    public int getCount() {
        return conversationVMs.size();
    }

    @Override
    public ConversationVM getItem(int i) {
        ConversationVM temp = conversationVMs.get(i);
        return temp;
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

        senderText= (TextView) view.findViewById(R.id.senderText);
        dateText= (TextView) view.findViewById(R.id.dateText);
        firstMessageText= (TextView) view.findViewById(R.id.firstMessageText);
        userPicture= (ImageView) view.findViewById(R.id.userPicture);

        ConversationVM item=conversationVMs.get(i);

        ImageUtil.displayThumbnailProfileImage(item.getId(), userPicture);

        senderText.setText(item.getNm());
        dateText.setText(DateTimeUtil.getTimeAgo(item.getLmd()));
        firstMessageText.setText(item.getLm());

        return  view;
    }

}
