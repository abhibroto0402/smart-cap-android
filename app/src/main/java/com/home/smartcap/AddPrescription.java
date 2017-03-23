package com.home.smartcap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amukherjee on 11/13/16.
 */

public class AddPrescription extends AppCompatActivity {
    private Button add;
    private JSONObject jsonBody;
    private EditText medname, sc_id, exp_date;
    private RadioGroup meal, dosage;
    private RadioButton meal_btn, dosage_btn;
    private String home_disp, org_user;
    private ImageButton _home, _addPrescription,_humidity;
    private String  emailId, user_json, halert, talert,mtimes;
    private JSONObject createJsonBody (JSONObject jsonBody, List<String> _smartcap) throws JSONException {

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i=0; i<_smartcap.size();i++){
            sb.append("\"");
            sb.append(_smartcap.get(i));
            sb.append("\"");
            sb.append(",");
        }
        sb.append("]");
        String temp = sb.toString();
        temp= temp.replace(",]","]");
        jsonBody.put("smartcap", new JSONArray(temp));
        return jsonBody;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prescription);
        add= (Button) findViewById(R.id.add);
        sc_id=(EditText)findViewById(R.id.smartcapnum);
        medname = (EditText)findViewById(R.id.medname);
        exp_date=(EditText)findViewById(R.id.expdata);
        meal =(RadioGroup) findViewById(R.id.meal);
        dosage = (RadioGroup) findViewById(R.id.dosage);

        /*
            Remove the _ID value
         */
        final Bundle presExt = getIntent().getExtras();
        try {
            org_user=presExt.getString("user_json");
            jsonBody= new JSONObject(org_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonBody.remove("_id");

        /*
            When Submit button obtain radio button information
         */
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> _smartcap= new ArrayList<String>();
                 _smartcap.add(sc_id.getText().toString());
                _smartcap.add(medname.getText().toString());
                int selectedId = dosage.getCheckedRadioButtonId();
                dosage_btn= (RadioButton) findViewById(selectedId);
                String dos= dosage_btn.getText().toString();
                switch (dos){
                    case "Once":
                        _smartcap.add("1X");
                        break;
                    case "Twice":
                        _smartcap.add("2X");
                        break;
                    case "Thrice":
                        _smartcap.add("3X");
                }
                selectedId = meal.getCheckedRadioButtonId();
                meal_btn= (RadioButton) findViewById(selectedId);
                _smartcap.add(meal_btn.getText().toString());
                String exp = exp_date.getText().toString();
                _smartcap.add(exp.replace("-","/"));
                try {
                    jsonBody= createJsonBody(jsonBody,_smartcap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new UploadPrescriptionApp().execute(ServerUtil.PATIENT_ENDPOINT+"appSubmit");
                Intent intent = new Intent(AddPrescription.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("jsonData",home_disp);
                intent.putExtra("user_json",org_user);
                try {
                    intent.putExtra("emailId",jsonBody.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                finish();
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
        _home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(view.getContext(), HomeActivity.class);
                home.putExtra("emailId", emailId);
                home.putExtra("user_json", user_json);
                home.putExtra("jsonData", (Serializable) jsonBody);
                home.putExtra("mtimes",mtimes);
                startActivity(home);
            }
        });
        _humidity = (ImageButton) findViewById(R.id.humidity);
        _humidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent humidity = new Intent(view.getContext(),HumidityActivity.class);
                humidity.putExtra("halert", halert);
                humidity.putExtra("talert", talert);
                humidity.putExtra("emailId", emailId);
                humidity.putExtra("user_json", user_json);
                humidity.putExtra("jsonData", (Serializable) jsonBody);
                humidity.putExtra("mtimes",mtimes);
                startActivity(humidity);
            }
        });
    }

    public class UploadPrescriptionApp extends AsyncTask<String, String,String>{

        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            StringBuilder info = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.connect();
                OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
                wr.write(jsonBody.toString());
                wr.close();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                url = new URL(ServerUtil.getPatientEndpoint(jsonBody.getString("email")));
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = rd.readLine()) != null) {
                    info.append(line);
                }
                rd.close();
                home_disp = info.toString();
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(AddPrescription.this, s,Toast.LENGTH_SHORT).show();
        }
    };
}
