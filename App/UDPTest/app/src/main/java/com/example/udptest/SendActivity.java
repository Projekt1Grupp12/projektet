package com.example.udptest;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by George on 2017-04-13.
 */

public class SendActivity extends AsyncTask<String, String, String>
{

    private String ipAddress;
    private int server_port;
    private InetAddress server_ip;
    private boolean initialized;
    private DatagramPacket p0, p1;
    protected byte b1[] = new byte[100];

    private DatagramSocket s;




    public SendActivity(String ipAddress, int server_port) {
        this.ipAddress = ipAddress;
        this.server_port = server_port;
        this.initialized = false;
    }

    @Override
    protected void onPreExecute() {
        Log.i("SendActivity", "inside asyncTask onPreExecute");
        try {
            Log.i("DebugA", "this.server_ip = InetAddress.getByName(ipAddress)");
            this.server_ip = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
            String textSent = null;
            b1 = params[0].getBytes();

            //Constructs a datagram packet for sending packet (b1) of length (b1.length) to
            //the specified port number(server_port) on the specified host(server_ip).
            this.p0 = new DatagramPacket(b1, b1.length, server_ip, server_port);
                try {
                    //Initializes a DatagramSocket s.
                    this.s = new DatagramSocket();

                    //Connects the socket(s) to a remote address(server_ip, server_port). When a socket is connected to a remote address,
                    //packets may only be sent to or received from that address.
                    this.s.connect(server_ip, server_port);

                    //Sends a specified datagram packet from the socket(s).
                    this.s.send(this.p0);

                    //Close socket
                    this.s.close();
                } catch (SocketException e) {
                    //status.append("Error creating socket");
                    textSent.concat(" Error creating socket");   //this doesnt work!
                } catch (IOException e) {
                    //status.append("Error recieving packet");
                    textSent.concat(" Error recieving packet");  //this doesnt work!
                }
        textSent = String.valueOf(b1[0]);
        return textSent;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String str = s + " " ;
        Log.i("SendActivity", "onPostExecute " + str);

    }
}