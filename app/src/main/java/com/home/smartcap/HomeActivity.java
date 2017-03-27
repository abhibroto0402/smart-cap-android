package com.home.smartcap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by amukherjee on 11/3/16.
 */

public class HomeActivity extends AppCompatActivity {

    private ImageButton _refresh;
    private String jsonBody, emailId, user_json, med_taken_times, tempAlert, humdityAlert, drugName,pswd;
    private ImageButton _addPrescription;
    private ImageButton _temperature, _humidity;
    private ListView _mListView;
    private DrugAdapter mdrugadapter;
    private int number_of_drugs;
    private DrugSchedule[] ds;
    private TextView date, day;
    private Button logout;
    private CheckedTextView checkText;
    private static String email;
    private String result = "blank";
    private String jsonData = "Testing";

    @Override
    protected void onResume(){
        super.onResume();
        new GetUserData().execute(ServerUtil.getPatientEndpoint(emailId));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        _mListView = (ListView) findViewById(R.id.drug_schedule);
        date = (TextView) findViewById(R.id.date);
        day = (TextView) findViewById(R.id.day);
        checkText = (CheckedTextView) findViewById(R.id.checkedTextView);
        DateFormat dayF = new SimpleDateFormat("EEE");
        DateFormat dateF = new SimpleDateFormat("MMM dd, yyyy");
        String now = dayF.format(new Date());
        date.setText(dateF.format(new Date()));
        day.setText(now);


        final Bundle extras = getIntent().getExtras();

        //Initialize all values
        jsonBody = extras.getString("jsonData");
        emailId = extras.getString("emailId");
        email= emailId;
        user_json = extras.getString("user_json");
        med_taken_times= extras.getString("mtimes");
        humdityAlert= extras.getString("halert");
        tempAlert= extras.getString("talert");
        pswd = extras.getString("pswd");
        new GetUserData().execute(ServerUtil.getPatientEndpoint(emailId));
        try {
            if (jsonBody.contains(med_taken_times+"X")) {
                checkText.setChecked(true);
            }
            else
                checkText.setChecked(false);
        }catch (NullPointerException e){
            Log.v("NUMBEROFMEDS","");
        }

        try {
            mdrugadapter = new DrugAdapter(getApplicationContext(), R.layout.listview, setDrugSchedule(jsonBody));
            if (_mListView != null && ds != null) {
                _mListView.setAdapter(mdrugadapter);
            }
            _mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    drugName = ds[i].getListData("drug_name");
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                    alertDialog.setTitle("Update");
                    alertDialog.setMessage(drugName);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Took",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Remove",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new RemoveUserData().execute(ServerUtil.getBaseEndpoint());
                                    new AuthenticationLogin().execute(ServerUtil.getUsersEndpoint(emailId, pswd));
                                    Intent login = new Intent(HomeActivity.this,LoginActivity.class);
                                    login.putExtra("load","true");
                                    login.putExtra("emailId", emailId);
                                    login.putExtra("pswd",pswd);
                                    startActivity(login);
                                }
                            });
                    alertDialog.show();
                    /*Log.v("DRUGSCHEDULE", ds[i].getListData("expdate"));
                    Intent ble = new Intent(view.getContext(), ConnectActivity.class);
                    ble.putExtra("mcount",med_taken_times);
                    ble.putExtra("emailId", emailId);
                    ble.putExtra("temp_alert", tempAlert);
                    ble.putExtra("humidity_alert", humdityAlert);
                    startActivity(ble);*/
                }
            });
        }catch (Exception e){
            e.printStackTrace();

        }

        _refresh = (ImageButton) findViewById(R.id.refresh);
        _refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetUserData().execute(ServerUtil.getPatientEndpoint(email));
                try {
                    if (jsonBody.contains(med_taken_times+"X")) {
                        checkText.setChecked(true);
                    }
                    else
                        checkText.setChecked(false);
                }catch (NullPointerException e){
                    Log.v("NUMBEROFMEDS","");
                }

            }
        });

        _addPrescription = (ImageButton) findViewById(R.id.prescription);
        _addPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pres = new Intent(view.getContext(), AddPrescription.class);
                pres.putExtra("user_json", user_json);
                startActivity(pres);
            }
        });

        _temperature = (ImageButton) findViewById(R.id.temperature);
        _temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent temperature= new Intent(view.getContext(), TemperatureActivity.class);
                temperature.putExtra("jsonData", jsonBody);
                temperature.putExtra("emailId", emailId);
                temperature.putExtra("user_json", user_json);
                temperature.putExtra("talert", tempAlert);
                temperature.putExtra("halert",humdityAlert);
                temperature.putExtra("mtimes",med_taken_times);
                startActivity(temperature);
            }
        });

        _humidity = (ImageButton) findViewById(R.id.humidity);
        _humidity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent humidity = new Intent(view.getContext(),HumidityActivity.class);
                humidity.putExtra("halert", humdityAlert);
                humidity.putExtra("talert", tempAlert);
                humidity.putExtra("emailId", emailId);
                humidity.putExtra("user_json", user_json);
                humidity.putExtra("jsonData", jsonBody);
                humidity.putExtra("mtimes",med_taken_times);
                startActivity(humidity);
            }
        });

        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(HomeActivity.this,LoginActivity.class );
                startActivity(login);
                finish();
            }
        });
    }

    private class GetUserData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder info = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    info.append(line);
                }
                rd.close();
                if (info.toString().contains("id")) {
                    jsonBody = info.toString();
                    setDrugSchedule(jsonBody);
                    url = new URL (ServerUtil.getBaseEndpoint()+"event/"+emailId);
                    getEvents(url);
                    return jsonBody;
                } else {
                    jsonBody = null;
                    return "";
                }
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class RemoveUserData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder info = new StringBuilder();
            try {
                URL url = new URL(urls[0]+"remove/"+emailId+"/"+drugName);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    info.append(line);
                }
                rd.close();
                if (info.toString().contains("id")) {
                    jsonBody = info.toString();
                    setDrugSchedule(jsonBody);
                    return jsonBody;
                } else {
                    jsonBody = null;
                    return "";
                }
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    }

    private DrugSchedule[] setDrugSchedule(String jsonBody) {
        if (jsonBody != null && jsonBody != "Testing") {
            try {
                JSONObject root = new JSONArray(jsonBody).getJSONObject(0);
                JSONArray jsArr;
                this.number_of_drugs = Integer.parseInt(root.get("number_of_drugs").toString());
                ds = new DrugSchedule[number_of_drugs];
                for (int i = 0; i < number_of_drugs; i++) {
                    ds[i] = new DrugSchedule();
                    jsArr = root.getJSONArray("smartcap" + i);
                    ds[i].setListData(jsArr);
                }
                return ds;


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
            }
        }
        this.ds = null;
        return ds;


    }

    private void getEvents(URL url){
        StringBuilder result = new StringBuilder();
        JSONObject parent;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            parent = new JSONObject(result.toString());
            med_taken_times= parent.getJSONObject("Event").getString("open_cap_event_times");
            tempAlert=  parent.getJSONObject("Event").getString("temp_alert");
            humdityAlert= parent.getJSONObject("Event").getString("humidity_alert");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class AuthenticationLogin extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            StringBuilder info = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                user_json= result.toString();
                rd.close();
                if (conn.getResponseCode() == 200) {
                    url = new URL(ServerUtil.getPatientEndpoint(emailId));
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = rd.readLine()) != null) {
                        info.append(line);
                    }
                    rd.close();
                    jsonData = info.toString();
                    getEvents(new URL(ServerUtil.getBaseEndpoint()+"event/"+emailId));
                    return "200";
                } else
                    return user_json;
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (result == "blank" || s == "200")
                    result = "Login Successful";
                else
                    result = "Login Failure. Try again" + user_json;
            } catch (NullPointerException e) {
                result = "Login Failed";
            }
        }
        private void getEvents(URL url){

            StringBuilder result = new StringBuilder();
            JSONObject parent;
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                parent = new JSONObject(result.toString());
                med_taken_times= parent.getJSONObject("Event").getString("open_cap_event_times");
                tempAlert=  parent.getJSONObject("Event").getString("temp_alert");
                humdityAlert= parent.getJSONObject("Event").getString("humidity_alert");


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
