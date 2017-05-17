package com.example.anna.colorgame;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class HighScore extends AppCompatActivity {


    private String TAG = "debugHighScore";
    private Player player = null;
    private AlertDialogClass alertDialog = null;

    private MediaPlayer mpHighScore;
    private Thread thread = null;

    private AsyncResponse delegate = new AsyncResponse() {

        /*

        Method that has result from AsyncTask as parameter.

        It is used to get result from Asynctask and store it in userID variable.

        And at last sendMessageToNextActivity method is called.

         */

        @Override

        public void postResult(String result) {
            TextView textViewMove = (TextView) findViewById(R.id.textViewMove);
            Log.d(TAG, "Inside updateUI call 2");

            Log.d(TAG, "RESULTAT FRÅN SERVER " + result);

            if(result != null) {
                textViewMove.setText(result);
                Log.d(TAG, "Text updated");
                //Music is played here.
                if (mpHighScore.isPlaying()) {
                    mpHighScore.stop();
                }

                thread = new Thread() {
                    public void run() {
                        while (true) {
                            try {
                                AssetFileDescriptor afd = getAssets().openFd("goodmove.mp3");
                                mpHighScore.stop();
                                mpHighScore.reset();
                                mpHighScore.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                                mpHighScore.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mpHighScore.prepare();
                                mpHighScore.start();
                                sleep(1000);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();

            }else if(result.contains("SocketTimeoutException")){
                alertDialog.setTitleMessage("Connections lost", "No connection to the Server. Try again later.");
                alertDialog.ButtonOK();
            }
            else{
                alertDialog.setTitleMessage("There is no cake", "The cake is a lie!");
                alertDialog.ButtonOK();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        player = (Player)intent.getSerializableExtra("player");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alertDialog = new AlertDialogClass(this);
        this.mpHighScore = MediaPlayer.create(this, R.raw.goodmove);
        String message = "highscore;" + player.getUserID();
        startAsyncTask(message);
    }


    public void startAsyncTask(String message) {

        ConnectToServer runner = new ConnectToServer(player.getChoosenIP(), delegate);

        Log.d(TAG, "Task created");

        runner.execute(message);

    }
}
