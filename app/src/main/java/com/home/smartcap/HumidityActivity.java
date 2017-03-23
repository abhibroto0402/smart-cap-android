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
 * Created by amukherjee on 12/1/16.
 */

public class HumidityActivity extends AppCompatActivity {
    private TextView message;
    private ImageButton _home, _addPrescription,_temperature;
    private String jsonBody, emailId, user_json, halert, talert,mtimes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);
        message = (TextView) findViewById(R.id.message);

        //Initialize all values
        final Bundle extras = getIntent().getExtras();
        jsonBody = extras.getString("jsonData");
        emailId = extras.getString("emailId");
        user_json = extras.getString("user_json");
        halert = extras.getString("halert");
        talert = extras.getString("talert");
        mtimes= extras.getString("mtimes");
        if(Boolean.valueOf(halert) && halert!=null){
            message.setText("Humidity Spike Detected Today");
            int color = Color.parseColor("#ff0040");
            message.setBackgroundColor(color);
        }
        else{
            message.setText("No Humidity Spikes detected today");
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

            }
        });
        _temperature = (ImageButton) findViewById(R.id.temperature);
        _temperature.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent temperature = new Intent(view.getContext(),TemperatureActivity.class);
                temperature.putExtra("talert", talert);
                temperature.putExtra("emailId", emailId);
                temperature.putExtra("user_json", user_json);
                temperature.putExtra("jsonData", jsonBody);
                temperature.putExtra("halert",halert);
                temperature.putExtra("mtimes",mtimes);
                startActivity(temperature);
            }
        });

    }
}
