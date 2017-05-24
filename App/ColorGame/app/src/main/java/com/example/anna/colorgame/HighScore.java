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
                closeProgressDialog();
                showAlertDialog(HighScore.this, "Error fetching highscore", "Try again later.", "HighScore");
            }
            else if (result != null) {
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

    @Override
    protected void onStop() {
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
        mpHighScore.stop();
        super.onBackPressed();
    }

    /**
     *This method is used to show specified AlertDialog in HighScore activity.
     * @param thisClass
     * @param title
     * @param message
     * @param dataMessage
     */
    private void showAlertDialog(HighScore thisClass, String title, String message, String dataMessage) {
        AlertDialogClass alertDialog = new AlertDialogClass(thisClass, title, message, dataMessage);
        alertDialog.ButtonOK();
    }
}
