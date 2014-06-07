package info.breezes.wordman.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import info.breezes.wordman.db.WordmanDbHelper;

import java.io.File;

/**
 * Created by jianxingqiao on 14-6-6.
 */
public class WordmanApplication extends Application {

    public static final String DbName = "wordman.db";
    public static final String sharedPreferencesName = "wordman_pref";

    public static String audioCache;
    public static WordmanApplication current;

    private WordmanDbHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private ConnectivityManager connectivityManager;


    @Override
    public void onCreate() {
        super.onCreate();
        current = this;
        initMember();
    }

    public synchronized WordmanDbHelper getDbHelper() {
        return dbHelper;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public String getAudioCache() {
        return audioCache;
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void setFloat(String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public boolean isWifiConnected() {
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    private void initMember() {
        dbHelper = new WordmanDbHelper(getApplicationContext(), DbName, null, 1);
        sharedPreferences = getApplicationContext().getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
        File cFile = getExternalFilesDir("audioCache");
        if (!cFile.exists()) {
            cFile.mkdirs();
        }
        audioCache = cFile.getAbsolutePath();
        connectivityManager = (ConnectivityManager) WordmanApplication.current.getSystemService(CONNECTIVITY_SERVICE);
    }

}
