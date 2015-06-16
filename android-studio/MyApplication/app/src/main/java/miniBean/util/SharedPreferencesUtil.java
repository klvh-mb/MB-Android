package miniBean.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import miniBean.app.AppController;
import miniBean.viewmodel.EmoticonVM;
import miniBean.viewmodel.LocationVM;
import miniBean.viewmodel.UserVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class SharedPreferencesUtil {
    public static final String TAG = SharedPreferencesUtil.class.getSimpleName();
    public static final String PREFS = "prefs";

    public static final String FB_ACCESS_TOKEN = "access_token";
    public static final String FB_ACCESS_EXPIRES = "access_expires";
    public static final String SESSION_ID = "sessionID";
    public static final String USER_INFO = "userInfo";
    public static final String DISTRICTS = "districts";
    public static final String EMOTICONS = "emoticons";

    public static enum Screen {
        COMMS_TAB,
        SCHOOLS_TAB,
        PROFILE_TAB,
        MY_NEWSFEED_TIPS
    }

    private static SharedPreferencesUtil instance = null;
    private SharedPreferences prefs;

    private SharedPreferencesUtil() {
        this.prefs = AppController.getInstance().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance() {
        if(instance == null)
            instance = new SharedPreferencesUtil();
        return instance;
    }

    //
    // Save
    //

    public void setScreenViewed(Screen screen) {
        saveBoolean(screen.name(), true);
    }

    public void saveUserInfo(UserVM userInfo) {
        this.saveObject(USER_INFO, userInfo);
    }

    public void saveDistricts(List<LocationVM> districts) {
        this.saveObject(DISTRICTS, districts);
    }

    public void saveEmoticons(List<EmoticonVM> emoticons) {
        this.saveObject(EMOTICONS, emoticons);
    }

    public void saveString(String key, String value) {
        this.prefs.edit().putString(key, value).commit();
    }

    public void saveLong(String key, Long value) {
        this.prefs.edit().putLong(key, value).commit();
    }

    public void saveBoolean(String key, Boolean value) {
        this.prefs.edit().putBoolean(key, value).commit();
    }

    private void saveObject(String key, Object obj) {
        String json = new Gson().toJson(obj);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] saveObject: key="+json);
        this.prefs.edit().putString(key, json).commit();
    }

    //
    // Get
    //

    public Boolean isScreenViewed(Screen screen) {
        return getBoolean(screen.name());
    }

    public UserVM getUserInfo() {
        String json = this.prefs.getString(USER_INFO, null);
        UserVM userInfo = new Gson().fromJson(json, UserVM.class);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] getUserInfo: json="+json);
        return userInfo;
    }

    public List<LocationVM> getDistricts() {
        Type type = new TypeToken<List<LocationVM>>() {}.getType();
        String json = this.prefs.getString(DISTRICTS, null);
        List<LocationVM> districts = new Gson().fromJson(json, type);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] getDistricts: size="+districts.size());
        return districts;
    }

    public List<EmoticonVM> getEmoticons() {
        Type type = new TypeToken<List<EmoticonVM>>() {}.getType();
        String json = this.prefs.getString(EMOTICONS, null);
        List<EmoticonVM> emoticons = new Gson().fromJson(json, type);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] getEmoticons: size="+emoticons.size());
        return emoticons;
    }

    public String getString(String key) {
        return this.prefs.getString(key, null);
    }

    public Long getLong(String key) {
        return this.prefs.getLong(key, 0L);
    }

    public Boolean getBoolean(String key) {
        return this.prefs.getBoolean(key, false);
    }

    public void clear(String key) {
        this.prefs.edit().remove(key).commit();
    }

    public void clearAll() {
        this.prefs.edit().clear().commit();
    }
}
