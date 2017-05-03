package com.example.anna.colorgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.style.UpdateAppearance;
import android.text.style.UpdateLayout;
import android.util.Log;
import java.util.concurrent.TimeUnit;

/**
 * Created by George on 2017-04-28.
 */

public class GameOver extends AppCompatActivity {

    private String TAG = "GameOverDebug";

    //The alertDialog
    private String message = null;
    private String playAgainTitle = "New game?";
    private String playAgain = "Do you want to play again?";

    //Player and the new intents
    private Player player;
    private Intent intentNewGame =  new Intent(this, MainMenu.class);
    private Intent intentPlayAgain=  new Intent(this, ChooseGame.class);
    private AlertDialogClass alertDialog = null;
    private Context context = null;

    public GameOver(Context context, Player player, String result){
        this.context = context;
        this.player = new Player(player.getName(), player.getUserID(), player.getChoosenIP());

        setResultMessage(result);

        playAgainMessage();

        displayMessage();
    }

    private void setResultMessage(String result){
        if (result.contains("WIN")) {
            setMessageWinner();
        } else if (result.contains("LOS")) {
            setMessageLoser();
        }
    }

    private void setMessageWinner(){
        this.message = "You are the winner!";
    }
    private void setMessageLoser() { this.message = "Epic fail!"; }

    /**
     * Prepares the alertDialog to display if player has won or lost the game
     */
    public void displayMessage(){
        alertDialog = new AlertDialogClass(context);
        alertDialog.setTitle("");
        alertDialog.setMessage(message);
        alertDialog.ButtonOK();

        Log.d(TAG, "show()");
        alertDialog.show();


    }

    /**
     * Prepares the alertDialogClass to ask the player if they want
     * to play again or not.
     */
    private void playAgainMessage(){
        String answer = "EMPTY";
        //Log.d(TAG, "getInput()");
       // alertDialog.dismiss();
        //answer = getInput();
        Log.d(TAG, "answerB: " + answer);
        Log.d(TAG, "m_Input: " + m_Input);


        alertDialog = new AlertDialogClass(context);
        answer = alertDialog.getAnswer();
        Log.d(TAG, "answerC: " + answer);

        alertDialog.setTitle(playAgainTitle);
        alertDialog.setMessage(playAgain);
        //customize alert dialog to allow desired input
        alertDialog.ButtonYesNo();
        alertDialog.show();


        /*if(answer.isEmpty())
            throw new RuntimeException("Subclasses of Valueables cannot take in an empty String or null value for the \"name\" constructor");
        if(answer.contains("YES")){
            sendMessageToNextActivity(intentPlayAgain, player);
        }
        else if(answer.contains("NO")){
            sendMessageToNextActivity(intentNewGame, player);
        }
        else
            Log.d(TAG, "answer: " + answer);*/
    }

    /*
   This method sends data(IP, Name, userID) to next activity using Intent class.
    */
    private void sendMessageToNextActivity(Intent intent, Player player) {
        Log.d(TAG, "Creating new intent and sending data");
        intent.putExtra("player", player);
        Log.d(TAG, "Starting new Activity");
        startActivity(intent);
    }

    public AlertDialogClass getAlertDialog(){
        return this.alertDialog;
    }


    String m_Input;

    public synchronized String getInput()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                //customize alert dialog to allow desired input
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        m_Input = String.valueOf(whichButton);
                        notify();
                    }
                });
                alert.show();
            }
        });

        try
        {
            wait();
        }
        catch (InterruptedException e)
        {
        }

        return m_Input;
    }



    /*public synchronized String getInput()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialogClass alertDialog = new AlertDialogClass(context);
                alertDialog = new AlertDialogClass(context);
                alertDialog.setTitle(playAgainTitle);
                alertDialog.setMessage(playAgain);
                //customize alert dialog to allow desired input
                alertDialog.ButtonYesNo();
                alertDialog.show();
            }
        });
        try{
            wait();
        }
        catch (InterruptedException e){}
        return alertDialog.getAnswer();
    }*/

}
