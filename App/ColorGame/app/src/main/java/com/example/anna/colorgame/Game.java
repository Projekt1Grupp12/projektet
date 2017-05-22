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
 * Created by George on 2017-05-09.
 */

public class Game extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "debugGame";
    private static final String RED = "3";
    private static final String YELLOW = "2";
    private static final String GREEN = "1";
    private static final String SEMICOLON = ";";
    private String data = "";
    private Player player;
    private MediaPlayer mpGood;
    private MediaPlayer mpBad;
    private RecieveDataThread recieveDataThread;
    private Thread thread;

    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to update TextView in this activity.
         */
        @Override
        public void postResult(String result) {
            TextView textViewMove = (TextView) findViewById(R.id.textViewMove);
            Log.d(TAG, "Inside updateUI call 2");

            Log.d(TAG, "Text updated");
            //Music is played here.
            if(mpGood.isPlaying() || mpBad.isPlaying()) {
                mpGood.stop();
                mpBad.stop();
            }
            try {
                AssetFileDescriptor afd;
                if (result.contains("GOOD")) {
                    afd = getAssets().openFd("goodmove.mp3");
                    mpGood.reset();
                    mpGood.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
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
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    public void setPlayer(Player player){
        this.player = player;
        String toShow= "IP: " + player.getChoosenIP() + " Name: " + player.getName() + " UserID: " + player.getUserID();
        //Show information in TextView
        //TextView textViewIP = (TextView) findViewById(R.id.textViewIP);
        //textViewIP.setText(toShow);
    }

    public void activateMusic(){
        this.mpGood = MediaPlayer.create(this, R.raw.goodmove);
        this.mpBad = MediaPlayer.create(this, R.raw.badmove);
    }

    /**
     * This method is called when one of the buttons is clicked in the GUI.
     * Which button is pushed is stored in data variable.
     * Then the semicolon and userID is added to data string and sent to startAsyncTask method.
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
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
        startAsyncTask(temp, data);
    }

    /**
     * This method is used to create new instance of a ConnectToServer class and send data as
     * parameter to doInBackground method of that class.
     * @param color
     * @param data
     */
    public void startAsyncTask(String color, String data){
        Log.d(TAG, "Create task. " + color);
        ConnectToServer runner = new ConnectToServer(player.getChoosenIP(), delegate);
        System.gc();
        Log.d(TAG, "Execute task. " + color);
        runner.execute(data);
    }

    /**
     *  Handler and new thread
     */
    public void startThread(){
        recieveDataThread = new RecieveDataThread(this, player);
        thread = new Thread(recieveDataThread);
        thread.start();
    }
    public void closeReceiveThread(){//this method closes socket, ends run method and stops the thread
        recieveDataThread.setIsRunning(false);
        thread.interrupt();
    }

    public AsyncResponse getDelgate(){
        return this.delegate;
    }

    /**
     * Shows AlertDialog in PuzzleGame activity.
     */
    public void showAlertDialog(Context context){
        GameOver gameOver = new GameOver(context, this.player, "Give Up");

    }

    /**
     * This method is called when button "Give Up" is clicked.
     * @param view
     */
    public void giveUp(View view){
        showAlertDialog(this);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(this);
    }
}
