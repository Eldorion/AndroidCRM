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

class OpportunityPull extends AsyncTask<String, Void, String> {
    ProgressDialog pd;
    Context context;
    String username, password, offset, timestamp, fromwhere;

    public OpportunityPull(Context context, String username, String password, String offset, String timestamp, String fromwhere) {
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

        String login_url = "http://192.168.1.31:81/api/pull_opportunity.php";
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
                    JSONObject opportunity = entry_list.getJSONObject(i);

                    String deleted = opportunity.getString("deleted");
                    if(deleted.equals("1")){
                        //call delete method,
                        continue;
                    }
                    String id = opportunity.getString("id");
                    String name = opportunity.getString("name");
                    String amount = opportunity.getString("amount");
                    String opportunity_type = opportunity.getString("opportunity_type");
                    String lead_source = opportunity.getString("lead_source");
                    String probability = opportunity.getString("probability");
                    String assigned_user_id = opportunity.getString("assigned_user_id");
                    String assigned_user_name = opportunity.getString("assigned_user_name");
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

        Toast.makeText(context, "Opp pulled", Toast.LENGTH_LONG).show();
    }
}


class OpportunityPush extends AsyncTask<String, Void, String> {
    ProgressDialog pd;
    Context context;
    String username, password, fromwhere;

    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    // list = call to Databse class to get ArrayList

    public OpportunityPush(Context context, String username, String password, String fromwhere) {
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


        String login_url = "http://192.168.2.8:81/api/push_opportunity.php";
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

                HashMap<String, String> current_opportunity = list.get(i);

                String new_with_id = current_opportunity.get("new_with_id");
                String id = current_opportunity.get("id");
                String name = current_opportunity.get("name");
                String amount = current_opportunity.get("amount");
                String opportunity_type = current_opportunity.get("opportunity_type");
                String lead_source = current_opportunity.get("lead_source");
                String probability = current_opportunity.get("probability");
                String assigned_user_id = current_opportunity.get("assigned_user_id");
                String assigned_user_name = current_opportunity.get("assigned_user_name");


                post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") +
                        "&" + URLEncoder.encode("user_hash", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") +
                        "&" + URLEncoder.encode("new_with_id", "UTF-8") + "=" + URLEncoder.encode(new_with_id, "UTF-8") +
                        "&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") +
                        "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") +
                        "&" + URLEncoder.encode("account_id", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("currency_id", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount, "UTF-8") +
                        "&" + URLEncoder.encode("date_closed", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("opportunity_type", "UTF-8") + "=" + URLEncoder.encode(opportunity_type, "UTF-8") +
                        "&" + URLEncoder.encode("sales_stage", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("lead_source", "UTF-8") + "=" + URLEncoder.encode(lead_source, "UTF-8") +
                        "&" + URLEncoder.encode("probability", "UTF-8") + "=" + URLEncoder.encode(probability, "UTF-8") +
                        "&" + URLEncoder.encode("campaign_id", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("assigned_user_id", "UTF-8") + "=" + URLEncoder.encode(assigned_user_id, "UTF-8") +
                        "&" + URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("account_types_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("nature_of_account_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("application_no1_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("account_no1_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("account_type2_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("nature_of_account1_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("bank_name1_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("address1_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("city1_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("state1_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("postalcode1_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("country1_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("micr_nobankacc_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("ifsc_code_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("customer_relationship_nobank_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("dp_name_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("dp_id_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("client_iddepositorydetails_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("depository_namedepositorydet_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("segment_and_exchange_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("financial_proofdepositorydet_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("whether_you_wish_to_receive_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("titlenomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("nomination_option_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("titlenomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("first_namenomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("last_namenomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("pannomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("aadhaar_numbernomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("savings_bank_account_nonomin_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("proofofidentitysubmittednomi_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("dp_idnomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("client_idnomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("relationshipwithapplicantnom_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("addressnomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("citynomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("state_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("postal_codenomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("countrynomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("office_phonenomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("residential_phonenomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("mobilenomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("email_addressnomination_c", "UTF-8") + "=" + URLEncoder.encode(lead_source, "UTF-8") +
                        "&" + URLEncoder.encode("first_nameminornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("last_fieldminornomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("panminornomination_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("aadharnominornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("savingsbankaccountnominornom_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("proofofidentitysubmiotedmino_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("dp_idminornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("client_idminornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("relationshipofguardianminorn_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("minoraddress_c", "UTF-8") + "=" + URLEncoder.encode(lead_source, "UTF-8") +
                        "&" + URLEncoder.encode("stateminornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("cityminornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("countryminornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("postalcodeminornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("officephoneminornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("residentialphoneminornomine_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("mobileminornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("emailaddressminornominee_c", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("contacts_opportunities_1contacts_ida", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") +
                        "&" + URLEncoder.encode("contact_id_c", "UTF-8") + "=" + URLEncoder.encode(lead_source, "UTF-8");
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
