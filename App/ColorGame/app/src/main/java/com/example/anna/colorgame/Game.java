package com.example.anna.colorgame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

/**
 * This class is a super class for every game in this application. It is created to store variables
 * and methods shared by all game classes.
 */
public class Game extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "debugGame";
    private static final String GREEN = "1";
    private static final String YELLOW = "2";
    private static final String RED = "3";
    private static final String SEMICOLON = ";";
    private String data = "";
    private Player player;
    private MediaPlayer mpGood;
    private MediaPlayer mpBad;
    private ReceiveDataThread receiveDataThread;
    private Thread thread;

    /**
     * This is used to get result from AsyncTask.
     * Different actions are taken depending on the result.
     */
    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to update TextView in this activity.
         */
        @Override
        public void postResult(String result) {
            TextView textViewMove = (TextView) findViewById(R.id.textViewMove);
            //Music is played here.
            if (mpGood.isPlaying() || mpBad.isPlaying()) {
                mpGood.stop();
                mpBad.stop();
            }
            try {
                AssetFileDescriptor afd;
                if (result.contains("GOOD")) {
                    afd = getAssets().openFd("goodmove.mp3");
                    mpGood.reset();
                    mpGood.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mpGood.prepare();
                    mpGood.start();
                    textViewMove.setText(result);
                } else if (result.contains("BAD")) {
                    afd = getAssets().openFd("badmove.mp3");
                    mpBad.reset();
                    mpBad.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mpBad.prepare();
                    mpBad.start();
                    textViewMove.setText(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * This method is used to initiate a player instance with specified value.
     *
     * @param player Player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * This method instantiates MediaPlayer instances with specified values.
     */
    public void activateMusic() {
        this.mpGood = MediaPlayer.create(this, R.raw.goodmove);
        this.mpBad = MediaPlayer.create(this, R.raw.badmove);
    }

    /**
     * This method is called when one of the buttons is clicked in the GUI.
     * Which button is pushed is stored in data variable.
     * Then the semicolon and userID is added to data string and sent to sendDataToServer method.
     *
     * @param view View
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.greenButton:
                data = GREEN;
                break;
            case R.id.yellowButton:
                data = YELLOW;
                break;
            case R.id.redButton:
                data = RED;
                break;
        }
        String temp = data;
        data += SEMICOLON + player.getUserID();
        sendDataToServer(temp, data);
    }

    /**
     * This method is used to create new instance of a ConnectToServer class and send data as
     * parameter to doInBackground method of that class.
     *
     * @param color String
     * @param data  String
     */
    public void sendDataToServer(String color, String data) {
        Log.d(TAG, "Create task. " + color);
        ConnectToServer runner = new ConnectToServer(player.getChoosenIP(), delegate);
        System.gc();
        Log.d(TAG, "Execute task. " + color);
        runner.execute(data);
    }

    /**
     * This method is used to start a thread that listens for incoming data from server.
     */
    public void startThread() {
        receiveDataThread = new ReceiveDataThread(this, player);
        thread = new Thread(receiveDataThread);
        thread.start();
    }

    /**
     * This method is used to close the thread. It stops while loop and interrupts the thread.
     */
    public void closeThread() {
        receiveDataThread.setIsRunning(false);
        thread.interrupt();
    }

    /**
     * This method is used to show AlertDialog in game activity.
     * It is called when GiveUp button is pushed.
     */
    public void showAlertDialog(Context context) {
        new GameOver(context, this.player, "Give Up");

    }

    /**
     * This method is called when button "Give Up" is clicked.
     *
     * @param view View
     */
    public void giveUp(View view) {
        showAlertDialog(this);
    }

    /**
     * This method is called when user navigates using Back button on the screen.
     * AlertDialog is shown by calling showAlertDialog() method.
     */
    @Override
    public void onBackPressed() {
        showAlertDialog(this);
    }
}
