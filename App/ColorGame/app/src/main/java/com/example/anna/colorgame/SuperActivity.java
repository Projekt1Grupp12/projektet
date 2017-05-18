package com.example.anna.colorgame;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Lenovo on 2017-05-18.
 */

public class SuperActivity extends AppCompatActivity {
    private static final String TAG = "debug SuperActivity";
    private RecieveDataThread recieveDataThread;
    private Thread thread;
    private Player player = new Player(null, null, null);
    private ProgressDialog pd;
    private Class startNextActivityClass = null;
    private AlertDialogClass alertDialog;

    public AlertDialogClass getAlertDialog() {
        return alertDialog;
    }

    public void setAlertDialog(AlertDialogClass alertDialog) {
        this.alertDialog = alertDialog;
    }

    public Class getStartNextActivityClass() {
        return startNextActivityClass;
    }

    public void setStartNextActivityClass(Class startNextActivityClass) {
        this.startNextActivityClass = startNextActivityClass;
    }

    public RecieveDataThread getRecieveDataThread() {
        return recieveDataThread;
    }

    public void setRecieveDataThread(RecieveDataThread recieveDataThread) {
        this.recieveDataThread = recieveDataThread;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ProgressDialog getPd() {
        return pd;
    }

    public void setPd(ProgressDialog pd) {
        this.pd = pd;
    }

    /**
     * This method creates new AsyncTask and sends specified message to server.
     * @param message
     * @param player
     * @param delegate
     */
    public void startAsyncTask(String message, Player player, AsyncResponse delegate) {
        ConnectToServer runner = new ConnectToServer(player.getChoosenIP(), delegate);
        System.gc();
        runner.execute(message);
    }

    /**
     * This method creates an Intent and start specified activity aswell as sending specified data
     * to next activity.
     * @param player
     * @param context
     */
    public void startNextActivity(Player player, Context context, Class nextClass){
        Intent intent = new Intent(context, nextClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("player", player);
        Log.d(TAG, "Starting new Activity");
        context.startActivity(intent);
    }

    /**
     * This method validates Ip address. It is called when focus state of editIPText is changed.
     * If IP is correct return true and if not return false
     * @param str
     * @return
     */
    public boolean validateIP(String str) {
        Log.d(TAG, "validateIP " + str);
        int digit = -1;
        String[] parts = str.split("\\.");
        if (str == null || str.isEmpty() || parts.length != 4 || str.endsWith(".")) {
            return false;
        } else {
            for (String s : parts) {
                try {
                    digit = Integer.parseInt(s);
                } catch (NumberFormatException n) {
                    return false;
                }
                if ((digit < 0) || (digit > 255)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method validates name in the namefield.
     * Returns true if name is correct, otherwise false.
     * @param str
     * @return
     */
    public boolean validateName(String str) {
        Log.d(TAG, "validateName " + str);
        if (str.length() > 20 || str.length() < 1 || str.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * This method creates new ProgressDialog with specified message.
     * Returns created ProgressDialog.
     * @param message
     * @param context
     * @return
     */
    public void startProgressDialog(String message, Context context){
        pd = new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setProgressStyle(pd.STYLE_SPINNER);
        pd.setMessage(message);
        pd.show();
    }

    public void closeProgressDialog(){
        pd.dismiss();
    }

    public Thread startThreadMainMenu(Context context, Player player, MainMenu mainMenu){
        recieveDataThread = new RecieveDataThread(context, player, mainMenu);
        thread = new Thread(recieveDataThread);
        thread.start();
        return thread;
    }

    public Thread startThreadChooseGame(Context context, Player player, ChooseGame chooseGame){
        recieveDataThread = new RecieveDataThread(context, player, chooseGame);
        thread = new Thread(recieveDataThread);
        thread.start();
        return thread;
    }

    public void closeReceiveThread(){//this method closes socket, ends run method and stops the thread
        recieveDataThread.setIsRunning(false);
        thread.interrupt();
    }

    public void showAlertDialog(String title, String message){
        alertDialog.setTitleMessage(title, message);
        alertDialog.ButtonOK();
    }

}
