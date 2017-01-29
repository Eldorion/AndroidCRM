package com.example.daanish.dhfl;

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

/**
 * Created by Daanish on 24-01-2017.
 */

class EmployeePull extends AsyncTask<String, Void, String> {
    ProgressDialog pd;
    Context context;
    String username, password, offset, timestamp, fromwhere;

    public EmployeePull(Context context, String username, String password, String offset, String timestamp, String fromwhere) {
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

        String login_url = "http://192.168.2.8:81/api/pull_emp.php";
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
                    JSONObject users = entry_list.getJSONObject(i);


                    String id = users.getString("id");
                    String name = users.getString("name");
                    String user_name = users.getString("user_name");


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
        } else if (fromwhere.equals("service")) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(5);
        }

        Toast.makeText(context, "Emp pulled", Toast.LENGTH_SHORT).show();
    }
}
