package com.example.anna.colorgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DuelGame extends Game implements View.OnClickListener{

    private static final String TAG = "debugDuel";
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.gameMainLayout);
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
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        closeReceiveThread();
        super.onDestroy();
    }
}
