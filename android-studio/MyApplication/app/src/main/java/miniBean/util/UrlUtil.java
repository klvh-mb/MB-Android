package miniBean.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import miniBean.app.AppController;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.GameAccountVM;
import miniBean.viewmodel.GameGiftVM;
import miniBean.viewmodel.KindergartenVM;
import miniBean.viewmodel.PreNurseryVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class UrlUtil {

    private static final String COMMUNITY_URL = AppController.BASE_URL + "/#!/community/%d";
    private static final String QNA_LANDING_URL = AppController.BASE_URL + "/#!/qna-landing/id/%d/communityId/%d";
    private static final String SCHOOL_PN_URL = AppController.BASE_URL + "/schools#!/pn/%d";
    private static final String SCHOOL_KG_URL = AppController.BASE_URL + "/schools#!/kg/%d";
    //private static final String ANDROID_APP_DOWNLOAD_URL = AppController.BASE_URL + "/#!/apps/android";
    private static final String ANDROID_APP_DOWNLOAD_URL = "https://goo.gl/gdHjty";
    private static final String REFERRAL_URL = AppController.BASE_URL + "/signup-code/%s";
    private static final String GAME_GIFT_URL = AppController.BASE_URL + "/#!/game-gift/%d";

    private static String QNA_LANDING_URL_REGEX = ".*/qna-landing/id/(\\d+)/communityId/(\\d+)";
    private static String COMMUNITY_URL_REGEX = ".*/community/(\\d+)";

    public static String createReferralUrl(GameAccountVM gameAccount) {
        return String.format(REFERRAL_URL, gameAccount.getPmcde());
    }

    public static String createGameGiftUrl(GameGiftVM gameGift) {
        return String.format(GAME_GIFT_URL, gameGift.getId());
    }

    public static String createCommunityUrl(CommunitiesWidgetChildVM community) {
        return String.format(COMMUNITY_URL, community.getId());
    }

    public static String createPostLandingUrl(CommunityPostVM post) {
        return String.format(QNA_LANDING_URL, post.getId(), post.getCid());
    }

    public static String createSchoolUrl(PreNurseryVM school) {
        return String.format(SCHOOL_PN_URL, school.getId());
    }

    public static String createSchoolUrl(KindergartenVM school) {
        return String.format(SCHOOL_KG_URL, school.getId());
    }

    public static String createAndroidAppDownloadUrl() {
        return ANDROID_APP_DOWNLOAD_URL;
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
