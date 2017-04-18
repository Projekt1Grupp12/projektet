package com.example.udptest;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
//AppCompatActivity is a base class for activities that use the support library action bar features.
//View.OnClickListener is an interface definition for a callback to be invoked when a view is clicked.
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    //WIFIManager class provides the primary API for managing all aspects of Wi-Fi connectivity
    WifiManager w;

    //InetAddress class represents an Internet Protocol (IP) address.
    //IP is a lower-level protocol on which protocols like UDP and TCP are built.
    InetAddress server_ip;
    private int server_port = 4444;
    private String ipAddress = "10.2.28.40";
    private byte[] message = new byte[1500];
    String statusText = null;
    private SendActivity send;
    private ReceiveActivity receive;


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
        //populateAutoComplete();
        //Initialise variables.
        status = (TextView) findViewById(R.id.status);
        mStatusView = (TextView) findViewById(R.id.message);
        w = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        
        //If WIFI is off, turn it on. Otherwise WIFI is on.
        if (!w.isWifiEnabled()) {
            status.setText("switching ON wifi ");
            w.setWifiEnabled(true);
        } else {
            status.setText("WiFi is already ON ");

        }
        //Initialise variable.
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

        //findViewById() returns view by ID and adds ClickListener to the view
        findViewById(R.id.send_UDP_button).setOnClickListener(this); // calling onClick() method


        Log.d("Phone IP", getIPAddress(true));
        send = new SendActivity(ipAddress, server_port);
        statusText = "First contact established...";
        send.execute(statusText);

        receive = new ReceiveActivity(ipAddress, server_port);
        receive.execute();
    }
    
    //Method inherited from View.OnClickListener and overriden.
    //Called when a view has been clicked.
    @Override
    public void onClick(View view) {
        send = new SendActivity(ipAddress, server_port);
        //Send data
        switch (view.getId()) {
                //If send_UDP button is pressed, call send_UDP_button method.
            case R.id.send_UDP_button:
                statusText = "0";
                if(!isEmpty(mIPView)) {
                    ipAddress = String.valueOf(mIPView.getText());
                }
                send.execute(statusText);
                //runUdpServer();
                break;
            case R.id.btn_Green://1
                statusText = "1";
                //runUdpServer();
                send.execute(statusText);
                break;
            case R.id.btn_Yellow://2
                statusText = "2";
                //runUdpServer();
                send.execute(statusText);
                break;
            case R.id.btn_Red://3
                statusText = "3";
                //runUdpServer();
                send.execute(statusText);
                break;
            default:
                Log.d("NO CASES MATCH", "onCklick event executed, something is wrong...");
                break;
        }
        //Wait for response from server
        receive = new ReceiveActivity(ipAddress, server_port);
        receive.execute();
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.gc();
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    //Update status of the view
    public void refreshStatus(View view)
        {
        mStatusView.setText(statusText);
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param //ipv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            //Creates a list of interfaces.
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            //For each interface in the list get an IP adress and place into list of InetAdress.
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                //For each  address in the list.
                for (InetAddress addr : addrs) {
                    //isLoopbackAddress() method returns true if the adress is the loopback address, false otherwise.
                    if (!addr.isLoopbackAddress()) {
                        //getHostAddress() returns string representation of IP adress.
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        //If useIPv4 is true and isIPv4 is true, return string representation of IPv4 adress.
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                            //Else return string representation of IPv6 adress.
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        //If adress is a loopback adress return empty string
        return "";
    }

}




