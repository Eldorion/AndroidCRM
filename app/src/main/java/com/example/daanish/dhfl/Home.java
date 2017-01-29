package com.example.daanish.dhfl;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* Nav Home elements */
    TextView userName, userEmail;
    String offset = "0";
    String timestamp = "1000-01-01 00:00:00";
    String username, password;
    /* Shared Preferences */
    SessionManager session;

    public static boolean flag = false;
    Boolean first_login = false;

    ArrayList<HashMap<String, String>> leadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("logout"));

        session = new SessionManager(getApplicationContext());
        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the session data
                // This will clear all session data and
                // redirect user to LoginActivity
                session.logoutUser();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*-------------------------------------------------------*/
        /*My code starts below this, the above remains untouched */
        /*-------------------------------------------------------*/

        View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_home);;
        userName = (TextView) navHeaderView.findViewById(R.id.user_name);
        userEmail = (TextView) navHeaderView.findViewById(R.id.user_email);

        Toast.makeText(getBaseContext(), session.isLoggedIn()+"", Toast.LENGTH_LONG).show();



        HashMap<String, String> user = session.getUserDetails();

        username = user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        String id = user.get(SessionManager.KEY_ID);
        String user_id = user.get(SessionManager.KEY_USER_ID);
        String status = user.get(SessionManager.KEY_STATUS);
        String title = user.get(SessionManager.KEY_TITLE);
        String user_email = user.get(SessionManager.KEY_USER_EMAIL);
        String first_login = user.get(SessionManager.KEY_FIRST_LOGIN);

        if(session.isLoggedIn() && first_login.equals("true")){
            new LeadPull(Home.this, username, password, offset, timestamp, "sync_settings").execute();
            new OpportunityPull(Home.this, username, password, offset, timestamp, "sync_settings").execute();
            new MeetingPull(Home.this, username, password, offset, timestamp, "sync_settings").execute();
            new EmployeePull(Home.this, username, password, offset, timestamp, "sync_settings").execute();
            session.setFirstLoginFalse();
        }

        userName.setText(username);
        userEmail.setText(user_email);

        Toast.makeText(getBaseContext(), username+" "+password+" "+id, Toast.LENGTH_LONG).show();


    }


    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //mar jaa
            finish();
        }
    };


    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.sync){
            startActivity(new Intent(getBaseContext(), SyncSettings.class));
            /*
            new LeadPull(Home.this, username, password, offset, timestamp).execute();
            new OpportunityPull(Home.this, username, password, offset, timestamp).execute();
            new MeetingPull(Home.this, username, password, offset, timestamp).execute();
            new EmployeePull(Home.this, username, password, offset, timestamp).execute();
            */
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    class HomeFetchData extends AsyncTask<String, Void, String> {


        String username, password, offset, timestamp;
        ProgressDialog pd = new ProgressDialog(Home.this);
        ListView lead_list;

        public HomeFetchData(String username, String password, String offset, String timestamp) {
            this.username = username;
            this.password = password;
            this.offset = offset;
            this.timestamp = timestamp;
        }

        @Override
        protected void onPreExecute() {

            lead_list = (ListView) findViewById(R.id.lead_list);

            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.setTitle("Please Wait..");
            pd.setMessage("Fetching Data...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String login_url = "http://192.168.1.31:81/api/ticketinfoleads2.php";
            String result = "";

            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"+URLEncoder.encode("user_hash","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"+URLEncoder.encode("offset","UTF-8")+"="+URLEncoder.encode(offset,"UTF-8")+"&"+URLEncoder.encode("Timestamp","UTF-8")+"="+URLEncoder.encode(timestamp,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line;
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (result != null) {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    JSONArray entry_list = jsonObj.getJSONArray("entry_list");


                    // looping through All Leads
                    for (int i = 0; i < entry_list.length(); i++) {
                        JSONObject leads = entry_list.getJSONObject(i);

                        String id = leads.getString("id");
                        String name = leads.getString("first_name")+" "+leads.getString("last_name");
                        String status = leads.getString("status");
                        String date_entered = leads.getString("date_entered");

                        // tmp hash map for single lead
                        HashMap<String, String> lead = new HashMap<>();

                        // adding each child node to HashMap key => value
                        lead.put("id", id);
                        lead.put("name", name);
                        lead.put("status", status);
                        lead.put("date_entered", date_entered);

                        // adding contact to contact list
                        leadList.add(lead);
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            }


            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            if (pd.isShowing())
                pd.dismiss();


            ListAdapter adapter = new SimpleAdapter(
                    Home.this, leadList,
                    R.layout.lead_list, new String[]{"name", "status", "date_entered"}, new int[]{R.id.name, R.id.status, R.id.date_entered});

            lead_list.setAdapter(adapter);
        }

    }
    */


}



