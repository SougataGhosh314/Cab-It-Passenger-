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
                Log.d("Phone", pno1);
                Communicate();
            }
        });
    }


    public void Communicate() {
        Toast.makeText(this, "Updated ", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = getSharedPreferences(new MainActivity().MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.putString("phoneno", pno1);
        editor.apply();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        super.onBackPressed();
       // new SendPostRequest().execute();
    }
        private class SendPostRequest extends AsyncTask<String, Void, String> {

            protected void onPreExecute(){}

            protected String doInBackground(String... arg0) {

                return  arg0[0];
                }
            @Override
            protected void onPostExecute(String result) {

            }
        }

}
