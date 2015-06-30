package miniBean.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import miniBean.R;
import miniBean.util.ImageUtil;
import miniBean.util.SharedPreferencesUtil;
import miniBean.viewmodel.LocationVM;
import miniBean.viewmodel.MessageVM;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import org.acra.*;
import org.acra.annotation.*;

import java.security.MessageDigest;
import java.util.List;

/**
 * ARCA config
 * http://www.acra.ch/
 * http://stackoverflow.com/questions/16747673/android-application-cant-compile-find-acra-library-after-import
 */
/*
@ReportsCrashes(
        mailTo = "minibean.hk@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
        */
@ReportsCrashes(
        mailTo = "minibean.hk@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        customReportContent = {
                ReportField.BUILD, ReportField.USER_APP_START_DATE, ReportField.USER_CRASH_DATE,
                ReportField.USER_EMAIL, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE,
                ReportField.LOGCAT,
        },
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast,
        logcatFilterByPid = true)
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    public static String BASE_URL;

    private static AppController mInstance;
    private static MyApi api;

    private long conversationId;
    public List<MessageVM> messageVMList;

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static synchronized MyApi getApi() {
        if (api == null)
            init();
        return api;
    }

    public static synchronized boolean isUserAdmin() {
        return UserInfoCache.getUser().isAdmin();
    }

    public static synchronized LocationVM getUserLocation() {
        return UserInfoCache.getUser().getLocation();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        init();

        //printKeyHashForFacebook();
    }

    public static void init() {
        BASE_URL = getInstance().getString(R.string.base_url);

        if (api == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .setClient(new OkClient()).build();
            api = restAdapter.create(MyApi.class);
        }

        ImageUtil.init();

        initStaticCaches();

        //ACRA.init(getInstance());
    }

    public static void initStaticCaches() {
        DistrictCache.refresh();
        EmoticonCache.refresh();
    }

    /**
     * Exit app. Clear everything.
     */
    public void clearAll() {
        Log.d(this.getClass().getSimpleName(), "clearAll");
        LocalCommunityTabCache.clear();
        NotificationCache.clear();
        UserInfoCache.clear();
    }

    public void saveSessionId(String sessionId) {
        SharedPreferencesUtil.getInstance().saveString(SharedPreferencesUtil.SESSION_ID, sessionId);
    }

    public String getSessionId() {
        return SharedPreferencesUtil.getInstance().getString(SharedPreferencesUtil.SESSION_ID);
    }

    public void clearPreferences() {
        SharedPreferencesUtil.getInstance().clearAll();
    }

    public void exitApp() {
        Log.d(this.getClass().getSimpleName(), "exitApp");

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //i.putExtra("EXIT", true);
        //i.setClass(this, LoginActivity.class);
        startActivity(i);

        // exit
        android.os.Process.killProcess(android.os.Process.myPid());

        System.exit(1);
    }

    public static boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) AppController.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(AppController.getInstance().getApplicationContext(), AppController.getInstance().getString(R.string.connection_timeout_message), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void printKeyHashForFacebook() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("miniBean.app", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(this.getClass().getSimpleName(), "KeyHash - " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}