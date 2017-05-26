package com.example.anna.colorgame;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * This class extends AsyncTask and is used to send a request with data to server, and to get a
 * response with data from server.
 * DatagramSocket is used for communication with server. Data is sent in DatagramPackets.
 */
public class ConnectToServer extends AsyncTask<String, String, String> {
    private static final int PORT = 4444;
    private static final String TAG = "ServerConnectDebug";
    private String ip = null;
    private AsyncResponse delegate = null;
    private int constructor = 0;

    /**
     * This constructor creates an instance of ConnectToServer class and initiates it with specified
     * values.
     *
     * @param ip       String
     * @param delegate AsyncResponse
     */
    public ConnectToServer(String ip, AsyncResponse delegate) {
        this.constructor = 1;
        this.ip = ip;
        this.delegate = delegate;
    }

    /**
     * This constructor creates an instance of ConnectToServer class and initiates it with specified
     * values
     *
     * @param player Player
     */
    public ConnectToServer(Player player) {
        this.constructor = 2;
        this.ip = player.getChoosenIP();
    }

    /**
     * This method is used to send specified data message to server and wait for response. If server
     * doesn't respond in 3 seconds SocketTimeOutException is cast to inform about bad connection or
     * connection loss.
     *
     * @param message String
     * @return result String
     */
    @Override
    protected String doInBackground(String... message) {
        String messageFromServer = "";
        DatagramSocket clientSocket = null;
        try {
            Log.d(TAG, "Asynctask. Creating Socket and IPAdress");
            clientSocket = new DatagramSocket(PORT);
            InetAddress IPAddress = InetAddress.getByName(ip);
            byte[] sendData;
            byte[] receiveData = new byte[128];
            String sentence = message[0];
            Log.d(TAG, "SKICKAT TILL SERVER:" + sentence);
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.setSoTimeout(5000);
            Log.d(TAG, "Asynctask. Waiting for response");
            clientSocket.receive(receivePacket);
            Log.d(TAG, "Asynctask. Reading DatagramPacket we got from server");
            messageFromServer = putChar(receiveData, receiveData.length);
        } catch (SocketTimeoutException e) {
            if (messageFromServer.isEmpty())
                Log.d(TAG, "EMPTY MESSAGE");
            messageFromServer = "SocketTimeoutException";
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientSocket.close();
        return messageFromServer;
    }

    /**
     * This method is called right after doInBackground method is complete.
     * It uses instance of AsyncResponce to call postResult method and send data stored in result as
     * parameter.
     *
     * @param result String
     */
    @Override
    protected void onPostExecute(String result) {
        if (constructor == 1) {
            delegate.postResult(result);
        } else if (constructor == 2) {
            Log.d(TAG, "result" + result);
        } else if (constructor == 0) {
            Log.d(TAG, "result" + result);
        }
    }

    /**
     * This method is used to sort data received from server, specifically to sort away all of the
     * "0".
     *
     * @param receiveData byte[]
     * @param legth           int
     * @return messageFromServer String
     */
    private String putChar(byte[] receiveData, int legth) {
        String messageFromServer = "";
        for (int i = 0; i < legth; i++) {
            if (receiveData[i] != 0) {
                messageFromServer += (char) receiveData[i];
            }
        }
        return messageFromServer;
    }
}
