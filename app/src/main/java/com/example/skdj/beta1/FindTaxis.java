package com.example.skdj.beta1;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class FindTaxis extends AsyncTask<URL, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
           new MainActivity().gotTaxis(s);

    }

    @Override
    protected String doInBackground(URL... urls) {
        try {
           
            String response=null;
            Log.d("task", "In tasss");
            double lan= MainActivity.strtLan;
            double lon=MainActivity.strtLong;
            Log.d("task", lan+"  "+ lon);
            URL url = urls[0]; // here is your URL path

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //  Toast.makeText(getApplicationContext(),restoredText,Toast.LENGTH_LONG).show();

            String s="lat="+lan+"&lon="+lon;
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
