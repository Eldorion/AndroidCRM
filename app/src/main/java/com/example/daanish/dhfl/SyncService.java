package com.example.daanish.dhfl;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DebugUtils;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class SyncService extends Service {

    static String username, password;
    boolean auto_sync;
    SessionManager session;
    static final String ACTION_1 = "action_1";
    static int mId = 5, nId = 6;
    public SyncService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        username = user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);


        HashMap<String, Boolean> syncPref = session.getSyncPref();
        auto_sync = syncPref.get(SessionManager.KEY_AUTO_SYNC);

        if(auto_sync){
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.sync)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("Sycning your data, this may take a few minutes"))
                            .setContentTitle("DHFL Pramerica")
                            .setContentText("Syncing your data, this may take a few minutes")
                            .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                            .setAutoCancel(false)
                            .setOngoing(true);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            new LeadPull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
            new OpportunityPull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
            new MeetingPull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
            new EmployeePull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
            mNotificationManager.notify(mId, mBuilder.build());
        }
        else{

            Intent action1Intent = new Intent(getBaseContext(), NotificationActionService.class)
                    .setAction(ACTION_1);
            Intent i = new Intent(this, SyncSettings.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
            PendingIntent action1PendingIntent = PendingIntent.getService(getBaseContext(), 0, action1Intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(0, "Sync Now", action1PendingIntent).build();
            NotificationCompat.Builder nBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.sync)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("Syncing your data, this may take a few minutes"))
                            .setContentTitle("DHFL Pramerica")
                            .setContentText("Internet Connection Available")
                            .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                            .setAutoCancel(true)
                            .addAction(action1);
            nBuilder.setContentIntent(contentIntent);
            NotificationManager nNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nNotificationManager.notify(nId, nBuilder.build());
        }


        /*
        Intent i = new Intent(this, ResultActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
        mBuilder.setContentIntent(contentIntent);

        NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(0, "Sync Now", contentIntent).build();
        mBuilder.addAction(action1).setAutoCancel(true);
        */

        return START_STICKY;
    }

    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            if (ACTION_1.equals(action)) {
                // TODO: handle action 1.
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(6);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.sync)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Sycning your data, this may take a few minutes"))
                                .setContentTitle("DHFL Pramerica")
                                .setContentText("Syncing your data, this may take a few minutes")
                                .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                                .setAutoCancel(false)
                                .setOngoing(true);
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                new Thread(new Runnable() {
                    public void run() {

                            new LeadPull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
                            new OpportunityPull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
                            new MeetingPull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
                            new EmployeePull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();

                    }
                }).start();

                mNotificationManager.notify(mId, mBuilder.build());
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}