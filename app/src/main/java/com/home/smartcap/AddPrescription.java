package com.home.smartcap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by amukherjee on 11/13/16.
 */

public class AddPrescription extends AppCompatActivity {
    private TextView add;
    private JSONObject jsonBody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prescription);
        add= (TextView) findViewById(R.id.add);
        final Bundle presExt = getIntent().getExtras();
        try {
            jsonBody= new JSONObject(presExt.getString("user_json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonBody.remove("_id");
    }
}
