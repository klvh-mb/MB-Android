package miniBean.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
    private static SharedPreferencesUtil instance = null;
    private SharedPreferences prefs;

    public static final String FB_ACCESS_TOKEN = "access_token";
    public static final String FB_ACCESS_EXPIRES = "access_expires";
    public static final String SESSION_ID = "sessionID";
    public static final String USER_INFO = "userInfo";
    public static final String DISTRICTS = "districts";
    public static final String EMOTICONS = "emoticons";

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

    public void saveString(String key, String value) {
        this.prefs.edit().putString(key, value).commit();
    }

    public void saveLong(String key, Long value) {
        this.prefs.edit().putLong(key, value).commit();
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

    private void saveObject(String key, Object obj) {
        String json = new Gson().toJson(obj);
        Log.d(this.getClass().getSimpleName(), "=====> saveObject: key="+json);
        this.prefs.edit().putString(key, json).commit();
    }

    //
    // Get
    //

    public String getString(String key) {
        return this.prefs.getString(key, null);
    }

    public Long getLong(String key) {
        return this.prefs.getLong(key, 0L);
    }

    public UserVM getUserInfo() {
        String json = this.prefs.getString(USER_INFO, null);
        UserVM userInfo = new Gson().fromJson(json, UserVM.class);
        Log.d(this.getClass().getSimpleName(), "=====> getUserInfo: json="+json);
        return userInfo;
    }

    public List<LocationVM> getDistricts() {
        Type type = new TypeToken<List<LocationVM>>() {}.getType();
        String json = this.prefs.getString(DISTRICTS, null);
        List<LocationVM> districts = new Gson().fromJson(json, type);
        Log.d(this.getClass().getSimpleName(), "=====> getDistricts: size="+districts.size());
        return districts;
    }

    public List<EmoticonVM> getEmoticons() {
        Type type = new TypeToken<List<EmoticonVM>>() {}.getType();
        String json = this.prefs.getString(EMOTICONS, null);
        List<EmoticonVM> emoticons = new Gson().fromJson(json, type);
        Log.d(this.getClass().getSimpleName(), "=====> getEmoticons: size="+emoticons.size());
        return emoticons;
    }

    public void clear(String key) {
        this.prefs.edit().remove(key).commit();
    }

    public void clearAll() {
        this.prefs.edit().clear().commit();
    }
}
