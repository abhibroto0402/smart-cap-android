package com.home.smartcap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    //Declaration of objects on the login screen

    private Button mlogin = null;
    private TextView _signupLink;
    private EditText emailid;
    private EditText password;
    private String emailId, pswd;
    private boolean isValid=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialization of all elements
        _signupLink = (TextView) findViewById(R.id.link_signup);
        mlogin = (Button) findViewById(R.id.login);
        emailid = (EditText) findViewById(R.id.emailid);
        password= (EditText) findViewById(R.id.password);

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
                emailId= emailid.getText().toString();
                pswd= password.getText().toString();
                isValid = false;
                String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                CharSequence inputStr = emailId;
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputStr);
                if (matcher.matches()) {
                    isValid = true;
                }
                if(isValid){
                    String tempUrl= "https://smartcap-abhibroto0402.c9users.io/user/"+emailId+"/"+pswd;
                    new AuthenticationLogin().execute(tempUrl);
                }
                else
                    throwError("Error in email Format");


            }

        });

    }
    private void throwError(String error){
        Toast.makeText(this,error, Toast.LENGTH_SHORT).show();
    }
}
