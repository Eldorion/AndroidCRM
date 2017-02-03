package com.example.daanish.dhfl;


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

class LeadPull extends AsyncTask<String, Void, String> {
    ProgressDialog pd;
    Context context;
    String username, password, offset, timestamp, fromwhere;

    public LeadPull(Context context, String username, String password, String offset, String timestamp, String fromwhere) {
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

        String login_url = "http://192.168.1.31:81/api/pull_leads.php";
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
        if(fromwhere.equals("sync_settings")){
            if (pd.isShowing())
                pd.dismiss();
        }
        Toast.makeText(context, "Leads pulled", Toast.LENGTH_LONG).show();
    }
}


class LeadPush extends AsyncTask<String, Void, String> {
    ProgressDialog pd;
    Context context;
    String username, password, fromwhere;

    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    // list = call to Databse class to get ArrayList

    public LeadPush(Context context, String username, String password, String fromwhere) {
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


        String login_url = "http://192.168.2.8:81/api/push_leads.php";
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

                HashMap<String, String> current_lead = list.get(i);

                String new_with_id = current_lead.get("new_with_id");
                String id = current_lead.get("id");
                String email = current_lead.get("email");
                String date_entered = current_lead.get("date_entered");
                String date_modified = current_lead.get("date_modified");
                String designation_c = current_lead.get("designation_c");
                String assigned_user_id = current_lead.get("assigned_user_id");
                String assigned_user_name = current_lead.get("assigned_user_name");
                String salutation = current_lead.get("salutation");
                String first_name = current_lead.get("first_name");
                String last_name = current_lead.get("last_name");
                String phone_mobile = current_lead.get("phone_mobile");
                String description = current_lead.get("description");
                String primary_address_street = current_lead.get("primary_address_street");
                String primary_address_city = current_lead.get("primary_address_city");
                String primary_address_state = current_lead.get("primary_address_state");
                String primary_address_country = current_lead.get("primary_address_country");
                String primary_address_postalcode = current_lead.get("primary_address_postalcode");
                String alt_address_street = current_lead.get("alt_address_street");
                String alt_address_city = current_lead.get("alt_address_city");
                String alt_address_state = current_lead.get("alt_address_state");
                String alt_address_country = current_lead.get("alt_address_country");
                String alt_address_postalcode = current_lead.get("alt_address_postalcode");
                String lead_source = current_lead.get("lead_source");
                String status = current_lead.get("status");
                String aadhaar_number_c = current_lead.get("aadhaar_number_c");
                String date_of_birth_c = current_lead.get("date_of_birth_c");
                String gender_c = current_lead.get("gender_c");
                String pan_c = current_lead.get("pan_c");
                String identity_proof_c = current_lead.get("identity_proof_c");
                String occupation1_c = current_lead.get("occupation1_c");
                String residence_addr_proof_c = current_lead.get("residence_addr_proof_c");
                String priority_c = current_lead.get("priority_c");


                post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") +
                        "&" + URLEncoder.encode("user_hash", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") +
                        "&" + URLEncoder.encode("new_with_id", "UTF-8") + "=" + URLEncoder.encode(new_with_id, "UTF-8") +
                        "&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") +
                        "&" + URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(first_name, "UTF-8") +
                        "&" + URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(last_name, "UTF-8") +
                        "&" + URLEncoder.encode("salutation", "UTF-8") + "=" + URLEncoder.encode(salutation, "UTF-8") +
                        "&" + URLEncoder.encode("department", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("phone_mobile", "UTF-8") + "=" + URLEncoder.encode(phone_mobile, "UTF-8") +
                        "&" + URLEncoder.encode("phone_work", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8") +
                        "&" + URLEncoder.encode("assigned_user_id", "UTF-8") + "=" + URLEncoder.encode(assigned_user_id, "UTF-8") +
                        "&" + URLEncoder.encode("refered_by", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("campaign_id", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("primary_address_street", "UTF-8") + "=" + URLEncoder.encode(primary_address_city, "UTF-8") +
                        "&" + URLEncoder.encode("primary_address_city", "UTF-8") + "=" + URLEncoder.encode(primary_address_city, "UTF-8") +
                        "&" + URLEncoder.encode("primary_address_state", "UTF-8") + "=" + URLEncoder.encode(primary_address_state, "UTF-8") +
                        "&" + URLEncoder.encode("primary_address_country", "UTF-8") + "=" + URLEncoder.encode(primary_address_country, "UTF-8") +
                        "&" + URLEncoder.encode("primary_address_postalcode", "UTF-8") + "=" + URLEncoder.encode(primary_address_postalcode, "UTF-8") +
                        "&" + URLEncoder.encode("alt_address_street", "UTF-8") + "=" + URLEncoder.encode(alt_address_street, "UTF-8") +
                        "&" + URLEncoder.encode("alt_address_city", "UTF-8") + "=" + URLEncoder.encode(alt_address_city, "UTF-8") +
                        "&" + URLEncoder.encode("alt_address_state", "UTF-8") + "=" + URLEncoder.encode(alt_address_state, "UTF-8") +
                        "&" + URLEncoder.encode("alt_address_country", "UTF-8") + "=" + URLEncoder.encode(alt_address_country, "UTF-8") +
                        "&" + URLEncoder.encode("alt_address_postalcode", "UTF-8") + "=" + URLEncoder.encode(alt_address_postalcode, "UTF-8") +
                        "&" + URLEncoder.encode("office_address_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("office_city_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("office_state_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("office_country_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("office_postal_code_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("website", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("status_description", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(description, "UTF-8") +
                        "&" + URLEncoder.encode("designation_c", "UTF-8") + "=" + URLEncoder.encode(designation_c, "UTF-8") +
                        "&" + URLEncoder.encode("contact_id_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("aadhaar_number_c", "UTF-8") + "=" + URLEncoder.encode(aadhaar_number_c, "UTF-8") +
                        "&" + URLEncoder.encode("father_spouse_name_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("date_of_birth_c", "UTF-8") + "=" + URLEncoder.encode(date_of_birth_c, "UTF-8") +
                        "&" + URLEncoder.encode("gender_c", "UTF-8") + "=" + URLEncoder.encode(gender_c, "UTF-8") +
                        "&" + URLEncoder.encode("marital_status_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("residency_status_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("pan_c", "UTF-8") + "=" + URLEncoder.encode(pan_c, "UTF-8") +
                        "&" + URLEncoder.encode("declaration_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("identity_proof_c", "UTF-8") + "=" + URLEncoder.encode(identity_proof_c, "UTF-8") +
                        "&" + URLEncoder.encode("networth_amount_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("opportunity_amount", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("occupation1_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("politically_exposed_person_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("details_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("accounts_leads_1accounts_ida", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("account_id_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("nature_of_business_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("educational_qualification_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("residence_proof_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("residence_addr_proof_c", "UTF-8") + "=" + URLEncoder.encode(residence_addr_proof_c, "UTF-8") +
                        "&" + URLEncoder.encode("priority_c", "UTF-8") + "=" + URLEncoder.encode(priority_c, "UTF-8") +
                        "&" + URLEncoder.encode("correspondance_addr_proof_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("telephone_number_of_introduc_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("status_of_introducer_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("lead_source", "UTF-8") + "=" + URLEncoder.encode(lead_source, "UTF-8") +
                        "&" + URLEncoder.encode("lead_source_description", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("lead_source_address_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("lead_source_city_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("lead_source_state_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("lead_source_postal_code_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("lead_source_country_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("experience", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("income_range_per_annum_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("preferences_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("email1", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") +
                        "&" + URLEncoder.encode("email2", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("email3", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("email4", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
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