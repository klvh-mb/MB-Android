package miniBean.util;

import java.util.Arrays;
import java.util.List;

import miniBean.R;
import miniBean.app.AppController;

/**
 * Should read from server.
 */
public class DefaultValues {

    // From server
    public static final int DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD = 0;
    public static final int DEFAULT_INFINITE_SCROLL_DELAY = 500;
    public static final int DEFAULT_INFINITE_SCROLL_COUNT = 10;
    public static final int DEFAULT_PAGINATION_COUNT = 10;

    public static final int CONVERSATION_LAST_MESSAGE_COUNT = 50;
    public static final int CONVERSATION_MESSAGE_COUNT = 10;
    public static final int CONVERSATION_COUNT = 100;

    // UI
    public static final int MIN_CHAR_SIGNUP_PASSWORD = 4;

    public static final int SPLASH_DISPLAY_MILLIS = 1000;
    public static final int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 3000;
    public static final int DEFAULT_HANDLER_DELAY = 10;

    public static final int PULL_TO_REFRESH_DELAY = 1000;

    public static final int LISTVIEW_SLIDE_IN_ANIM_START = 10;
    public static final int LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR = 2;

    public static final int PAGINATION_POPUP_WIDTH = 150;
    public static final int PAGINATION_POPUP_HEIGHT = 300;

    public static final int MY_COMMUNITY_POPUP_WIDTH = 250;
    public static final int MY_COMMUNITY_POPUP_HEIGHT = 350;

    public static final int EMOTICON_POPUP_WIDTH = 300;
    public static final int EMOTICON_POPUP_HEIGHT = 100;

    public static final int MAX_POST_IMAGES = 3;
    public static final int MAX_COMMENT_IMAGES = 3;
    public static final int MAX_MESSAGE_IMAGES = 1;
    public static final int MAX_POST_IMAGE_DIMENSION = 100;

    public static final int IMAGE_CORNERS_ROUNDED_VALUE = 38;
    public static final int IMAGE_ROUND_ROUNDED_VALUE = 120;

    public static final int NEW_POST_DAYS_AGO = 3;
    public static final int NEW_POST_NOC = 3;
    public static final int HOT_POST_NOV = 200;
    public static final int HOT_POST_NOL = 5;
    public static final int HOT_POST_NOC = 5;

    public static final int DEFAULT_PARENT_BIRTH_YEAR = 9999;

    public static final List<String> FILTER_MY_COMM_TYPE = Arrays.asList(
            new String[] {"BUSINESS"}
    );
    public static final List<String> FILTER_MY_COMM_TARGETING_INFO = Arrays.asList(
            new String[] {"FEEDBACK"}
    );

    public static final String FILTER_SCHOOLS_ALL = AppController.getInstance().getString(R.string.filter_schools_all);
    public static final String FILTER_SCHOOLS_YES = AppController.getInstance().getString(R.string.filter_schools_yes);
    public static final String FILTER_SCHOOLS_NO = AppController.getInstance().getString(R.string.filter_schools_no);

    public static final List<String> FILTER_SCHOOLS_COUPON = Arrays.asList(
            new String[] {
                    FILTER_SCHOOLS_ALL,
                    FILTER_SCHOOLS_YES,
                    FILTER_SCHOOLS_NO
            }
    );

    public static final List<String> FILTER_SCHOOLS_TYPE = Arrays.asList(
            new String[] {
                    FILTER_SCHOOLS_ALL,
                    AppController.getInstance().getString(R.string.filter_schools_type_private),
                    AppController.getInstance().getString(R.string.filter_schools_type_public)
            }
    );

    public static final List<String> FILTER_SCHOOLS_CURRICULUM = Arrays.asList(
            new String[] {
                    FILTER_SCHOOLS_ALL,
                    AppController.getInstance().getString(R.string.filter_schools_curriculum_local),
                    AppController.getInstance().getString(R.string.filter_schools_curriculum_nonlocal)
            }
    );

    public static final List<String> FILTER_SCHOOLS_TIME = Arrays.asList(
            new String[] {
                    FILTER_SCHOOLS_ALL,
                    AppController.getInstance().getString(R.string.filter_schools_time_am),
                    AppController.getInstance().getString(R.string.filter_schools_time_pm),
                    AppController.getInstance().getString(R.string.filter_schools_time_wd)
            }
    );

    /*
    public static final List<String> FILTER_SCHOOLS_TIME = Arrays.asList(
            new String[] {
                    FILTER_SCHOOLS_ALL,
                    "AM",
                    "PM",
                    "WD"
            }
    );
    */

    public static final int MAX_SCHOOLS_SEARCH_COUNT = 100;
}
