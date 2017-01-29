package com.example.daanish.dhfl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Daanish on 26-01-2017.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {


        final Context ctx = context;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            new Thread(new Runnable() {
                public void run() {
                    try {
                        HttpURLConnection urlc = (HttpURLConnection) (new URL(
                                "http://clients3.google.com/generate_204").openConnection());
                        urlc.setRequestProperty("User-Agent", "Test");
                        urlc.setRequestProperty("Connection", "close");
                        urlc.setConnectTimeout(3000);
                        urlc.setReadTimeout(4000);
                        urlc.connect();
                        if(urlc.getResponseCode() == 204 && urlc.getContentLength() == 0){
                            ctx.startService(new Intent(ctx, SyncService.class));
                        }

                    }
                    catch (IOException e) {
                        Log.e("Gaya","Kaam se");
                    }
                }
            }).start();
        }


    }
}

