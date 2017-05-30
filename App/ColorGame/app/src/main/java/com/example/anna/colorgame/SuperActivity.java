package com.example.anna.colorgame;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * This class is a super class for MainFrame, MainMenu, ChooseGame and HighScore activities.
 * It has a set of variables and methods used in those activities.
 */
public class SuperActivity extends AppCompatActivity {
    private static final String TAG = "debug SuperActivity";
    private ReceiveDataThread receiveDataThread;
    private Thread thread;
    private Player player = new Player(null, null, null);
    private ProgressDialog pd;
    private AlertDialogClass alertDialog;

    /**
     * This method returns an instance of AlertDialogClass stored in SuperActivity.
     *
     * @return AlertDialogClass
     */
    public AlertDialogClass getAlertDialog() {
        return alertDialog;
    }

    /**
     * This method initiates AlertDialogClass instance with specified value.
     *
     * @param alertDialog AlertDialogClass
     */
    public void setAlertDialog(AlertDialogClass alertDialog) {
        this.alertDialog = alertDialog;
    }

    /**
     * This method returns an instance of a Thread that is used in SuperActivity.
     *
     * @return Thread
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * This method initiates Thread instance with specified value.
     *
     * @param thread Thread
     */
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    /**
     * This method returns an instance of Player class used in SuperActivity.
     *
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * This method initiates Player class instance with specified value.
     *
     * @param player Player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * This method creates new AsyncTask and sends specified data to server.
     *
     * @param data String
     * @param player Player
     * @param delegate AsyncResponse
     */
    public void sendDataToServer(String data, Player player, AsyncResponse delegate) {
        ConnectToServer runner = new ConnectToServer(player.getChoosenIP(), delegate);
        System.gc();
        runner.execute(data);
    }

    /**
     * This method creates an Intent and start specified activity as well as sending specified data
     * to next activity.
     *
     * @param player Player
     * @param context Context
     * @param nextClass Class
     */
    public void startNextActivity(Player player, Context context, Class nextClass) {
        Intent intent = new Intent(context, nextClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("player", player);
        Log.d(TAG, "Starting new Activity");
        context.startActivity(intent);
    }

    /**
     * This method validates IP address in EditText field. It is called when focus state of
     * editIPText is changed. If IP is correct return true and if not return false
     *
     * @param ip String
     * @return boolean
     */
    public boolean validateIP(String ip) {
        Log.d(TAG, "validateIP " + ip);
        int digit;
        String[] parts = ip.split("\\.");
        if (ip == null || ip.isEmpty() || parts.length != 4 || ip.endsWith(".")) {
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
     * This method validates name in EditText field. It is called when user pushes "Login" button.
     * If edit field is empty or if name is more than 20 character long the input is incorrect and
     * method will return false, else true.
     *
     * @param name String
     * @return boolean
     */
    public boolean validateName(String name) {
        Log.d(TAG, "validateName " + name);
        if (name.length() > 20 || name.length() < 1 || name.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * This method creates new ProgressDialog with specified message.
     * Returns created ProgressDialog.
     *
     * @param message String
     * @param context Context
     */
    public void startProgressDialog(String message, Context context) {
        pd = new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setProgressStyle(pd.STYLE_SPINNER);
        pd.setMessage(message);
        pd.show();
    }

    /**
     * This method is used to determine if ProgressDialog is showing or not.
     *
     * @return boolean
     */
    public boolean isShowingProgressDialog() {
        try{
            return pd.isShowing();
        }catch(NullPointerException e){
            Log.d(TAG, e.toString());
            return false;
        }
    }

    /**
     * This method closes ProgressDialog.
     */
    public void closeProgressDialog() {
        pd.dismiss();
    }

    /**
     * This method is used to start a thread in MainMenu that listens for incoming data
     * and updates UI if needed.
     *
     * @param context Context
     * @param player Player
     * @param mainMenu MainMenu
     */
    public void startThreadMainMenu(Context context, Player player, MainMenu mainMenu) {
        receiveDataThread = new ReceiveDataThread(context, player, mainMenu);
        thread = new Thread(receiveDataThread);
        thread.start();

    }

    /**
     * This method is used to start a thread in ChooseGame that listens for incoming data
     * and updates UI if needed.
     *
     * @param context Context
     * @param player Player
     * @param chooseGame ChooseGame class
     */
    public void startThreadChooseGame(Context context, Player player, ChooseGame chooseGame) {
        receiveDataThread = new ReceiveDataThread(context, player, chooseGame);
        thread = new Thread(receiveDataThread);
        thread.start();

    }

    /**
     * This method is used to close thread in any activity. It stops while loop and interrupts
     * thread.
     */
    public void closeThread() {//this method closes socket, ends run method and stops the thread
        receiveDataThread.setIsRunning(false);
        thread.interrupt();
    }

}
