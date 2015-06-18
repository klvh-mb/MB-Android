package miniBean.util;

import android.content.res.Resources;

import miniBean.R;
import miniBean.viewmodel.CommunityPostVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class ViewUtil {

    private ViewUtil() {}

    //
    // Schools
    //

    public static String translateClassTime(String classTime, Resources resources) {
        return classTime.replace("AM", resources.getString(R.string.filter_schools_time_am)).
                replace("PM", resources.getString(R.string.filter_schools_time_pm)).
                replace("WD", resources.getString(R.string.filter_schools_time_wd));
    }

    //
    // Post
    //

    public static boolean isNewPost(CommunityPostVM post) {
        return post.getN_c() <= DefaultValues.NEW_POST_NOC &&
                DateTimeUtil.getDaysAgo(post.t) <= DefaultValues.NEW_POST_DAYS_AGO;
    }

    public static boolean isHotPost(CommunityPostVM post) {
        return post.getN_c() > DefaultValues.HOT_POST_NOC ||
                post.getNol() > DefaultValues.HOT_POST_NOL ||
                post.getNov() > DefaultValues.HOT_POST_NOV;
    }

    public static boolean isImagePost(CommunityPostVM post) {
        return post.isHasImage();
    }
}
