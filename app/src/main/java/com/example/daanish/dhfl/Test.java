package com.example.daanish.dhfl;

/**
 * Created by Daanish on 28-01-2017.
 */




import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daanish on 24-01-2017.
 */

class Test extends AsyncTask<String, Void, String> {
    Context context;
    String username, password, offset, timestamp;

    public Test(Context context, String username, String password, String offset, String timestamp) {
        this.context = context;
        this.username = username;
        this.password = password;
        this.offset = offset;
        this.timestamp = timestamp;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {

        String login_url = "http://192.168.2.8:81/api/pull_leads.php";
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

                result = "";
                // looping through All Leads
                for (int i = 0; i < entry_list.length(); i++) {
                    JSONObject leads = entry_list.getJSONObject(i);

                    String deleted = leads.getString("deleted");
                    if(deleted.equals("1")){
                        //call delete method,
                        continue;
                    }
                    String id = leads.getString("id");
                    String email = leads.getString("email1");
                    String date_entered = leads.getString("date_entered");
                    String date_modified = leads.getString("date_modified");
                    String designation_c = leads.getString("designation_c");
                    String assigned_user_id = leads.getString("assigned_user_id");
                    String assigned_user_name = leads.getString("assigned_user_name");
                    String salutation = leads.getString("salutation");
                    String first_name = leads.getString("first_name");
                    String last_name = leads.getString("last_name");
                    String phone_mobile = leads.getString("phone_mobile");
                    String description = leads.getString("description");
                    String primary_address_street = leads.getString("primary_address_street");
                    String primary_address_city = leads.getString("primary_address_city");
                    String primary_address_state = leads.getString("primary_address_state");
                    String primary_address_country = leads.getString("primary_address_country");
                    String primary_address_postalcode = leads.getString("primary_address_postalcode");
                    String alt_address_street = leads.getString("alt_address_street");
                    String alt_address_city = leads.getString("alt_address_city");
                    String alt_address_state = leads.getString("alt_address_state");
                    String alt_address_country = leads.getString("alt_address_country");
                    String alt_address_postalcode = leads.getString("alt_address_postalcode");
                    String lead_source = leads.getString("lead_source");
                    String status = leads.getString("status");
                    String aadhaar_number_c = leads.getString("aadhaar_number_c");
                    String date_of_birth_c = leads.getString("date_of_birth_c");
                    String gender_c = leads.getString("gender_c");
                    String pan_c = leads.getString("pan_c");
                    String identity_proof_c = leads.getString("identity_proof_c");
                    String occupation1_c = leads.getString("occupation1_c");
                    String residence_addr_proof_c = leads.getString("residence_addr_proof_c");
                    String priority_c = leads.getString("priority_c");
                    String toPush = "0";

                    result += id+" "+first_name+"\n";
                    //replace above statement with call to database, insert call

                }
            } catch (final JSONException e) {
                e.printStackTrace();
            }
        }

        return result;

    }

    @Override
    protected void onPostExecute(String result) {

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(5);

        Toast.makeText(context, "Leads pulled", Toast.LENGTH_LONG).show();
    }
}


