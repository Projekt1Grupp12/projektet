package com.example.anna.colorgame;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ChooseGame extends AppCompatActivity {
    private static final String TAG = "debugChoose";
    private Player player;
    private ProgressDialog pd;
    private Class startThisClass = null;

    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to get result from Asynctask and store it in userID variable.
        And at last sendMessageToNextActivity method is called.
         */
        @Override
        public void postResult(String result) {
            Log.d(TAG, "RESULTAT FRÅN SERVER: " + result);

            if (result.contains("start")){
                pd.dismiss();
                startActivity(startThisClass);
            } else if(result.contains("SocketTimeoutException")){
                startAsyncTask("timeout;0");
            } else if(result.contains("ok")){
                pd.dismiss();
                startThisClass = MainMenu.class;
                startActivity(startThisClass);
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game);
        Log.i(TAG, "onCreate().ChooseGame");
        //Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        player = (Player)intent.getSerializableExtra("player");
    }
    //This method is called when Puzzle button is clicked. It starts next Activity and sends data to it.
    public void startPuzzle(View view){
        startProgressDialog();
        this.startThisClass = PuzzleGame.class;
        startAsyncTask("Puzzle Game;0");
    }
    //This method is called when Traffic button is clicked. It starts next Activity and sends data to it.
    public void startTraffic(View view){
        startProgressDialog();
        this.startThisClass = TrafficGame.class;
        startAsyncTask("Traffic Game;0");
    }
    public void startDuel(View view){
        startProgressDialog();
        this.startThisClass = DuelGame.class;
        startAsyncTask("Duel Game;0");
    }

    private void startProgressDialog(){
        pd = new ProgressDialog(ChooseGame.this);
        pd.setProgressStyle(pd.STYLE_SPINNER);
        pd.setMessage("Waiting for opponent...");
        pd.show();
    }

    public void startAsyncTask(String message) {//Button
        ConnectToServer runner = new ConnectToServer(player.getChoosenIP(), delegate);
        Log.d(TAG, "Task created");
        Log.d(TAG, "Execute task");
        runner.execute(message);
    }

    private void startActivity(Class startThisClass){
        Intent intent = new Intent(this, startThisClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("player", player);
        startActivity(intent);
    }
}
