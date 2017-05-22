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
    private MediaPlayer mpTraffic = null;

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

        this.mpTraffic = MediaPlayer.create(this, R.raw.ghostmachine);
        try {
            AssetFileDescriptor afd;
            afd = getAssets().openFd("ghostmachine.mp3");
            mpTraffic.reset();
            mpTraffic.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mpTraffic.setLooping(true);
            mpTraffic.prepare();
            mpTraffic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mpTraffic.stop();
        closeReceiveThread();
        super.onDestroy();
    }

}
