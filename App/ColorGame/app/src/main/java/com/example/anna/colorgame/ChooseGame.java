package com.example.anna.colorgame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.TimeUnit;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ChooseGame extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "debugChoose";
    private Player player;
    private ProgressDialog pd;
    private Class startThisClass = null;
    private RecieveDataThread recieveDataThread;
    private Thread thread;
    private boolean start = false;
    private static final String SEMICOLON = ";";
    private Button [] myButtons;

    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to get result from Asynctask and store it in userID variable.
        And at last sendMessageToNextActivity method is called.
         */
        @Override
        public void postResult(String result) {
            Log.d(TAG, "RESULTAT FRÅN SERVER: " + result);
            if(result.contains("SocketTimeoutException")) {
                pd.dismiss();
            }
            if(result.contains(";")){
                for(int i=0; i<result.split(";").length; i++){
                    myButtons[i] = new Button(getApplicationContext());
                    myButtons[i].setText(result.split(";")[i]);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //getgames
        startAsyncTask("getgames");

        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.layout_ChooseGame);
        for(int i = 0; i<myButtons.length; i++){
            cl.addView(myButtons[i]);
        }


        setContentView(cl);
        Log.i(TAG, "onCreate().ChooseGame");
        Intent intent = getIntent();
        player = (Player)intent.getSerializableExtra("player");

        recieveDataThread = new RecieveDataThread(this, player, this);//added thread 05-16
        thread = new Thread(recieveDataThread);
        thread.start();
    }
    @Override
    protected void onStop() {//added 05-16 for thread
        Log.d(TAG, "onDestroy() ");
        recieveDataThread.setIsRunning(false);
        thread.interrupt();
        super.onStop();
    }

    private void startProgressDialog(){
        pd = new ProgressDialog(ChooseGame.this);
        pd.setCanceledOnTouchOutside(false);
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


    /*
    This method is called when one of the buttons is clicked in the GUI.
    Which button is pushed is stored in data variable.
    Then the semicolon and userID is added to data string and sent to startAsyncTask method.
     */
    @Override
    public void onClick(View view) {
       startProgressDialog();
        Log.d(TAG, "onClick()-metod");
        String data = " ";
        switch(view.getId()){
            case R.id.buttonPuzzle:
                this.startThisClass = PuzzleGame.class;
                data = "PuzzleGame";
                break;
            case R.id.buttonTraffic:
                this.startThisClass = TrafficGame.class;
                data = "TrafficGame";
                break;
            case R.id.buttonDuel:
                this.startThisClass = DuelGame.class;
                data = "DuelGame";
                break;
        }
        data += SEMICOLON + player.getUserID();
        Log.d(TAG, "onClick-method data: " + String.valueOf(data));
        startAsyncTask(data);
    }

    public void startGame() {
        Intent intent = null;
        pd.dismiss();//when start=true, start next activity
        intent = new Intent(this, startThisClass);
        intent.putExtra("player", player);
        startActivity(intent);
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
        String message = "logout" + ";" + player.getUserID();
        ConnectToServer connectToServer = new ConnectToServer(player);
        Log.d(TAG, "connectToServer is created " + player.getName());
        System.gc();
        connectToServer.execute(message);

        Log.d(TAG, "Creating new intent and sending data");

        Intent intent = new Intent(this, MainMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("player", player);
        Log.d(TAG, "Starting new Activity");
        startActivity(intent);
    }

}