package com.expensedroid.expensedroid;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by S. Ameli on 08/07/16.
 */
public class SettingsIO {

    public static <T> T readData(Context context, T data, String key){
        SharedPreferences settings = context.getSharedPreferences(Tools.SETTINGS_TITLE, 0);

        if(data instanceof String){
            return (T) (String) settings.getString(key, (String)data);

        }else if(data instanceof Integer){
            return (T) (Integer) settings.getInt(key, (Integer)data);

        }else if(data instanceof Boolean){
            return (T) (Boolean) settings.getBoolean(key, (Boolean)data);
        }

        return null;
    }

    /*
    when passing data to this method, preferably declare type
     */
    public static <T> Boolean saveData(Context context, T data, String key) {
        SharedPreferences settings = context.getSharedPreferences(Tools.SETTINGS_TITLE, 0);
        SharedPreferences.Editor editor = settings.edit();

        if (data instanceof String) {
            //System.out.println("saveData: data is String");
            editor.putString(key, (String) data);
            editor.commit();

        } else if (data instanceof Integer) {
            //System.out.println("saveData: data is Integer");
            editor.putInt(key, (Integer) data);
            editor.commit();

        } else if (data instanceof Boolean) {
            //System.out.println("saveData: data is Boolean");
            editor.putBoolean(key, (Boolean) data);
            editor.commit();
        }

        return null;
    }

}
