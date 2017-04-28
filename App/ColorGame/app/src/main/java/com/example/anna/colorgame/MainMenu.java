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
    private static String ip = "";
    private static String name = "";
    private static String userID = "";
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

        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        Log.i(TAG, "onCreate()");
        ip = intent.getStringExtra("ip");
        name = intent.getStringExtra("name");
        userID = intent.getStringExtra("userid");

        //Text on the Button change depending on userID
        chooseGameBtn = (Button) findViewById(R.id.choose_game_button);
        if(userID.equals("0")){
            chooseGameBtn.setText("Choose Game");
        }else if(userID.equals("1")){
            chooseGameBtn.setText("Join Game");
            chooseGameBtn.setEnabled(false);
        }
        startAsyncTask("join");

        saveInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
        saveInfo();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
        loadInfo();
    }

	 //Save the users login info
    public void saveInfo(){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ip", ip);
        editor.putString("name", name);
        editor.putString("userid", userID);
        editor.apply();
        Log.i(TAG , "saveInfo");
        Log.i(TAG +" ip", ip);
    }

    //Load the users login info
    public void loadInfo(){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.i(TAG , "loadInfo");
        this.ip = sharedPref.getString("ip", "");
        this.name = sharedPref.getString("name", "");
        this.userID = sharedPref.getString("userid", "");
    }

    /*
    This method is called when button is clicked.
    It starts next activity and sends data to it using Intent class.
    */
    public void showHighScore(View view) {//Button Choose Game
        Log.d(TAG, "Inside startGameClicked");
        Intent intent = null;
        //There is two different ways to go depending on userID
        if (userID.equals("0")) {
            Log.d(TAG, "Inside if satsen");
            intent = new Intent(this, ChooseGame.class);
        } else if (userID.equals("1")) {
            intent = new Intent(this, PuzzleGame.class);
        }
        Log.d(TAG, "Outside if satsen");
        intent.putExtra("ip", ip);
        intent.putExtra("name", name);
        intent.putExtra("userid", userID);
        startActivity(intent);
    }

    public void chooseGame(View view){
        Log.d(TAG, "Inside startGameClicked");
        Intent intent = null;
        //There is two different ways to go depending on userID
        if (userID.equals("0")) {
            Log.d(TAG, "Inside if satsen");
            intent = new Intent(this, ChooseGame.class);
        } else if (userID.equals("1")) {
            intent = new Intent(this, PuzzleGame.class);
        }
        Log.d(TAG, "Outside if satsen");
        intent.putExtra("ip", ip);
        intent.putExtra("name", name);
        intent.putExtra("userid", userID);
        startActivity(intent);
    }

    public void startAsyncTask(String message) {//Button
        ConnectToServer runner = new ConnectToServer(ip, delegate);
        Log.d(TAG, "Task created");
        Log.d(TAG, "Execute task");
        runner.execute(message);
    }
}
