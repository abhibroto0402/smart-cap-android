package com.home.smartcap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by amukherjee on 11/3/16.
 */

public class HomeActivity extends AppCompatActivity {

    private ImageButton _refresh;
    private String jsonBody, emailId, user_json;
    private ImageButton _addPrescription;
    private ListView _mListView;
    private DrugAdapter mdrugadapter;
    private int number_of_drugs;
    private DrugSchedule[] ds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        _mListView = (ListView) findViewById(R.id.drug_schedule);


        final Bundle extras = getIntent().getExtras();

        //Initialize all values
        jsonBody = extras.getString("jsonData");
        emailId = extras.getString("emailId");
        user_json = extras.getString("user_json");

        try {
            mdrugadapter = new DrugAdapter(getApplicationContext(), R.layout.listview, setDrugSchedule(jsonBody));
            if (_mListView != null && ds != null) {
                _mListView.setAdapter(mdrugadapter);
            }
            _mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.v("DRUGSCHEDULE", ds[i].getListData("expdate"));
                }
            });
        }catch (NullPointerException e){

        }

        _refresh = (ImageButton) findViewById(R.id.refresh);
        _refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetUserData().execute(ServerUtil.getPatientEndpoint(emailId));
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
            try {

                if (s != "") {
                    mdrugadapter = new DrugAdapter(getApplicationContext(), R.layout.listview, setDrugSchedule(s));
                    if (_mListView != null && ds != null) {
                        _mListView.setAdapter(mdrugadapter);
                    }
                    _mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.v("DRUGSCHEDULE", ds[i].getListData("expdate"));
                        }
                    });

                }

            } catch (NullPointerException e) {
            }
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

}
