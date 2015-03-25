package miniBean.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import miniBean.R;

/**
 * This works around the issue described here: http://stackoverflow.com/a/12675430/265521
 *
 * Created by keithlei on 3/25/15.
 */
public class FullScreenImageView extends ImageView {

    public FullScreenImageView(Context context) {
        super(context);
    }

    public FullScreenImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * (getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth());
        Log.d(this.getClass().getSimpleName(), "onMeasure: intrinsicWidth="+getDrawable().getIntrinsicWidth()+" width="+width);
        setMeasuredDimension(width, height);
    }
}