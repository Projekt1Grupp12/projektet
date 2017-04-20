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

public class SendActivity extends AsyncTask<String, String, String>
{

    private String ipAddress;
    private int server_port;
    private InetAddress server_ip;

    protected byte buffer[] = new byte[100];
    private DatagramPacket packet;
    private DatagramSocket s;

    /**
     * Constructor that takes the server IP-address and portnumber
     * as arguments. The input arguments are used to establish a
     * connection between the client and the server
     * @param  ipAddress
     * @param server_port
     */
    public SendActivity(String ipAddress, int server_port) {
        this.ipAddress = ipAddress;
        this.server_port = server_port;
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
            String textSent = null;
            //Text message to be sent is stored in the variable buffer
            buffer = params[0].getBytes();

            //Constructs a datagram packet for sending packet (b1) of length (b1.length) to
            //the specified port number(server_port) on the specified host(server_ip).
            this.packet = new DatagramPacket(buffer, buffer.length, server_ip, server_port);
                try {
                    //Initializes a DatagramSocket s.
                    this.s = new DatagramSocket();

                    //Connects the socket(s) to a remote address(server_ip, server_port). When a socket is connected to a remote address,
                    //packets may only be sent to or received from that address.
                    this.s.connect(server_ip, server_port);

                    //Sends a specified datagram packet from the socket(s).
                    this.s.send(this.packet);

                    //Close socket
                    this.s.close();
                } catch (SocketException e) {
                    //status.append("Error creating socket");
                    textSent.concat(" Error creating socket");   //this doesnt work!
                } catch (IOException e) {
                    //status.append("Error recieving packet");
                    textSent.concat(" Error recieving packet");  //this doesnt work!
                }
        textSent = String.valueOf(buffer[0]);
        return textSent;
    }
}