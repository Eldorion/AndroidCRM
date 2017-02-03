package com.example.daanish.dhfl;

/**
 * Created by Daanish on 09-01-2017.
 */

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "SharedPref";

    // All Shared Preferences Keys
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ID = "id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_STATUS = "status";
    public static final String KEY_TITLE = "title";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_AUTO_SYNC = "auto_sync";
    public static final String KEY_ONLY_WIFI = "only_wifi";
    public static final String KEY_FIRST_LOGIN = "first_login";

    // Constructor
    public SessionManager(Context context){
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String username, String password, String id, String user_id, String status, String title, boolean user_is_admin, String user_email){

        HashMap<String, String> user = getUserDetails();
        if(user.get(KEY_USERNAME)!=null && user.get(KEY_USERNAME).equals(username)){
            editor.putString(KEY_FIRST_LOGIN, "false");
        }
        else {
            editor.putString(KEY_FIRST_LOGIN, "true");
        }
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_USERNAME, username);

        // Storing email in pref
        editor.putString(KEY_PASSWORD, password);

        // my extra data
        editor.putString(KEY_ID, id);
        editor.putString(KEY_USER_ID, user_id);
        editor.putString(KEY_STATUS, status);
        editor.putString(KEY_TITLE, title);
        editor.putString(KEY_USER_EMAIL, user_email);
        editor.putBoolean(KEY_AUTO_SYNC, false);
        editor.putBoolean(KEY_ONLY_WIFI, false);
        // commit changes
        editor.commit();
    }

    public void saveSyncPref(boolean pref){
        editor.putBoolean(KEY_AUTO_SYNC, pref);
        editor.commit();
    }
    public void saveSyncWifi(boolean pref){
        editor.putBoolean(KEY_ONLY_WIFI, pref);
        editor.commit();
    }
    public HashMap<String, Boolean> getSyncPref(){
        HashMap<String, Boolean> syncPref = new HashMap();
        syncPref.put(KEY_AUTO_SYNC, pref.getBoolean(KEY_AUTO_SYNC, false));
        syncPref.put(KEY_ONLY_WIFI, pref.getBoolean(KEY_ONLY_WIFI, false));
        return syncPref;
    }

    public void setFirstLoginFalse(){
        editor.putString(KEY_FIRST_LOGIN, "false");
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){

            //Send Broadcast to kill all activities which are receiving those broadcasts
            Intent intent = new Intent("logout");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // user password
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // my extra data

        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_STATUS, pref.getString(KEY_STATUS, null));
        user.put(KEY_TITLE, pref.getString(KEY_TITLE, null));
        user.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, null));
        user.put(KEY_FIRST_LOGIN, pref.getString(KEY_FIRST_LOGIN, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();

        Intent intent = new Intent("logout");
        // You can also include some extra data.
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        /*
        http://stackoverflow.com/questions/3007998/on-logout-clear-activity-history-stack-preventing-back-button-from-opening-l/3008684#3008684
        http://www.sanfoundry.com/java-android-program-demonstrate-local-broadcast-manager/
        http://stackoverflow.com/questions/8022999/how-do-i-sendbroadcastintent-from-mydialog-and-receive-in-myactivity
        http://stackoverflow.com/questions/3007998/on-logout-clear-activity-history-stack-preventing-back-button-from-opening-l
        http://stackoverflow.com/questions/9426346/how-to-kill-all-activities-in-android-application
        */


        // After logout redirect user to Login Activity
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean checkWifiState(){
        if(pref.getBoolean(KEY_ONLY_WIFI, false)==false)
            return true;
        else{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo.getType()==ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }
}

