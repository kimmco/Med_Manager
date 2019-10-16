package com.cokimutai.med_manager.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtilities {

    public static final String KEY_SWALLOW_COUNT = "swallow_count";

    private static final int DEFAULT_COUNT = 0;

    synchronized private static void setSwallowCount(Context context, int medSwallowed){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SWALLOW_COUNT, medSwallowed);
        editor.apply();
    }

    public static int getSwallowCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int noOfMedicines = prefs.getInt(KEY_SWALLOW_COUNT, DEFAULT_COUNT);
        return noOfMedicines;
    }
    synchronized public static void incrementSwallowCount(Context context){
        int medCount = PreferenceUtilities.getSwallowCount(context);
        PreferenceUtilities.setSwallowCount(context, ++medCount);
    }


}
