package com.expensedroid.expensedroid;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by S. Ameli on 08/07/16.
 */
public class SettingsIO {

    public static final String SETTINGS_TITLE = "settings";

    public static <T> T readData(Context context, T data, String key){
        SharedPreferences settings = context.getSharedPreferences(SETTINGS_TITLE, 0);

        if(data instanceof String){
            return (T) (String) settings.getString(key, (String)data);

        }else if(data instanceof Integer){
            return (T) (Integer) settings.getInt(key, (Integer)data);

        }else if(data instanceof Boolean){
            return (T) (Boolean) settings.getBoolean(key, (Boolean)data);
        }

        return null;
    }
}
