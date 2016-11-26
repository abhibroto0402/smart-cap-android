package com.home.smartcap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by amukherjee on 11/26/16.
 */

public class TemperatureActivity extends AppCompatActivity {

    private TextView bleText;
    private Button sendSMS;
    private ImageButton _home, _addPrescription;
    private String jsonBody, emailId, user_json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        bleText = (TextView) findViewById(R.id.bleTest);
        sendSMS =(Button) findViewById(R.id.sendsms);
        //Initialize all values
        final Bundle extras = getIntent().getExtras();
        jsonBody = extras.getString("jsonData");
        emailId = extras.getString("emailId");
        user_json = extras.getString("user_json");


        sendSMS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendSMS().execute(ServerUtil.getBaseEndpoint()+"twilio");
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

        _home = (ImageButton) findViewById(R.id.timer);
        _home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(view.getContext(), HomeActivity.class);
                home.putExtra("emailId", emailId);
                home.putExtra("user_json", user_json);
                home.putExtra("jsonData", jsonBody);
                startActivity(home);
            }
        });

    }

    private class SendSMS extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            StringBuilder info = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    info.append(line);
                }
                rd.close();
                return info.toString();


            } catch (Exception e) {
                return "";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            bleText.setText(s);

        }

    }
}
