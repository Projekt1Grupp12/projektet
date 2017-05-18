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

public class TrafficGame extends Game implements View.OnClickListener{

    private static final String TAG = "debugTraffic";
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.game_main_layout);
        final TextView viewToChange = (TextView) findViewById(R.id.textPuzzleGame);
        viewToChange.setText("Traffic Game");

        super.onCreate(savedInstanceState);
        //Get the intent that started this activity and extract the string
        Log.d(TAG, "TrafficGame, onCreate method. Creating an intent");
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
