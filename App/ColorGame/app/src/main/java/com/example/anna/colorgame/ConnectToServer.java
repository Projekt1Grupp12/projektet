package com.example.anna.colorgame;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
 /*
This class extends AsyncTask and is used to send a request with data to server, and get a response
with data from server.
Parameter in doInBackground method is data that will be sent to the server.
*/

public class ConnectToServer extends AsyncTask<String, String, String> {
    private static final int PORT = 4444;
    private static final String TAG = "ServerConnectDebug";
    private String ip = null;
    private AsyncResponse delegate = null;

    public ConnectToServer(String ip, AsyncResponse delegate){
        this.ip=ip;
        this.delegate = delegate;
    }
    /*
    This method is used to estabilish connection with server.
    Parameter is data that will be sent to the server.
    It returns data received from the server to onPostExecute method.
     */
    @Override
    protected String doInBackground(String... message) {
        String messageFromServer = "";
        DatagramSocket clientSocket = null;
        try {
            Log.d(TAG, "Asynctask. Creating Socket and IPAdress");
            clientSocket = new DatagramSocket(PORT);
            InetAddress IPAddress = InetAddress.getByName(ip);

            Log.d(TAG, "Asynctask. Initialising byte arrays");
            byte[] sendData = new byte[24];
            byte[] receiveData = new byte[24];
            String sentence = message[0];

            Log.d(TAG, "Asynctask. Sending user input to server");
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
            clientSocket.send(sendPacket);

            Log.d(TAG, "Asynctask. Recieving response from server");
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            Log.d(TAG, "clientSocket.setSoTimeout(3000)");

            if(sentence.contains("ready?")) {
                clientSocket.setSoTimeout(3000);
            }
            else {
                clientSocket.setSoTimeout(3000);
            }
            Log.d(TAG, "Asynctask. Waiting for response");
            clientSocket.receive(receivePacket);


            Log.d(TAG, "Asynctask. Reading DatagramPacket we got from server");
            messageFromServer = putChar(receiveData, receiveData.length);
        } catch (SocketTimeoutException e) {
            if(messageFromServer.isEmpty())
                Log.d(TAG, "EMPTY MESSAGE");
            messageFromServer = "SocketTimeoutException";
            Log.e(TAG, "SocketTimeoutException");
        }catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        clientSocket.close();
        return messageFromServer;
    }
    /*
    This method is called right after doInBackground method is complete.
    This method uses instance of AsyncResponce to call postResult method and send data stored in
    result as parameter.
     */
    @Override
    protected void onPostExecute(String result){
        delegate.postResult(result);
    }
    /*
    This method is used to sort data received from server to sort away all of the "0".
     */
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
