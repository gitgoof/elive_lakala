package com.lakala.platform.activity;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.R;
import com.lakala.platform.config.Config;

/**
 * 2014/12/18 - 15:41
 * Created by lianglong.
 */
public class DebugSettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.setActionBar();
        } catch (Exception e) {
            LogUtil.print("", e.toString());
        }
        addPreferencesFromResource(R.xml.debug_preference);
        //服务器地址
        String key_serverAddress = getString(R.string.debug_key_server_address);
        final Preference serverAddress = findPreference(key_serverAddress);
        serverAddress.setSummary(Config.getBaseUrlFromPreference());
        serverAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                serverAddress.setSummary(newValue.toString());
                return true;
            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Sencha地址
        String key_senchaAddress = getString(R.string.debug_key_remote_sencha);
        final Preference senchaAddress = findPreference(key_senchaAddress);
        senchaAddress.setSummary(preferences.getString(key_senchaAddress,"未配置"));
        senchaAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                senchaAddress.setSummary(newValue.toString());
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setActionBar() {
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.plat_selector_btn_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            returnHome();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        returnHome();
    }

    private void returnHome() {
        setResult(RESULT_OK);
        finish();
    }

}