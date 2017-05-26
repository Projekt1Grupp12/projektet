package com.example.anna.colorgame;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
/**
 * This class is an game activity for Duel game that shows GUI specified for this game.
 * It has three buttons with different colors and a three TextView.
 * TextView shows different information to the user.(Move, points)
 */
public class DuelGame extends Game implements View.OnClickListener{
    private static final String TAG = "debugDuel";
    private MediaPlayer mpDuel = null;

    /**
     * In this method a TextView is created to show Game name to player. Next Intent is used to
     * get data send to this activity. Player variable is instantiated in the super class and
     * MediaPlayer variable is instantiated with files.
     * Thread is started to listen for incoming data from server.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main_layout);
        final TextView viewToChange = (TextView) findViewById(R.id.textPuzzleGame);
        viewToChange.setText("Duel Game");
        Log.d(TAG, "DuelGame, onCreate method. Creating an intent");
        Intent intent = getIntent();
        Player player = (Player) intent.getSerializableExtra("player");
        setPlayer(player);
        activateMusic();
        startThread();
        this.mpDuel = MediaPlayer.create(this, R.raw.rushing);
        try {
            AssetFileDescriptor afd;
                afd = getAssets().openFd("rushing.mp3");
            mpDuel.reset();
            mpDuel.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mpDuel.setLooping(true);
            mpDuel.prepare();
            mpDuel.start();
            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When this method is called mpDuel.stop() is called to stop music, closeThread() is called to
     * stop the thread and last super.onDestroy() is called to end this activity.
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mpDuel.stop();
        closeThread();
        super.onDestroy();
    }
}
