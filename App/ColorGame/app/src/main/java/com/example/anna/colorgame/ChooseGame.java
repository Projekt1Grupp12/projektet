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
    private RecieveDataThread recieveDataThread;
    private Thread thread;
    private boolean start = false;

    private AsyncResponse delegate = new AsyncResponse() {
        /*
        Method that has result from AsyncTask as parameter.
        It is used to get result from Asynctask and store it in userID variable.
        And at last sendMessageToNextActivity method is called.
         */
        @Override
        public void postResult(String result) {
            Log.d(TAG, "RESULTAT FRÅN SERVER: " + result);
            if(result.contains("ok")){
                //do nothing
            }


          /*  if (result.contains("start")){
                pd.dismiss();
                startActivity(startThisClass);
            } else if(result.contains("SocketTimeoutException")){
                startAsyncTask("timeout;0");
            } else if(result.contains("ok")){
                pd.dismiss();
                startThisClass = MainMenu.class;
                startActivity(startThisClass);
            }*/
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game);
        Log.i(TAG, "onCreate().ChooseGame");
        Intent intent = getIntent();
        player = (Player)intent.getSerializableExtra("player");

        recieveDataThread = new RecieveDataThread(this, player);//added thread 05-16
        thread = new Thread(recieveDataThread);
        thread.start();
    }
    @Override
    protected void onDestroy() {//added 05-16 for thread
        Log.d(TAG, "onDestroy() ");
        recieveDataThread.closeSocket();
        recieveDataThread.setIsRunning(false);
        thread.interrupt();
        finish();//do we need this method?
        super.onDestroy();
    }
    /**
     * This method is called when Puzzle button is clicked. It starts next Activity and sends data to it.
     * @param view
     */
    public void startPuzzle(View view){
        startProgressDialog();
        this.startThisClass = PuzzleGame.class;
        startAsyncTask("Puzzle Game;0");//sending message to server
        while(!start){
            //do nothing while start=false
        }
        pd.dismiss();//when start=true, start next activity
        startActivity(startThisClass);
    } //Button
    public void startTraffic(View view){
        startProgressDialog();
        this.startThisClass = TrafficGame.class;
        startAsyncTask("Traffic Game;0");
    } //Button
    public void startDuel(View view){
        startProgressDialog();
        this.startThisClass = DuelGame.class;
        startAsyncTask("Duel Game;0");
    } //Button
    private void startProgressDialog(){
        pd = new ProgressDialog(ChooseGame.this);
        pd.setProgressStyle(pd.STYLE_SPINNER);
        pd.setMessage("Waiting for opponent...");
        pd.show();
    }
    public void setStart(boolean start){this.start=start;}
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
