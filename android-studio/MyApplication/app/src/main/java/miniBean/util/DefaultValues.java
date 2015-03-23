package miniBean.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by keithlei on 3/9/15.
 */
public class DefaultValues {

    public static int SPLASH_DISPLAY_MILLIS = 1000;
    public static int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 3000;

    public static int DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD = 0;
    public static int DEFAULT_INFINITE_SCROLL_DELAY = 500;
    public static int DEFAULT_INFINITE_SCROLL_COUNT = 10;
    public static int DEFAULT_PAGINATION_COUNT = 10;

    public static int LISTVIEW_SLIDE_IN_ANIM_START = 10;
    public static int LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR = 2;

    public static int PAGINATION_POPUP_WIDTH = 150;
    public static int PAGINATION_POPUP_HEIGHT = 300;

    public static int COMMENT_POPUP_HEIGHT = 150;

    public static List<String> FILTER_MY_COMM_TYPE = Arrays.asList(
            new String[] {"BUSINESS"}
    );
    public static List<String> FILTER_MY_COMM_TARGETING_INFO = Arrays.asList(
            new String[] {"FEEDBACK"}
    );

}
