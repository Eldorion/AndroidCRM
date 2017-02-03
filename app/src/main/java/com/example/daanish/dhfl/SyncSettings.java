package com.example.daanish.dhfl;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.HashMap;

import static com.example.daanish.dhfl.R.styleable.CoordinatorLayout;

public class SyncSettings extends AppCompatActivity {
    ToggleButton tb1, tb2;
    Button b;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    String offset = "0";
    String timestamp = "1000-01-01 00:00:00";
    String username, password;
    boolean auto_sync, only_wifi;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final NetworkCheck check = new NetworkCheck(getBaseContext());

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        username = user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);


        HashMap<String, Boolean> syncPref = session.getSyncPref();
        auto_sync = syncPref.get(SessionManager.KEY_AUTO_SYNC);
        only_wifi = syncPref.get(SessionManager.KEY_ONLY_WIFI);

        b = (Button) findViewById(R.id.button);
        tb1 = (ToggleButton) findViewById(R.id.toggleButton1);
        tb1.setChecked(auto_sync);

        tb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                session.saveSyncPref(tb1.isChecked());
            }
        });

        tb2 = (ToggleButton) findViewById(R.id.toggleButton2);
        tb2.setChecked(only_wifi);

        tb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                session.saveSyncWifi(tb2.isChecked());
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LeadPull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
                new OpportunityPull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
                new MeetingPull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
                new EmployeePull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();

                /*
                if(session.checkWifiState())
                    if(check.isConnectingToInternet()) {
                        new LeadPull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
                        new OpportunityPull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
                        new MeetingPull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
                        new EmployeePull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
                    }
                    else {
                        Snackbar snackbar1 = Snackbar.make(v, "No Internet Connection Available", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                else{
                    Snackbar snackbar2 = Snackbar.make(v, "Connect to a Wifi Network or change preference", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                */
            }

        });

    }
}
