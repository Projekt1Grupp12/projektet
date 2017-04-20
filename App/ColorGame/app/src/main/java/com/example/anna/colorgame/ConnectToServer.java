package com.example.anna.colorgame;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Lenovo on 2017-04-20.
 */
 /*
    AsyncTask doInBackground sends data to server and receive response from server. Than it sends data to onPostExecute.
    onPostExecute recieve data from doInBackground and updates UI.
     */

public class ConnectToServer extends AsyncTask<String, String, String> {
    private static final int PORT = 4444;
    private static final String TAG = "debug";
    private String ip = null;
    private String userID = null;
    public AsyncResponse delegate = null;

    public ConnectToServer(String ip, String userID, AsyncResponse delegate){
        this.ip=ip;
        this.userID = userID;
        this.delegate = delegate;
    }
    @Override
    protected String doInBackground(String... message) {
        String messageFromServer = "";
        try {
            Log.d(TAG, "Creating Socket and IPAdress");
            DatagramSocket clientSocket = new DatagramSocket(PORT);
            //clientSocket.setSoTimeout(1000);
            InetAddress IPAddress = InetAddress.getByName(ip);

            Log.d(TAG, "Initialising byte arrays");
            byte[] sendData = new byte[24];
            byte[] receiveData = new byte[24];
            String sentence = message[0];

            Log.d(TAG, "Sending user input to server");
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
            clientSocket.send(sendPacket);

            Log.d(TAG, "Recieving response from server");
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            Log.d(TAG, "Waiting for response");
            clientSocket.receive(receivePacket);
            Log.d(TAG, "Reading DatagramPacket we got from server");
            // modifiedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
            Log.d(TAG, "FROM SERVER:" + messageFromServer);
            messageFromServer = putChar(receiveData, receiveData.length);
            clientSocket.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return messageFromServer;
    }
    @Override
    protected void onPostExecute(String result){
        delegate.postResult(result);
      /*  GameFrame gf = new GameFrame();
        Log.d(TAG, "Inside onPostExecute" + result);
        gf.updateUI(result);
        Log.d(TAG, "After updateUI call");*/
    }

    private String putChar(byte[] receiveData, int l){
        String tmp = "";
        for(int i=0; i<l; i++) {
            if (receiveData[i] != 0) {
                tmp += (char) receiveData[i];
            }
        }
        return tmp;
    }

}
