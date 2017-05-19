package com.example.anna.colorgame;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class DuelGame extends Game implements View.OnClickListener{

    private static final String TAG = "debugDuel";
    private Player player;
    private MediaPlayer mpDuel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.game_main_layout);
        final TextView viewToChange = (TextView) findViewById(R.id.textPuzzleGame);
        viewToChange.setText("Duel Game");

        super.onCreate(savedInstanceState);
        //Get the intent that started this activity and extract the string
        Log.d(TAG, "DuelGame, onCreate method. Creating an intent");
        Intent intent = getIntent();
        player = (Player) intent.getSerializableExtra("player");

        setPlayer(this.player);
        activateMusic();
        startThread();

        if(mpDuel.isPlaying()) {
            mpDuel.stop();
        }
        try {
            AssetFileDescriptor afd;
                afd = getAssets().openFd("rushing.mp3");
            mpDuel.reset();
            mpDuel.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mpDuel.prepare();
            mpDuel.start();
            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        closeReceiveThread();
        super.onDestroy();
    }
}
