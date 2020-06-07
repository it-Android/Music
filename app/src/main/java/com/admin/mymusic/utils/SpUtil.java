package com.admin.mymusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/4/2 21:36
 **/
public class SpUtil {
    private final static String SP_NAME="setting";
    public final static String CSRF="csrf";//

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context){
        return getSharedPreferences(context).edit();
    }


    /*****************************************************************************************************************************************************************************************************************************************************************************
     *                                                                                              对外API
     *****************************************************************************************************************************************************************************************************************************************************************************
     */


    public static void putString(Context context,String key,String data){
        getEditor(context).putString(key,data).commit();
    }

    public static String getString(Context context,String key){
        return getSharedPreferences(context).getString(key,"");
    }

}
