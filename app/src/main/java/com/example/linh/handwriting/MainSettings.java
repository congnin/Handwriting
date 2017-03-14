package com.example.linh.handwriting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.linh.handwriting.utils.DialogHelper;
import com.example.linh.handwriting.utils.WritePadFlagManager;
import com.example.linh.handwriting.utils.WritePadManager;

/**
 * Created by Linh on 3/8/2017.
 */

public class MainSettings extends PreferenceActivity {
    public static final String TAG = "MainSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference languagePreference = findPreference(getResources().getString(R.string.preference_main_settings_language_key));
            languagePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    DialogHelper.createLanguageDialog(MainSettings.this, getCurrentFocus().getWindowToken()).show();
                    return true;
                }
            });
            WritePadManager.recoInit(this);
            WritePadFlagManager.initialize(getBaseContext());
        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage(), e);
            finish();
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
            finish();
        }
    }



    public static String getLanguage(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getResources().getString(R.string.preference_main_settings_language_key), null);
    }

    public static void setLanguage(Context context, String language) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.preference_main_settings_language_key), language);
        editor.commit();
    }

    private static boolean getCheckBoxPreference(Context context, Resources resources, int resourceID, boolean defaultValue) {
        boolean result;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = resources.getString(resourceID);
        result = preferences.contains(key) ? preferences.getBoolean(key, false) : defaultValue;
        return result;
    }


    public static boolean isSeparateLetterModeEnabled(Context context) {
        return getCheckBoxPreference(context, context.getResources(), R.string.preference_recognizer_separate_letters_key, false);
    }


    public static boolean isSingleWordEnabled(Context context) {
        return getCheckBoxPreference(context, context.getResources(), R.string.preference_recognizer_single_word_only_key, false);
    }
}
