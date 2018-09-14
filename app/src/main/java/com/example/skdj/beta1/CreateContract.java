package com.example.skdj.beta1;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;
import static com.example.skdj.beta1.MainActivity.dest_latitude;
import static com.example.skdj.beta1.MainActivity.dest_longitude;
import static com.example.skdj.beta1.MainActivity.phno;
import static com.example.skdj.beta1.MainActivity.strtLan;
import static com.example.skdj.beta1.MainActivity.strtLong;

public class CreateContract  extends AsyncTask<URL, Void, String> {

    SharedPreferences prefs;
    String MY_PREFS_NAME = "Start";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        Log.i("contract","done");
    }

    @Override
    protected String doInBackground(URL... urls) {
        try {


            String phno1 = phno;
            Double clat = strtLan;
            Double clon = strtLong;
            Double dlat = dest_latitude;
            Double dlon = dest_longitude;
            URL url = urls[0]; // here is your URL path

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //  Toast.makeText(getApplicationContext(),restoredText,Toast.LENGTH_LONG).show();
            String s="phoneNumber="+phno1+"&lat0="+clat+"&lon0="+clon+"&lat1="+dlat+"&lon1="+dlon+"&starttime="+"123";
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(s);

            writer.flush();
            writer.close();
            os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                InputStream is = null;
                try {
                    is = conn.getInputStream();
                    int ch;
                    StringBuffer sb = new StringBuffer();
                    while ((ch = is.read()) != -1) {
                        sb.append((char) ch);
                    }
                    return sb.toString();
                } catch (IOException e) {
                    throw e;
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }

            }
            else {
                return new String("false : "+responseCode);
            }
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }
}
