package miniBean.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by keithlei on 3/9/15.
 */
public class DefaultValues {

    public static int DEFAULT_CONNECTION_TIMEOUT = 2;

    public static int DEFAULT_INFINITE_SCROLL_COUNT = 10;
    public static int DEFAULT_PAGINATION_COUNT = 10;

    public static List<String> FILTER_MY_COMM_TYPE = Arrays.asList(
            new String[] {"BUSINESS"}
    );
    public static List<String> FILTER_MY_COMM_TARGETING_INFO = Arrays.asList(
            new String[] {"FEEDBACK"}
    );

}
