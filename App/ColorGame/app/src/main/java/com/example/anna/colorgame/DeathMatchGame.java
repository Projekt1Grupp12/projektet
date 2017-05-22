package com.example.anna.colorgame;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by George on 2017-05-18.
 */

public class DeathmatchGame extends Game{
    private static final String TAG = "debugPuzzleGame";
    private Player player;
    private MediaPlayer mpDeathmatch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main_layout);
        final TextView viewToChange = (TextView) findViewById(R.id.textPuzzleGame);
        viewToChange.setText("Deathmatch Game");

        //Get the intent that started this activity and extract the string
        Log.d(TAG, "DeathmatchGame, onCreate method. Creating an intent");
        Intent intent = getIntent();
        player = (Player) intent.getSerializableExtra("player");
        setPlayer(this.player);
        activateMusic();
        startThread();
        this.mpDeathmatch = MediaPlayer.create(this, R.raw.amongstcrystallight);
        try {
            AssetFileDescriptor afd;
            afd = getAssets().openFd("amongstcrystallight.mp3");
            mpDeathmatch.reset();
            mpDeathmatch.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mpDeathmatch.setLooping(true);
            mpDeathmatch.prepare();
            mpDeathmatch.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mpDeathmatch.stop();
        closeReceiveThread();
        super.onDestroy();
    }
}
