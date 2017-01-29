package com.example.daanish.dhfl;

/**
 * Created by Daanish on 24-01-2017.
 */

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


;

/**
 * Created by Daanish on 24-01-2017.
 */

class MeetingPull extends AsyncTask<String, Void, String> {
    ProgressDialog pd;
    Context context;
    String username, password, offset, timestamp, fromwhere;

    public MeetingPull(Context context, String username, String password, String offset, String timestamp, String fromwhere) {
        this.context = context;
        this.username = username;
        this.password = password;
        this.offset = offset;
        this.timestamp = timestamp;
        this.fromwhere = fromwhere;
        if(this.fromwhere.equals("sync_settings"))
            pd = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        if(fromwhere.equals("sync_settings")){
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.setTitle("Syncing Your Data");
            pd.setMessage("This will take a couple of minutes...");
            pd.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {

        String login_url = "http://192.168.2.8:81/api/pull_meeting.php";
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
                    JSONObject meetings = entry_list.getJSONObject(i);

                    String deleted = meetings.getString("deleted");
                    if(deleted.equals("1")){
                        //call delete method,
                        continue;
                    }
                    String id = meetings.getString("id");
                    String assigned_user_id = meetings.getString("assigned_user_id");
                    String assigned_user_name = meetings.getString("assigned_user_name");
                    String name = meetings.getString("name");
                    String status = meetings.getString("status");
                    String date_start = meetings.getString("date_start");
                    String date_end = meetings.getString("date_end");
                    String location = meetings.getString("location");
                    String description = meetings.getString("description");
                    String toPush = "0";

                    result += id+" "+name+"\n";
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

        if(fromwhere.equals("sync_settings")){
            if (pd.isShowing())
                pd.dismiss();
        }

        Toast.makeText(context, "Meetings pulled", Toast.LENGTH_LONG).show();
    }
}


class MeetingPush extends AsyncTask<String, Void, String> {
    ProgressDialog pd;
    Context context;
    String username, password, fromwhere;

    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    // list = call to Databse class to get ArrayList

    public MeetingPush(Context context, String username, String password, String fromwhere) {
        this.context = context;
        this.username = username;
        this.password = password;
        this.fromwhere = fromwhere;
        if(this.fromwhere.equals("sync_settings"))
            pd = new ProgressDialog(context);
    }


    @Override
    protected void onPreExecute() {
        if(fromwhere.equals("sync_settings")){
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.setTitle("Syncing Your Data");
            pd.setMessage("This will take a couple of minutes...");
            pd.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {


        String login_url = "http://192.168.2.8:81/api/push_meeting.php";
        String result = "";

        try {
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            String post_data = "";


            for (int i = 0; i < list.size(); i++) {

                HashMap<String, String> current_meeting = list.get(i);

                String new_with_id = current_meeting.get("new_with_id");
                String id = current_meeting.get("id");
                String name = current_meeting.get("name");
                String status = current_meeting.get("status");
                String date_start = current_meeting.get("date_start");
                String date_end = current_meeting.get("date_end");
                String description = current_meeting.get("description");
                String assigned_user_id = current_meeting.get("assigned_user_id");
                String assigned_user_name = current_meeting.get("assigned_user_name");
                String location = current_meeting.get("location");


                post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") +
                        "&" + URLEncoder.encode("user_hash", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") +
                        "&" + URLEncoder.encode("new_with_id", "UTF-8") + "=" + URLEncoder.encode(new_with_id, "UTF-8") +
                        "&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") +
                        "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") +
                        "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8") +
                        "&" + URLEncoder.encode("date_start", "UTF-8") + "=" + URLEncoder.encode(date_start, "UTF-8") +
                        "&" + URLEncoder.encode("date_end", "UTF-8") + "=" + URLEncoder.encode(date_end, "UTF-8") +
                        "&" + URLEncoder.encode("parent_type", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("parent_id", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") +
                        "&" + URLEncoder.encode("duration_hours", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("duration_minutes", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(description, "UTF-8") +
                        "&" + URLEncoder.encode("assigned_user_id", "UTF-8") + "=" + URLEncoder.encode(assigned_user_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }


                bufferedWriter.close();
                outputStream.close();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            }
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        } catch(ProtocolException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {

        if(fromwhere.equals("sync_settings")){
            if (pd.isShowing())
                pd.dismiss();
        }

        Toast.makeText(context, "Push\n"+result, Toast.LENGTH_LONG).show();
    }

}
