package com.home.smartcap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by amukherjee on 11/3/16.
 */

public class HomeActivity extends AppCompatActivity {

    private TextView tv;// TODO - Need to make this ListView
    private ImageButton _refresh;
    private String jsonBody, emailId, user_json;
    private ImageButton _addPrescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*TODO Need to change it to ListView */

        this.tv = (TextView) findViewById(R.id.jsonbody);
        final Bundle extras = getIntent().getExtras();
        //Initialize all values
        jsonBody = extras.getString("jsonData");
        emailId = extras.getString("emailId");
        user_json= extras.getString("user_json");
        if (jsonBody != null && jsonBody != "Testing") {
            tv.setText(jsonBody);
        } else {
            tv.setText("Error Occurred");
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
                if(info.toString().contains("id")) {
                    jsonBody = info.toString();
                    return "200";
                }
                else{
                    jsonBody = "Record Not Found. Please add prescription";
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
                if(s=="200"){
                    tv.setText(jsonBody);
                }
                else{
                    tv.setText("Record Not Found. Please add prescription");
                }

            } catch (NullPointerException e) {

            }
        }
    }
}
