package com.example.anna.colorgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import android.os.Handler;

/*
This class is a thread that is used in PuzzleGame class(Activity).
After it is started it waits for a specified message from server.
When thread recieve message from the server it shows an AlertDialog to the user.
 */
public class RecieveDataThread implements Runnable {
    private static final String TAG = "debug";
    private static final int PORT = 4445;
    private final Handler handler;
    private Context context;
    private Player player;

    /*
    This constructor creates new instance of a class with context as parameter.
    It initiate context and handler instances.
     */
    public RecieveDataThread(Context context, Player player){
        this.player=player;
        this.context=context;
        handler = new Handler(context.getMainLooper());
    }

    /*
    This method contains code that makes it possible to wait for a message from server and
    show AlertDialog.
    First udp socket is created that is supposed to listen for incoming data on port 6666 in an
    infinite while loop.
     */
    @Override
    public void run() {
        Log.d(TAG, "RecieveDataThread, run method");
        DatagramSocket mySocket = null;
        DatagramPacket receivePacket = null;
        byte[] receiveData = null;

        try{
            Log.d(TAG, "Thread. Creating Socket");
            mySocket = new DatagramSocket(PORT);
            mySocket.setReuseAddress(true);
        }catch(SocketException e){
            e.printStackTrace();
        }

            while(true) {
                try {
                    Log.d(TAG, "Thread. Initialising byte array and DatagramPacket");
                    receiveData = new byte[24];
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);

                    Log.d(TAG, "Thread. Waiting for message from server");
                    mySocket.receive(receivePacket);

                    Log.d(TAG, "Thread. Reading DatagramPacket we got from server");
                    String messageFromServer = putChar(receiveData, receiveData.length);



                    /*
                    Here must be a if-else for different messages in AlertDialog frame
                    depending on message from server.
                     */
                    if (messageFromServer.equals("WIN!")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "runOnUiThread, run method WIN");
                                GameOver gameOver = new GameOver(context, player, "WIN!");
                            }
                        });
                    }else if(messageFromServer.equals("LOSE!")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "runOnUiThread, run method LOSE");
                                GameOver gameOver = new GameOver(context, player, "LOSE!");
                            }
                        });
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
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

    /*
    This method is used to update UI in main thread (PuzzleGame Activity).
     */
    private void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }

}
