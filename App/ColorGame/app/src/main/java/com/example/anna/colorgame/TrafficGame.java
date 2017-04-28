package com.example.anna.colorgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class TrafficGame extends AppCompatActivity {
    private static final String TAG = "debug";
    private static String ip = "";
    private static String name = "";
    private static String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traffic_game);

        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        Log.i(TAG, "onCreate()");
        ip = intent.getStringExtra("ip");
        name = intent.getStringExtra("name");
        userID = intent.getStringExtra("userid");
    }
}