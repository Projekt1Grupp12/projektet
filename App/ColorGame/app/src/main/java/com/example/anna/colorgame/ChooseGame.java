package com.example.anna.colorgame;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ChooseGame extends AppCompatActivity {
    private static final String TAG = "debug";
    private static String ip = "";
    private static String name = "";
    private static String userID = "";
    private ProgressDialog pd;
    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to get result from Asynctask and store it in userID variable.
        And at last sendMessageToNextActivity method is called.
         */
        @Override
        public void postResult(String result) {
            Log.d(TAG, "RESULTAT FRÅN SERVER " + result);
            pd.dismiss();
            startActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game);

        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        Log.i(TAG, "onCreate()");
        ip = intent.getStringExtra("ip");
        name = intent.getStringExtra("name");
        userID = intent.getStringExtra("userid");
    }
    //This method is called when Puzzle button is clicked. It starts next Activity and sends data to it.
    public void startPuzzle(View view){
        startProgressDialog();
        startAsyncTask("join");
    }
    //This method is called when Traffic button is clicked. It starts next Activity and sends data to it.
    public void startTraffic(View vierw){
        Intent intent = new Intent(this, TrafficGame.class);
        intent.putExtra("ip", ip);
        intent.putExtra("name", name);
        intent.putExtra("userid", userID);
        startActivity(intent);
    }

    private void startProgressDialog(){
        pd = new ProgressDialog(ChooseGame.this);
        pd.setProgressStyle(pd.STYLE_SPINNER);
        pd.setMessage("Waiting for opponent...");
        pd.show();
    }

    public void startAsyncTask(String message) {//Button
        ConnectToServer runner = new ConnectToServer(ip, delegate);
        Log.d(TAG, "Task created");
        Log.d(TAG, "Execute task");
        runner.execute(message);
    }

    private void startActivity(){
        Intent intent = new Intent(this, PuzzleGame.class);
        intent.putExtra("ip", ip);
        intent.putExtra("name", name);
        intent.putExtra("userid", userID);
        startActivity(intent);
    }
}
