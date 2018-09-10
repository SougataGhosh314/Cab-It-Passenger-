package com.example.skdj.beta1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class Update extends AppCompatActivity{

    EditText fullName, phoneNumber_1;
    String name, pno1;
    Button updateButton;
    int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        flag=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);

        fullName = (EditText) findViewById(R.id.fullName);
        phoneNumber_1 = (EditText) findViewById(R.id.phoneNumber_1);
        updateButton = (Button) findViewById(R.id.updateButton);

        SharedPreferences prefs = getSharedPreferences(new MainActivity().MY_PREFS_NAME, MODE_PRIVATE);

        String prefname = prefs.getString("name", null);
        String prefpno1=prefs.getString("phoneno", null);

       // Log.d("sharedpref=", ""+prefname+",  "+prefvno);
        fullName.setText(prefname);
        phoneNumber_1.setText(prefpno1);


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name= fullName.getText().toString();
                pno1= phoneNumber_1.getText().toString();
                Communicate();
            }
        });
    }


    public void Communicate() {
        Toast.makeText(this, "Updated ", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
        //new SendPostRequest().execute();
    }
        private class SendPostRequest extends AsyncTask<String, Void, String> {

            protected void onPreExecute(){}

            protected String doInBackground(String... arg0) {

                try {

                    URL url = new URL("http://51.15.38.26/cgi-bin/Pune/DriverRegister/DriverRegister.out"); // here is your URL path


                    String tosend="XYZ";
                    tosend=tosend.replace(" ", "+");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(tosend);

                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                         flag=1;
                        flag=1;

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
                        Log.d("update","false : "+responseCode);
                        return new String("false : "+responseCode);
                    }
                }
                catch(Exception e){
                    Log.d("update","Exception: " + e.getMessage());
                    return new String("Exception: " + e.getMessage());
                }

            }

            @Override
            protected void onPostExecute(String result) {
                if(flag==1) {
                    SharedPreferences.Editor editor = getSharedPreferences(new MainActivity().MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("name", name);
                    editor.putString("phoneno1", pno1);
                    editor.apply();
                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"error, tryagain",Toast.LENGTH_SHORT).show();
                }
            }
        }

}
