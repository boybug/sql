package com.newit.bsrpos_sql.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.text.DecimalFormat;

public class Global {

    public static void setwh_Grp_Id(int wh_grp_Id, Context context) {
        setInt("wh_grp_Id", wh_grp_Id, context);
    }

    public static int getwh_Grp_Id(Context context) {
        return getInt("wh_grp_Id", context);
    }

    public static void setwh_grp_name(String wh_grp_name, Context context) {
        setString("wh_grp_name", wh_grp_name, context);
    }

    public static String getwh_grp_name(Context context) {
        return getString("wh_grp_name", context);
    }

    public static void setisLocal(boolean isLocal, Context context) {
        setBoolean("isLocal", isLocal, context);
    }

    public static boolean getisLocal(Context context) {
        return getBoolean("isLocal", context);
    }

    public static void setisLocal(User user, Context context) {
        setObject("user", user, context);
    }

    public static void setUser(User user, Context context) {
        setObject("user", user, context);
    }

    public static User getUser(Context context) {
        return (User) getObject("user", context, User.class);
    }

    public static void setDatabase(Database database, Context context) {
        setObject("database", database, context);
    }

    public static Database getDatabase(Context context) {
        return (Database) getObject("database", context, Database.class);
    }

    private static void setString(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }


    private static void setInt(String key, int value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static int getInt(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }

    private static void setBoolean(String key, boolean value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private static boolean getBoolean(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

    private static void setObject(String key, Object value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.commit();
    }

    private static Object getObject(String key, Context context, Class className) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = preferences.getString(key, null);
        return gson.fromJson(json, className);
    }


    public static String getFbStockPath(Context context) {
        return "fbstock" + String.valueOf(Global.getwh_Grp_Id(context));
    }

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String formatMoney(float value) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        return df.format(value);
    }
}
