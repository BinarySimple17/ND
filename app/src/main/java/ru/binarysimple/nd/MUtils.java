package ru.binarysimple.nd;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MUtils {

    public static int getThemeId(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String ListPreference = sp.getString("example_list", "0");
        if (ListPreference.equals("0")) {
            return R.style.AppTheme;
        }
        else if (ListPreference.equals("1")){
            return R.style.AppThemeDark;
        }
        return R.style.AppTheme;
    }

    public static boolean getRound (Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("round_switch", true);

    }

}
