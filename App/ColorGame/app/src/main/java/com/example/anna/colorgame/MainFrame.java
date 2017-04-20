package com.example.anna.colorgame;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MainFrame extends AppCompatActivity {
    private static final String TAG = "debug";
    private static final int PORT = 4444;
    private String ip = "";
    private String name = "";
    private String userID = "";
    private AsyncResponse delegate = new AsyncResponse() {
        @Override
        public void postResult(String result) {
            //updating userID variable with the ID we got from the server
            Log.d(TAG, "RESULTAT FRÅN SERVER " + result);
            userID = result;
            sendMessageToNextActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);
    }

    /*
    This method is called when the button is clicked
     */
    public void sendMessage(View view) {//Button
        updateIPName();

        ConnectToServer runner = new ConnectToServer(ip, userID, delegate);
        Log.d(TAG, "Task created");
        String messageToServer = "" + name;//we want to send name to server
        Log.d(TAG, "Execute task");
        runner.execute(messageToServer);
    }

    /*
   This method updates ip and name variable
    */
    public void updateIPName() {
        EditText editIPText = (EditText) findViewById(R.id.editIPText);
        EditText editNameText = (EditText) findViewById(R.id.editNameText);
        this.ip = "10.2.19.28";
        //this.ip = editIPText.getText().toString();
       this.name = "george";
       // this.name = editNameText.getText().toString();
    }

    /*
     This method send data to next activity
      */
    public void sendMessageToNextActivity() {
        Log.d(TAG, "Creating new intent");
        Intent intent = new Intent(this, StartGameFrame.class);
        Log.d(TAG, "IP and NAME" + ip + " " + name + " " + userID);
        intent.putExtra("ip", ip);
        intent.putExtra("name", name);
        intent. putExtra("userid", userID);
        Log.d(TAG, "Starting new Activity");
        startActivity(intent);
    }

}


