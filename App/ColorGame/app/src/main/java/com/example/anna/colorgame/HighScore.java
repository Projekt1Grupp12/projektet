package com.example.anna.colorgame;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;

public class HighScore extends SuperActivity {
    private String TAG = "debugHighScore";
    private boolean isRunning = true;
    private MediaPlayer mpHighScore;
    private AsyncResponse delegate = new AsyncResponse() {

        /*

        Method that has result from AsyncTask as parameter.

        It is used to get result from Asynctask and store it in userID variable.

        And at last sendMessageToNextActivity method is called.

         */

        @Override

        public void postResult(String result) {
            TextView textViewMove = (TextView) findViewById(R.id.textViewMove);
            Log.d(TAG, "RESULTAT FRÅN SERVER " + result);
            if (result.contains("SocketTimeoutException")) {
                showAlertDialog("Connections lost", "No connection to the Server. Try again later.");
            }
            else if (result != null) {
                textViewMove.setText(result);
                Log.d(TAG, "Text updated");
                //Music is played here.
                if (mpHighScore.isPlaying()) {
                    mpHighScore.stop();
                }
                setThread(new Thread() {
                    public void run() {
                        while (isRunning) {
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
                });
                getThread().start();
            } else {
                showAlertDialog("There is no cake", "The cake is a lie!");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        Intent intent = getIntent();
        setPlayer((Player) intent.getSerializableExtra("player"));
        setAlertDialog(new AlertDialogClass(this));
        this.mpHighScore = MediaPlayer.create(this, R.raw.goodmove);
        startAsyncTask("highscore;" + getPlayer().getUserID(), getPlayer(), delegate);//using new class
    }

    @Override
    protected void onStop() {
        isRunning = false;
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        startNextActivity(getPlayer(), this, MainMenu.class);
    }
}
