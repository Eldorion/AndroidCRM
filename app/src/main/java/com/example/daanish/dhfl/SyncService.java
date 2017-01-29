package com.example.daanish.dhfl;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import java.util.HashMap;

public class SyncService extends Service {

    String username, password;
    boolean auto_sync;
    SessionManager session;

    public SyncService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int mId = 5;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.sync)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Sycning your data, this may take a few minutes"))
                        .setContentTitle("DHFL Pramerica")
                        .setContentText("Syncing your data, this may take a few minutes")
                        .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                        .setAutoCancel(false)
                        .setOngoing(true);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        username = user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);


        HashMap<String, Boolean> syncPref = session.getSyncPref();
        auto_sync = syncPref.get(SessionManager.KEY_AUTO_SYNC);

        if(auto_sync){
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            new LeadPull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
            new OpportunityPull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
            new MeetingPull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
            new EmployeePull(getBaseContext(), username, password, "0", "1000-01-01 00:00:00", "service").execute();
            mNotificationManager.notify(mId, mBuilder.build());
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}