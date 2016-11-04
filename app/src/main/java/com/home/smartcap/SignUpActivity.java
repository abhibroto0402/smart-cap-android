package com.home.smartcap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.home.smartcap.R.id.emailid;

/**
 * Created by amukherjee on 10/30/16.
 */

public class SignUpActivity extends AppCompatActivity{
    private TextView gotoLogin = null;
    private EditText sign_name= null;
    private EditText sign_email= null;
    private EditText sign_password= null;
    private Button create_account= null;
    private String emailId, pswd, user_name;
    private String post_result="blank";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);

            gotoLogin= (TextView) findViewById(R.id.gotologin);
            sign_name = (EditText) findViewById(R.id.name) ;
            sign_email= (EditText) findViewById(emailid) ;
            sign_password = (EditText) findViewById(R.id.password) ;
            create_account = (Button) findViewById(R.id.createAccount) ;


            gotoLogin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), LoginActivity.class);
                    startActivity(i);
                }
            });

            create_account.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    user_name= sign_name.getText().toString();
                    emailId= sign_email.getText().toString();
                    pswd= sign_password.getText().toString();
                    boolean isValid = false;


                    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                    CharSequence inputStr = emailId;
                    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(inputStr);
                    if (matcher.matches()) {
                        isValid = true;
                    }
                    if(isValid){
                        String tempUrl= "https://smartcap-abhibroto0402.c9users.io/user/";
                        new CreateAccount().execute(tempUrl);
                        showMessage();
                        Intent i = new Intent(view.getContext(), LoginActivity.class);
                        startActivity(i);
                    }
                    else
                        throwError("Error in email Format");
                }
            });
        }

    private void throwError(String error){
        Toast.makeText(this,error, Toast.LENGTH_SHORT).show();
    }

    private void showMessage(){
        if(post_result!="blank")
            Toast.makeText(this, post_result,Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Account Created",Toast.LENGTH_SHORT).show();
    }

    private class CreateAccount extends AsyncTask<String, String, String>{


        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            JSONObject parent = new JSONObject();
            try {
                parent.put("user_name",user_name);
                parent.put("email",emailId);
                parent.put("password",pswd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL (params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.connect();
                OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
                wr.write(parent.toString());
                wr.close();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                if(conn.getResponseCode()==200)
                    return "200";
                else
                    return "400";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (post_result=="blank" || s.equals("200") )
                    post_result = "Account Creation Successful";
                else
                    post_result = "Account Creation Failed";
            }catch (NullPointerException e){
                post_result = "Could not create account. Try Again";
            }
        }

    }
}
