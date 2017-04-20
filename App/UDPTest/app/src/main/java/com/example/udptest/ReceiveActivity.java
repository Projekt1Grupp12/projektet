package com.example.udptest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by George on 2017-04-13.
 */

class ReceiveActivity extends AsyncTask<String, String, String>{

    private String ipAddress;
    private int server_port;
    private int client_port = 4444;
    private InetAddress server_ip;

    protected byte buffer[] = new byte[100];
    private DatagramPacket packet;
    private DatagramSocket s;

    public AsyncResponse delegate = null;


    /**
     * Constructor that takes the server IP-address and portnumber
     * as arguments. The input arguments are used to establish a
     * connection between the client and the server
     * @param ipAddress
     * @param server_port
     */
    public ReceiveActivity(String ipAddress, int server_port, AsyncResponse delegate) {
        this.ipAddress = ipAddress;
        this.server_port = server_port;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        try {
            this.server_ip = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected String doInBackground(String... params) {
        String textReceived = "";
        //Constructs a datagram packet for sending packet (b1) of length (b1.length) to
        //the specified port number(server_port) on the specified host(server_ip).
        this.packet = new DatagramPacket(buffer, buffer.length);
        try {
            //Initializes a DatagramSocket s.
            this.s = new DatagramSocket(client_port);

            //Times out if no data is received within the specefied time in millis
            //s.setSoTimeout(5000);
            //Connects the socket(s) to a remote address(server_ip, server_port). When a socket is connected to a remote address,
            //packets may only be sent to or received from that address.
            this.s.connect(server_ip, server_port);

            //Sends a specified datagram packet from the socket(s).
            this.s.receive(this.packet);

            //Close socket
            this.s.close();
        } catch (SocketException e) {
            //status.append("Error creating socket");
            textReceived.concat(" Error creating socket");   //this doesnt work!
        } catch (IOException e) {
            //status.append("Error recieving packet");
            textReceived.concat(" Error recieving packet");  //this doesnt work!
        }
        textReceived = putTogether(buffer, 16);
        return textReceived;
    }

    @Override
    protected void onPostExecute(String result) {
        if(delegate!=null)
        {
            delegate.postResult(result);
        }
        else
        {
            Log.e("ApiAccess", "You have not assigned IApiAccessResponse delegate");
        }
    }

    /**
     *  Assembles a string from the received datapackage from the server
     *  by putting the string together char by char.
     * @param t
     * @param l
     * @return String
     */
    public String putTogether(byte[] t, int l) {
        String tmp = "";
        for(int i = 0; i < l; i++) {
            if((char)t[i] != 0)
                tmp += (char)t[i];
            else
                break; //no more useful inforation in the datapackage
        }
        return tmp;
    }


}
