package com.example.anna.colorgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by George on 2017-05-18.
 */

public class DeathMatchGame extends Game{
    private static final String TAG = "debugPuzzleGame";
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameMainLayout);
        final TextView viewToChange = (TextView) findViewById(R.id.textPuzzleGame);
        viewToChange.setText("Deathmatch Game");

        //Get the intent that started this activity and extract the string
        Log.d(TAG, "PuzzleGame, onCreate method. Creating an intent");
        Intent intent = getIntent();
        player = (Player) intent.getSerializableExtra("player");
        setPlayer(this.player);
        activateMusic();
        startThread();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        closeReceiveThread();
        super.onDestroy();
    }
}
