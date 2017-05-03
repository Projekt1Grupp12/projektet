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
                                Log.d(TAG, "runOnUiThread, run method");
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("You won!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Start new AlertDialog
                                        secondAlertDialog();

                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.create();
                                builder.show();
                            }
                        });
                    }else if(messageFromServer.equals("LOSE!")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "runOnUiThread, run method");
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("You lost!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Start new AlertDialog
                                        secondAlertDialog();

                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.create();
                                builder.show();
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

    private void secondAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to play Again ?");

        //YES button listener
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Start new activity ChooseGAme
                Intent intent = new Intent(context, MainMenu.class);
                intent.putExtra("player", player);
                Log.d(TAG, "Starting new Activity");
                context.startActivity(intent);

                dialogInterface.dismiss();
            }
        });

        //NO button listener
        builder.setNeutralButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Start new activity MainFrame
                Intent intent = new Intent(context, MainFrame.class);
                intent.putExtra("player", player);
                Log.d(TAG, "Starting new Activity");
                context.startActivity(intent);

                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }
}
