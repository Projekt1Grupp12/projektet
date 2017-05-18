package com.example.anna.colorgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ChooseGame extends SuperActivity implements View.OnClickListener {
    private static final String TAG = "debugChoose";
    private static final String SEMICOLON = ";";
    private Class startNextActivityClass = null;
    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to get result from Asynctask and store it in userID variable.
        And at last sendMessageToNextActivity method is called.
         */
        @Override
        public void postResult(String result) {
            Log.d(TAG, "RESULTAT FRÅN SERVER: " + result);
            if (result.contains("SocketTimeoutException")) {
                closeProgressDialog();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game);

        Log.i(TAG, "onCreate().ChooseGame");
        Intent intent = getIntent();
        setPlayer((Player) intent.getSerializableExtra("player"));
        startThreadChooseGame(this, getPlayer(), ChooseGame.this);
    }

    @Override
    protected void onStop() {//added 05-16 for thread
        Log.d(TAG, "onDestroy() ");
        closeReceiveThread();
        super.onStop();
    }

    /*
    This method is called when one of the buttons is clicked in the GUI.
    Which button is pushed is stored in data variable.
    Then the semicolon and userID is added to data string and sent to startAsyncTask method.
     */
    @Override
    public void onClick(View view) {
        startProgressDialog("Waiting for opponent...", this);
        Log.d(TAG, "onClick()-metod");
        String data = " ";
        switch (view.getId()) {
            case R.id.buttonPuzzle:
                this.startNextActivityClass = PuzzleGame.class;
                data = "PuzzleGame";
                break;
            case R.id.buttonTraffic:
                this.startNextActivityClass = TrafficGame.class;
                data = "TrafficGame";
                break;
            case R.id.buttonDuel:
                this.startNextActivityClass = DuelGame.class;
                data = "DuelGame";
                break;
            case R.id.buttonDeathmatch:
                this.startThisClass = DeathMatchGame.class;
                data = "DeathmatchGame";
                break;
        }
        startAsyncTask(data + SEMICOLON + getPlayer().getUserID(), this.getPlayer(), delegate);//using new class. need delegate
    }

    public void startGame() {
        closeProgressDialog();//when start=true, start next activity
        startNextActivity(getPlayer(), this, startNextActivityClass);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startAsyncTask("logout" + ";" + getPlayer().getUserID(), this.getPlayer(), delegate);//using new class. don't need delegate
        startNextActivity(getPlayer(), this, MainMenu.class);
    }
}