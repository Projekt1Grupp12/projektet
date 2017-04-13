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
    private String ipAddress = "10.2.19.28";
    private byte[] message = new byte[1500];
    String statusText = null;
    String text;

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
    }
    
    //Method inherited from View.OnClickListener and overriden.
    //Called when a view has been clicked.
    @Override
    public void onClick(View view) {
        //Send data
        switch (view.getId()) {
                //If send_UDP button is pressed, call send_UDP_button method.
            case R.id.send_UDP_button:
                statusText = "0";
                if(!isEmpty(mIPView)) {
                    ipAddress = String.valueOf(mIPView.getText());
                    statusText = ipAddress;
                }
                runUdpServer();
                break;
            case R.id.btn_Green://1
                statusText = "1";
                runUdpServer();
                break;
            case R.id.btn_Yellow://2
                statusText = "2";
                runUdpServer();
                break;
            case R.id.btn_Red://3
                statusText = "3";
                runUdpServer();
                break;
            default:
                Log.d("NO CASES MATCH", "onCklick event executed, something is wrong...");
                break;
        }
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
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


    //Update status of the view
    public void refreshStatus(View view)
        {
        mStatusView.setText(statusText);
    }

    public void runUdpServer()
    {
        int x;
        //getConnectionInfo() return dynamic information about the current Wi-Fi connection.
        WifiInfo info = w.getConnectionInfo();
        mStatusView.setText(" ");

        //Add information to status variable.
       // status.append("\n\nWiFi Status: " + info.toString());

        //getIpAddress() method returns int representation of IP.
        x = info.getIpAddress();

        //getMacAddress() returns string representation of mac adress.
        //String str1 = info.getMacAddress();

        //Add information to status variable.
       // status.append("\n\nmac address===" + str1 + "  ,ip===" + x);

        try {
            server_ip = InetAddress.getByName(ipAddress); // ip of THE OTHER DEVICE - NOT THE PHONE

        } catch (UnknownHostException e) {
            Log.d("UnknownHostException", "debug");
            status.append("Error at fetching inetAddress");
        }

        //async_udp will execute whatever you put in doInBackground on a background thread with the given parameters.
        async_udp = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                byte b1[] = new byte[100];
                b1 = statusText.getBytes();
                //Constructs a datagram packet for sending packet (b1) of length (b1.length) to
                //the specified port number(server_port) on the specified host(server_ip). 
                //Constructs a datagram packet for receiving packet (b1) of length (b1.length) from specified host
                DatagramPacket p1 = new DatagramPacket(b1, b1.length);


                try {
                    //Creates a DatagramSocket s.
                    DatagramSocket s = new DatagramSocket();

                    //Connects the socket(s) to a remote address(server_ip, server_port). When a socket is connected to a remote address, 
                    //packets may only be sent to or received from that address.
                    s.connect(server_ip, server_port);

                    //Constructs a datagram packet for sending packet (b1) of length (b1.length) to
                    //the specified port number(server_port) on the specified host(InetAddress.getByName(ipAddress)).
                    DatagramPacket p0 = new DatagramPacket(b1, b1.length, InetAddress.getByName(ipAddress), server_port);

                    //Sends a specified datagram packet from the socket(s).
                    s.send(p0);
                    //Receives a specified datagram packet from the socket(s).
                    Log.d("RECEIVE" , String.valueOf(p1.getData()[0]));
                    Log.d("RECEIVE" , "pre");
                    s.receive(p1);
                    Log.d("RECEIVE" , "post");
                    Log.d("RECEIVE" , String.valueOf(p1.getData()[0]));
                    //Close socket
                    s.close();

                    b1 = p1.getData();

                    statusText = String.valueOf(b1[0]);
                } catch (SocketException e) {
                    //status.append("Error creating socket");
                    statusText.concat(" Error creating socket");   //this doesnt work!
                } catch (IOException e) {
                    //status.append("Error recieving packet");
                    statusText.concat(" Error recieving packet");  //this doesnt work!
                }
                return null;
            }
        };
        status.append(statusText);


        if(Build.VERSION.SDK_INT >=11)

            {
                async_udp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        else

            {
                async_udp.execute();
            }
        mStatusView.setText(statusText); //need to set out here, as above is in an async thread

        }

    public String hostInfor(byte b1[]){
        String str = new String(b1);
        //Creates a String that holds server_ip, server_port, message number and data
        String str_msg = "RECEIVED FROM CLIENT IP =" + server_ip + " port=" + server_port + " message no = " + b1[0] +
                " data=" + str.substring(1);  //first character is message number
        //WARNING: b1 bytes display as signed but are sent as signed characters!
        return str_msg;
    }

}




