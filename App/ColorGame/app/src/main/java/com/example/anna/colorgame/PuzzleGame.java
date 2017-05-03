package com.example.anna.colorgame;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;


/*
Class is an activity that represents GUI of the game.
It has three buttons with different colors and a three TextView.
TextView shows different information to the user.(Move, points)
 */
public class PuzzleGame extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "debug";
    private static final String RED = "3";
    private static final String YELLOW = "2";
    private static final String GREEN = "1";
    private static final String SEMICOLON = ";";
    private String data = "";
    private Player player;
    private MediaPlayer mpGood;
    private MediaPlayer mpBad;


    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to update TextView in this activity.
         */
        @Override
        public void postResult(String result) {
            TextView textViewMove = (TextView) findViewById(R.id.textViewMove);
            Log.d(TAG, "Inside PuzzleGame");
            textViewMove.setText(result);
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
                } else if (result.contains("BAD")) {
                    afd = getAssets().openFd("badmove.mp3");
                    mpBad.reset();
                    mpBad.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                    mpBad.prepare();
                    mpBad.start();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_game);

        //Get the intent that started this activity and extract the string
        Log.d(TAG, "PuzzleGame, onCreate method. Creating an intent");
        Intent intent = getIntent();
        player = (Player) intent.getSerializableExtra("player");
        String toShow= "IP: " + player.getChoosenIP() + " Name: " + player.getName() + " UserID: " + player.getUserID();

        //Show information in TextView
        TextView textViewIP = (TextView) findViewById(R.id.textViewIP);
        textViewIP.setText(toShow);

        //Initiate audioclips
        mpGood = MediaPlayer.create(PuzzleGame.this, R.raw.goodmove);
        mpBad = MediaPlayer.create(PuzzleGame.this, R.raw.badmove);

        //Handler and new thread
        Log.d(TAG, "PuzzleGame, onCreate method. Creating a thread");
        RecieveDataThread recieveDataThread = new RecieveDataThread(this, player);
        Thread thread = new Thread(recieveDataThread);
        thread.start();
    }

    /*
    This method is called when one of the buttons is clicked in the GUI.
    Which button is pushed is stored in data variable.
    Then the semicolon and userID is added to data string and sent to startAsyncTask method.
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

    /*
    This method is used to create new instance of a ConnectToServer class and send data as
    parameter to doInBackground method of that class.
     */
    public void startAsyncTask(String color, String data){
        Log.d(TAG, "Create task.PuzzleGame " + color);
        ConnectToServer runner = new ConnectToServer(player.getChoosenIP(), delegate);
        System.gc();
        Log.d(TAG, "Execute task.PuzzleGame " + color);
        runner.execute(data);
    }
}
