package com.home.smartcap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by amukherjee on 10/30/16.
 */

public class HomeActivity extends AppCompatActivity{
    private Button gotoLogin = null;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            gotoLogin= (Button) findViewById(R.id.gotologin);

            gotoLogin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), LoginActivity.class);
                    startActivity(i);
                }
            });
        }
}
