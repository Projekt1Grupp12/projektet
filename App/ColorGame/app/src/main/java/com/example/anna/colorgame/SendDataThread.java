package com.example.anna.colorgame;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Created by Lenovo on 2017-05-15.
 */

public class SendDataThread {
    private static final String TAG = "debugSend";
    private static final int PORT = 4445;
    private final Handler handler;
    private Context context;
    private Player player;
    private DatagramSocket mySocket;
    private String message;
    private byte[] sendData;
    private boolean connetionSuccess = false;

    /*
    This constructor creates new instance of a class with context as parameter.
    It initiate context and handler instances.
     */
    public SendDataThread(Context context, Player player, String message) {
        this.player = player;
        this.context = context;
        this.message = message;
        handler = new Handler(context.getMainLooper());
    }

    public void run() {
        DatagramSocket clientSocket = null;
        try {
            Log.d(TAG, "Creating Socket and IPAdress");
            clientSocket = new DatagramSocket(PORT);
            InetAddress IPAddress = InetAddress.getByName(player.getChoosenIP());
            sendData = new byte[24];
            Log.d(TAG, "SKICKAT TILL SERVER:" + message);
            sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
            clientSocket.send(sendPacket);
            connetionSuccess = clientSocket.isConnected();//if socked succesfully connected to server return true
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "SocketTimeoutException");
        }catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        clientSocket.close();
    }
}
