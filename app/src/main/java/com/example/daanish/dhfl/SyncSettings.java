package com.example.daanish.dhfl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.HashMap;

public class SyncSettings extends AppCompatActivity {
    ToggleButton tb;
    Button b;
    String offset = "0";
    String timestamp = "1000-01-01 00:00:00";
    String username, password;
    boolean auto_sync;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_settings);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        username = user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);


        HashMap<String, Boolean> syncPref = session.getSyncPref();
        auto_sync = syncPref.get(SessionManager.KEY_AUTO_SYNC);

        b = (Button) findViewById(R.id.button);
        tb = (ToggleButton) findViewById(R.id.toggleButton);
        tb.setChecked(auto_sync);

        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(tb.isChecked()){
                    session.saveSyncPref(true);
                }
                else{
                    session.saveSyncPref(false);
                }
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LeadPull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
                new OpportunityPull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
                new MeetingPull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
                new EmployeePull(SyncSettings.this, username, password, offset, timestamp, "sync_settings").execute();
            }
        });

    }
}
