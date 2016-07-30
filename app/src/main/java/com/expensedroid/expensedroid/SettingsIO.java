package com.expensedroid.expensedroid;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by S. Ameli on 08/07/16.
 * This class is used to read and write to settings file.
 * We don't need to specify type when writing date to settings.
 * Note: For reading data, we have to make sure to set the type of "default value" to the same type that we are requesting.
 * With the exception of Integers. Do not use null as "default value" for reading Integer values.
 */
public class SettingsIO {

    /*
     * This method reads the value of the given key from the settings file.
     * The type of the given default value will be used to choose type of data to read.
     * Type of default value must be either String, Integer or Boolean
     */
    public static <T> T readData(Context context, T defaultValue, String key){
        SharedPreferences settings = context.getSharedPreferences(Tools.SETTINGS_TITLE, 0);

        if(defaultValue instanceof String){
            return (T) (String) settings.getString(key, (String)defaultValue);

        }else if(defaultValue instanceof Integer){
            return (T) (Integer) settings.getInt(key, (Integer)defaultValue);

        }else if(defaultValue instanceof Boolean){
            return (T) (Boolean) settings.getBoolean(key, (Boolean)defaultValue);
        }

        return null;
    }

    /*
     * This method saves the given data for the given key. It automatically chooses the type.
     * The input data type must be either String, Integer or Boolean
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
