package com.home.smartcap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by amukherjee on 11/3/16.
 */

public class HomeActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.tv = (TextView) findViewById(R.id.jsonbody);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString("jsonData") != "Testing") {
            tv.setText(extras.getString("jsonData"));
        } else {
            tv.setText("Error Occurred");


        }
    }
}
