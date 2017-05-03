package com.example.anna.colorgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
/*
Class is an activity that shows main menu of the application.
It has three TextView to show data.
 */
public class MainMenu extends AppCompatActivity {
    private static final String TAG = "debug";
    private Player player;
    private Button chooseGameBtn;
    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to get result from Asynctask and store it in userID variable.
        And at last sendMessageToNextActivity method is called.
         */
        @Override
        public void postResult(String result) {
            Log.d(TAG, "RESULTAT FRÅN SERVER " + result);
               chooseGameBtn.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        Log.i(TAG, "onCreate()");
        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        player = (Player)intent.getSerializableExtra("player");

        //Text on the Button change depending on userID
        chooseGameBtn = (Button) findViewById(R.id.choose_game_button);
        if(player.getUserID().equals("0")){
            chooseGameBtn.setText("Choose Game");
        }else if(player.getUserID().equals("1")){
            chooseGameBtn.setText("Join Game");
            chooseGameBtn.setEnabled(false);
            startAsyncTask("join");
        }
    }
    /*
       This method is called when button "Choose Game" is clicked.
       It starts next activity and sends data to it using Intent class.
       */
    public void chooseGame(View view){
        Log.d(TAG, "Button Choose Game is clicked");
        Intent intent = null;
        //There is two different ways to go depending on userID
        if (player.getUserID().equals("0")) {
            Log.d(TAG, "Inside if satsen");
            intent = new Intent(this, ChooseGame.class);
        } else if (player.getUserID().equals("1")) {
            intent = new Intent(this, PuzzleGame.class);
        }
        Log.d(TAG, "Outside if satsen");
        intent.putExtra("player", player);
        startActivity(intent);
    }
    public void showHighScore(View view) {}

    public void startAsyncTask(String message) {//Button
        ConnectToServer runner = new ConnectToServer(player.getChoosenIP(), delegate);
        Log.d(TAG, "Task created");
        Log.d(TAG, "Execute task");
        runner.execute(message);
    }
}
