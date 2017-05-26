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

/**
 * This class is an activity that shows high score to user. First request to server is send asking
 * for high score data. If server respond with data, then it is shown on the screen. If server
 * doesn't respond to request AlertDialog is shown to inform user about the error.
 * Music is playing while this activity is active.
 */
public class HighScore extends SuperActivity {
    private String TAG = "debugHighScore";
    private MediaPlayer mpHighScore;
    /**
     * This is used to get result from AsyncTask. In this case result is data with high score.
     * Depending on result different actions are taken.
     */
    private AsyncResponse delegate = new AsyncResponse() {
        @Override
        public void postResult(String result) {
            TextView textViewMove = (TextView) findViewById(R.id.textViewMove);
            Log.d(TAG, "RESULTAT FRÅN SERVER " + result);
            if (result.contains("SocketTimeoutException")) {
                closeProgressDialog();
                showAlertDialog(HighScore.this, "Error fetching highscore", "Try again later.", "HighScore");
            } else if (result != null) {
                closeProgressDialog();
                textViewMove.setText(result);
                Log.d(TAG, "Text updated");
                //Music is played here.
                setThread(new Thread() {
                    public void run() {
                        try {
                            AssetFileDescriptor afd = getAssets().openFd("intotheinfinitybeat.mp3");
                            mpHighScore.stop();
                            mpHighScore.reset();
                            mpHighScore.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            mpHighScore.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mpHighScore.setLooping(true);
                            mpHighScore.prepare();
                            mpHighScore.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                getThread().start();
            }
        }
    };

    /**
     * This method is called when activity is created. Intent is instantiated to get data sent by
     * previous activity.
     * MediaPlayer object is instantiated with file and specified data is sent to server by calling
     * sendDataToServer() method.
     * ProgressDialog is created and shown by calling startProgressDialog() method.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        Intent intent = getIntent();
        setPlayer((Player) intent.getSerializableExtra("player"));
        this.mpHighScore = MediaPlayer.create(this, R.raw.intotheinfinitybeat);
        sendDataToServer("highscore;" + getPlayer().getUserID(), getPlayer(), delegate);//using new class
        startProgressDialog("Fetching highscore list...", this);
    }

    /**
     * This method is called when user push "UP" button on the screen.
     * onBackPressed() method is called to show AlertDialog.
     *
     * @param item MenuItem
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    /**
     * This method is called when user pushes "Back" button on the screen. mpHighScore.stop() is
     * called to stop music.
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        mpHighScore.stop();
        super.onBackPressed();
    }

    /**
     * This method is used to show specified AlertDialog in HighScore activity.
     *
     * @param thisClass   HighScore class
     * @param title       String
     * @param message     String
     * @param dataMessage String
     */
    private void showAlertDialog(HighScore thisClass, String title, String message, String dataMessage) {
        AlertDialogClass alertDialog = new AlertDialogClass(thisClass, title, message, dataMessage);
        alertDialog.ButtonOK();
    }
}
