package miniBean.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import miniBean.R;
import miniBean.app.AppController;

/**
 * Created by keithlei on 3/16/15.
 */
public class AnimationUtil {

    public static final Animation rotate = AnimationUtils.loadAnimation(AppController.getInstance(), R.anim.image_rotate);
    public static final Animation rotateBackForth = AnimationUtils.loadAnimation(AppController.getInstance(), R.anim.image_rotate_back_forth);
    public static final Animation rotateBackForthOnce = AnimationUtils.loadAnimation(AppController.getInstance(), R.anim.image_rotate_back_forth_once);

    private AnimationUtil() {}

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }

    public static void rotate(View view) {
        show(view);
        view.startAnimation(rotate);
    }

    public static void rotateBackForth(View view) {
        rotateBackForth(view, false);
    }

    public static void rotateBackForthOnce(View view) {
        rotateBackForth(view, true);
    }

    private static void rotateBackForth(View view, boolean once) {
        show(view);
        if (once)
            view.startAnimation(rotateBackForthOnce);
        else
            view.startAnimation(rotateBackForth);
    }

    public static void cancel(View view) {
        if (view.getAnimation() != null) {
            view.getAnimation().cancel();
            view.clearAnimation();
        }
        view.setVisibility(View.GONE);
    }
}
