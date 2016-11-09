package com.home.smartcap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    //Declaration of objects on the login screen

    private Button mlogin = null;
    private TextView _signupLink;
    private EditText emailid;
    private EditText password;
    public String emailId, pswd;
    private String jsonData = "Testing";
    private boolean isValid = false;
    private String result = "blank";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialization of all elements
        _signupLink = (TextView) findViewById(R.id.link_signup);
        mlogin = (Button) findViewById(R.id.login);
        emailid = (EditText) findViewById(R.id.emailid);
        password = (EditText) findViewById(R.id.password);

        //Events for Object click
        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), SignUpActivity.class);
                startActivity(i);
            }
        });

        //Event of login
        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailId = emailid.getText().toString();
                pswd = password.getText().toString();
                isValid = false;
                String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                CharSequence inputStr = emailId;
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputStr);
                if (matcher.matches()) {
                    isValid = true;
                }
                if (isValid) {
                    String tempUrl = ServerUtil.getUsersEndpoint(emailId,pswd);
                    for(int i = 0; i<2 ;i++)
                        new AuthenticationLogin().execute(tempUrl);
                    showMessage();
                    if (jsonData != "Testing") {
                        Intent i = new Intent(view.getContext(), HomeActivity.class);
                        i.putExtra("jsonData", jsonData);
                        i.putExtra("emailId", emailId);
                        startActivity(i);
                    }
                } else
                    throwError("Error in email Format");

            }

        });

    }

    private void throwError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void showMessage() {
        if (result != "blank")
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
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
                    return "200";
                } else
                    return result.toString();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (result == "blank" || s == "200")
                    result = "Login Successful";
                else
                    result = "Login Failure. Try again";
            } catch (NullPointerException e) {
                result = "Login Failed";
            }
        }
    }
}
