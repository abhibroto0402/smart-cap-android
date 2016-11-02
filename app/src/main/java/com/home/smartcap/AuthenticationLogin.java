package com.home.smartcap;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by amukherjee on 11/2/16.
 */

public class AuthenticationLogin extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... urls) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL (urls[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return conn.getResponseMessage()+conn.getResponseCode();
        }catch(Exception e){
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);
    }
}
