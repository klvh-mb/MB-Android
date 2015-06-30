package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.activity.GameRulesActivity;
import miniBean.activity.UserProfileActivity;
import miniBean.util.DateTimeUtil;
import miniBean.viewmodel.GameTransactionVM;

public class GameTransactionListAdapter extends BaseAdapter {

    public static final String TRANSACTION_TYPE_SYSTEM_CREDIT = "SystemCredit";
    public static final String TRANSACTION_TYPE_REDEEM = "Redeem";
    public static final String TRANSACTION_TYPE_BONUS = "Bonus";
    public static final String TRANSACTION_TYPE_PENALTY = "Penalty";
    public static final String TRANSACTION_TYPE_ADJUSTMENT = "Adjustment";

    private Activity activity;
    private LayoutInflater inflater;
    private List<GameTransactionVM> items;
    private TextView gameTransactionText;
    private boolean showDetails = false;

    public GameTransactionListAdapter(Activity activity, List<GameTransactionVM> items) {
        this(activity, items, false);
    }

    public GameTransactionListAdapter(Activity activity, List<GameTransactionVM> items, boolean showDetails) {
        this.activity = activity;
        this.items = items;
        this.showDetails = showDetails;
    }

    @Override
    public int getCount() {
        if (items == null)
            return 0;
        return items.size();
    }

    @Override
    public GameTransactionVM getItem(int location) {
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
            convertView = inflater.inflate(R.layout.game_transaction_item, null);

        gameTransactionText = (TextView) convertView.findViewById(R.id.gameTransactionText);

        final GameTransactionVM item = items.get(position);

        String str;
        if (!TRANSACTION_TYPE_REDEEM.equalsIgnoreCase(item.getTy()) &&
                !TRANSACTION_TYPE_PENALTY.equalsIgnoreCase(item.getTy())) {
            str = "%s %s獲得%d小豆豆";
        } else {
            str = "%s %s扣除%d小豆豆";
        }

        if (showDetails) {
            str += "\n[u=%d][points=%d]";
            str = String.format(str, DateTimeUtil.format(item.getTt(),true), item.getTd(), item.getTp(), item.getUid(), item.getNtp());
            gameTransactionText.setTextColor(activity.getResources().getColor(R.color.admin_green));
            gameTransactionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UserProfileActivity.class);
                    intent.putExtra("oid", item.getUid());
                    activity.startActivity(intent);
                }
            });
        } else {
            str = String.format(str, DateTimeUtil.format(item.getTt(), true), item.getTd(), item.getTp());
        }

        gameTransactionText.setText(str);

        return convertView;
    }
}