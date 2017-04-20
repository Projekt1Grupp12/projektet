package com.example.anna.colorgame;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class GameFrame extends AppCompatActivity implements View.OnClickListener {
    private static final int PORT = 4444;
    private static final String TAG = "debug";
    private static final String RED = "3";
    private static final String YELLOW = "2";
    private static final String GREEN = "1";
    private static final String COMMA = ";";
    private String data = "";
    private String ip = "";
    //private String name = "";
    private String userID = "";
    private AsyncResponse delegate = new AsyncResponse() {
        @Override
        public void postResult(String result) {
           TextView textViewMove = (TextView) findViewById(R.id.textViewMove);
            Log.d(TAG, "Inside updateUI call 2");
            textViewMove.setText(result);
            Log.d(TAG, "Text updated");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_frame);

        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        String name = intent.getStringExtra("name");
        userID = intent.getStringExtra("userid");
        String toShow= "ip: " + ip + "name: " + name + "userID: " + userID;

        //Capture the layout's TextView and set the string as its text
        TextView textViewIP = (TextView) findViewById(R.id.textViewIP);
        textViewIP.setText(toShow);
    }
/*
    public void redButtonCLicked(View view){//RED button
        data = RED + COMMA + userID;

        //If this button is pushed, send content in str to AsyncTask.doInBackground
        startAsyncTask("RED", data);
    }

    public void yellowButtonClicked(View view){//YELLOW button
        data = YELLOW + COMMA + userID;

        //If this button is pushed, send content in str to AsyncTask.doInBackground
        startAsyncTask("YELLOW", data);
    }

    public void greenButtonClicked(View view){//GREEN button
        data = GREEN + COMMA + userID;

        //If this button is pushed, send content in str to AsyncTask.doInBackground
        startAsyncTask("GREEN", data);
    }
    */

    public void startAsyncTask(String color, String data){
        //If this button is pushed, send content in str to AsyncTask.doInBackground

        ConnectToServer runner = new ConnectToServer(ip, userID, delegate);
        System.gc();
        Log.d(TAG, "Task created. " + color);
        //we want to send name to server
        Log.d(TAG, "Execute task. " + color);

        runner.execute(data);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.greenButton:
                data = GREEN;
                break;
            case R.id.yellowButton:
                data = YELLOW;
                break;
            case R.id.redButton:
                data = RED;
                break;
        }
        String temp = data;
        data += COMMA + userID;
        startAsyncTask(temp, data);
    }
    /*public void updateUI(String result){
        Log.d(TAG, "Inside updateUI call 1");
        TextView textViewMove = new TextView(setContentView(R.layout.activity_game_frame));
        textViewMove = (TextView) findViewById(R.id.textViewMove);
        Log.d(TAG, "Inside updateUI call 2");
        textViewMove.setText(result);
        Log.d(TAG, "Text updated");
    }*/
}
