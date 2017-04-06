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
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    WifiManager w;
    TextView status;
    InetAddress server_ip;
    int server_port = 4444;
    private String ipAddress = "192.168.0.12";
    private byte[] message = new byte[1500];
    String statusText;
    String text;

    private EditText  mIPView;
    private TextView  mStatusView;


    private AsyncTask<Void, Void, Void> async_udp;
    private boolean Server_Active = true;



    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("A", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //populateAutoComplete();

        status = (TextView) findViewById(R.id.ip);

        w = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        if (!w.isWifiEnabled()) {
            status.setText("switching ON wifi ");
            w.setWifiEnabled(true);
        } else {
            status.setText("Its already ON ");

        }


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
        mStatusView =  (TextView) findViewById(R.id.message);

        findViewById(R.id.send_UDP_button).setOnClickListener(this); // calling onClick() method
        findViewById(R.id.button_begin_graph).setOnClickListener(this); // calling onClick() method
        findViewById(R.id.button_refresh_status).setOnClickListener(this); // calling onClick() method

    }
    @Override
    public void onClick(View view) {
        //Send data
        switch (view.getId()) {
            case R.id.send_UDP_button:
                ipAddress = String.valueOf(mIPView.getText());
                Log.d("IP-address", String.valueOf(ipAddress));
                message[0] = 'H';
                message[0] = 'e';
                message[0] = 'l';
                message[0] = 'l';
                message[0] = 'o';
                message[0] = '?';
                DatagramPacket p = new DatagramPacket(message, message.length);
                DatagramSocket s = null;
                try {
                    s = new DatagramSocket(server_port);
                    s.receive(p);
                } catch (SocketException e) {
                    e.printStackTrace();
                    Log.d("SocketException", "----------------");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("IOException", "----------------");
                }
                text = new String(message, 0, p.getLength());
                Log.d("Udp tutorial", "message:" + text);
                s.close();
                break;
            case R.id.button_begin_graph:
                Log.d("runUdpServer", "Running server");
                runUdpServer();
                break;
            case R.id.button_refresh_status:
                Log.d("BUTTON REFRESH", "button_refresh_status");
                refreshStatus(mIPView);
                break;
        default:
            Log.d("NO BUTTON", "and still onClick event ecevuted, something wrong...");
            break;
        }
    }


    public void refreshStatus(View view)
    {
        status.setText(statusText);
    }

    public void runUdpServer()
    {

        int x;
        WifiInfo info = w.getConnectionInfo();
        status.setText(" ");
        status.append("\n\nWiFi Status: " + info.toString());

        x = info.getIpAddress();
        String str1 = info.getMacAddress();

        status.append("\n\nmac address===" + str1 + "  ,ip===" + x);

        try {
            server_ip = InetAddress.getByName(ipAddress); // ip of THE OTHER DEVICE - NOT THE PHONE

        } catch (UnknownHostException e) {
            status.append("Error at fetching inetAddress");
        }

        async_udp = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                String str2 = "TEST MESSAGE !!!";
                byte b1[];
                b1 = new byte[100];
                b1 = str2.getBytes();
                //DatagramPacket p1 = new DatagramPacket(b1, b1.length, server_ip, server_port);


                try {
                    //DatagramSocket s = new DatagramSocket(server_port, server_ip);
                    DatagramSocket s = new DatagramSocket(server_port);
                    s.connect(server_ip, server_port);

                    //DatagramPacket p0 = new DatagramPacket(b1, b1.length, InetAddress.getByName("192.168.43.xxx"), server_port);
                    //s.send(p0);
                    //The above two line can be used to send a packet - the other code is only to recieve

                    DatagramPacket p1 = new DatagramPacket(b1,b1.length);
                    s.receive(p1);

                    s.close();
                    b1=p1.getData();
                    String str = new String( b1);

                    server_port = p1.getPort();
                    server_ip=p1.getAddress();



                    String str_msg = "RECEIVED FROM CLIENT IP =" + server_ip + " port=" + server_port + " message no = " + b1[0] +
                            " data=" + str.substring(1);  //first character is message number
                    //WARNING: b1 bytes display as signed but are sent as signed characters!

                    //status.setText(str_msg);
                    statusText = str_msg;

                } catch (SocketException e)
                {
                    //status.append("Error creating socket");
                    statusText.concat(" Error creating socket");   //this doesnt work!
                } catch (IOException e)
                {
                    //status.append("Error recieving packet");
                    statusText.concat(" Error recieving packet");  //this doesnt work!
                }


                return null;
            }
        };

        if (Build.VERSION.SDK_INT >= 11)
        {
            async_udp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            async_udp.execute();
        }
        status.setText(statusText); //need to set out here, as above is in an async thread

    }
}




