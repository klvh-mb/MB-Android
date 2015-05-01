package miniBean.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class AdaptiveGridView extends GridView {

    public AdaptiveGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptiveGridView(Context context) {
        super(context);
    }

    public AdaptiveGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}