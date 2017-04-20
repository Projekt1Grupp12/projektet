package com.example.udptest;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.net.wifi.WifiManager;

/**
 * A login screen that offers login via email/password.
 */
//AppCompatActivity is a base class for activities that use the support library action bar features.
//View.OnClickListener is an interface definition for a callback to be invoked when a view is clicked.
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //WIFIManager class provides the primary API for managing all aspects of Wi-Fi connectivity
    WifiManager w;

    private String ipAddress = "10.2.19.28"; //Server IP-address
    private int server_port = 4444; //Server portnumber

    String message = null;

    private SendActivity send;
    private ReceiveActivity receive;

    MobileInfo mInfo; //used to get the phones IP-address

    private boolean hasPlayerID = false;
    private static int playerID;
    private AsyncResponse asyncDelegate  = new AsyncResponse() {
        @Override
        public void postResult(String asyncresult) {
            Log.i("ASYNC_RESULT", asyncresult);
            if(!hasPlayerID){
                hasPlayerID = true;
                try {
                    playerID = Integer.parseInt(asyncresult);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    hasPlayerID = false;
                    Log.i("NumberFormatException", e.getMessage());
                }
            }
        }
    };

    private EditText mIPView;
    private TextView status;
    private TextView mStatusView;

    //AsyncTask class allows to perform background operations and publish results on the UI thread
    //without having to manipulate threads and/or handlers.
    private AsyncTask<Void, Void, Void> async_udp;
    private boolean Server_Active = true;

    //A method is called when activity is to be created. It has a main window with TextView, EditText.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise variables.
        status = (TextView) findViewById(R.id.status);
        mStatusView = (TextView) findViewById(R.id.message);
        mIPView = (EditText) findViewById(R.id.ip);
        mIPView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.ip || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

        //If WIFI is off, turn it on. Otherwise WIFI is on.
        w = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        turnOnWiFi(w);

        //findViewById() returns view by ID and adds ClickListener to the view
        findViewById(R.id.send_UDP_button).setOnClickListener(this); // calling onClick() method

        mInfo = new MobileInfo();
        Log.d("Phone IP", mInfo.getIPAddress(true));
        send = new SendActivity(ipAddress, server_port);
        message = "First contact established...";
        send.execute("0");
        //execute the async task
        ReceiveActivity receive = new ReceiveActivity(ipAddress, server_port, asyncDelegate);
        //Receives a 0 as first response from server.
        //Assign the AsyncTask's delegate to your class's context (this links your asynctask and this class together)
        //receive.delegate = this;
        if(!hasPlayerID) {
            receive.execute();
        }
        Log.i("onCreate", "FINISHED");
    }

    private void turnOnWiFi(WifiManager w){
        if (!w.isWifiEnabled()) {
            status.setText("switching ON wifi ");
            w.setWifiEnabled(true);
        } else {
            status.setText("WiFi is already ON ");

        }
    }

    //Method inherited from View.OnClickListener and overriden.
    //Called when a view has been clicked.
    @Override
    public void onClick(View view) {
        send = new SendActivity(ipAddress, server_port);
        String message = "";
        //Send data
        switch (view.getId()) {
            //If send_UDP button is pressed, call send_UDP_button method.
            case R.id.send_UDP_button:
                message = "0";
                if(!isEmpty(mIPView)) {
                    ipAddress = String.valueOf(mIPView.getText());
                }
                send.execute(message);
                break;
            case R.id.btn_Green://1
                message = "1";
                send.execute(message);
                break;
            case R.id.btn_Yellow://2
                message = "2";
                send.execute(message);
                break;
            case R.id.btn_Red://3
                message = "3";
                send.execute(message);
                break;
            default:
                Log.d("NO CASES MATCH", "onCklick event executed, something is wrong...");
                break;
        }
        //Wait for response from server
        receive = new ReceiveActivity(ipAddress, server_port, asyncDelegate);
        receive.execute();

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       System.gc(); //Garbage collector
    }

    private boolean isEmpty(EditText setText) {
        if (setText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
}




