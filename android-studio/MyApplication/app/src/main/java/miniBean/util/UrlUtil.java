package miniBean.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import miniBean.app.AppController;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.CommunityVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class UrlUtil {

    private static String QNA_LANDING_URL = AppController.BASE_URL + "/#!/qna-landing/id/%d/communityId/%d";
    private static String COMMUNITY_URL = AppController.BASE_URL + "/#!/qna-landing/id/%d/communityId/%d";
    private static String APP_DOWNLOAD_URL = AppController.BASE_URL + "/apps/android";

    private static String QNA_LANDING_URL_REGEX = ".*/qna-landing/id/(\\d+)/communityId/(\\d+)";
    private static String COMMUNITY_URL_REGEX = ".*/community/(\\d+)";

    public static String createCommunityUrl(CommunityVM community) {
        return String.format(COMMUNITY_URL, community.getId());
    }

    public static String createPostLandingUrl(CommunityPostVM post) {
        return String.format(QNA_LANDING_URL, post.getId(), post.getCid());
    }

    public static String createAndroidAppDownloadUrl() {
        return APP_DOWNLOAD_URL;
    }

    public static long parseCommunityUrlId(String url) {
        return parseUrlMatcher(COMMUNITY_URL_REGEX, url);
    }

    public static long parsePostLandingUrlId(String url) {
        return parseUrlMatcherAtPosition(QNA_LANDING_URL_REGEX, url, 1);
    }

    public static long parsePostLandingUrlCommId(String url) {
        return parseUrlMatcherAtPosition(QNA_LANDING_URL_REGEX, url, 2);
    }

    /**
     * Group always starts at 1. Group 0 is whole string.
     *
     * @param regex
     * @param url
     * @param pos
     * @return
     */
    private static long parseUrlMatcherAtPosition(String regex, String url, int pos) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(url);
        if (m.find()) {
            return Long.parseLong(m.group(pos));
        }
        return -1;
    }

    private static long parseUrlMatcher(String regex, String url) {
        return parseUrlMatcherAtPosition(regex, url, 1);
    }
}
