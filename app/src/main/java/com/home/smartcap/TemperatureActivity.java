package com.home.smartcap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by amukherjee on 11/26/16.
 */

public class TemperatureActivity extends AppCompatActivity {

    private TextView message;
    private ImageButton _home, _addPrescription,_humidity;
    private String jsonBody, emailId, user_json, halert, talert,mtimes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        message = (TextView) findViewById(R.id.message);

        //Initialize all values
        final Bundle extras = getIntent().getExtras();
        jsonBody = extras.getString("jsonData");
        emailId = extras.getString("emailId");
        user_json = extras.getString("user_json");
        halert = extras.getString("halert");
        talert = extras.getString("talert");
        mtimes= extras.getString("mtimes");
        if(Boolean.valueOf(talert)){
            message.setText("Temperature Spike Detected Today");
            int color = Color.parseColor("#ff0040");
            message.setBackgroundColor(color);
        }
        else{
            message.setText("No Temperature Spikes detected today");
            int color = Color.parseColor("#77fd04");
            message.setBackgroundColor(color);
        }

        _addPrescription = (ImageButton) findViewById(R.id.prescription);
        _addPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pres = new Intent(view.getContext(), AddPrescription.class);
                pres.putExtra("user_json", user_json);
                startActivity(pres);
                finish();
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
                home.putExtra("mtimes",mtimes);
                startActivity(home);
                finish();
            }
        });
        _humidity = (ImageButton) findViewById(R.id.humidity);
        _humidity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent humidity = new Intent(view.getContext(),HumidityActivity.class);
                humidity.putExtra("halert", halert);
                humidity.putExtra("talert", talert);
                humidity.putExtra("emailId", emailId);
                humidity.putExtra("user_json", user_json);
                humidity.putExtra("jsonData", jsonBody);
                startActivity(humidity);
                finish();
            }
        });

    }

}
