package com.lhht.xiaozhi.settings;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

public class SettingsManager {
    private static final String PREF_NAME = "xiaozhi_settings";
    private static final String KEY_WS_URL = "ws_url";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ENABLE_TOKEN = "enable_token";
    private static final String KEY_WS_URLS = "ws_urls";
    
    private final SharedPreferences preferences;
    
    public SettingsManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveSettings(String wsUrl, String token, boolean enableToken) {
        preferences.edit()
                .putString(KEY_WS_URL, wsUrl)
                .putString(KEY_TOKEN, token)
                .putBoolean(KEY_ENABLE_TOKEN, enableToken)
                .apply();
    }

    public void saveWsUrls(Set<String> urls) {
        preferences.edit()
                .putStringSet(KEY_WS_URLS, urls)
                .apply();
    }
    
    public String getWsUrl() {
        return preferences.getString(KEY_WS_URL, "ws://localhost:9005");
    }
    
    public String getToken() {
        return preferences.getString(KEY_TOKEN, "test-token");
    }
    
    public boolean isTokenEnabled() {
        return preferences.getBoolean(KEY_ENABLE_TOKEN, true);
    }

    public Set<String> getWsUrls() {
        return preferences.getStringSet(KEY_WS_URLS, null);
    }
} 