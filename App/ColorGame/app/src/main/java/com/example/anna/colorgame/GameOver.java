package com.example.anna.colorgame;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

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
    }

    /**
     * Prepares the alertDialogClass to ask the player if they want
     * to play again or not.
     */
    private void playAgainMessage() {
        alertDialog = new AlertDialogClass(context);
        alertDialog.setPlayer(player);
        alertDialog.setTitle(playAgainTitle);
        alertDialog.setMessage(playAgain);
        alertDialog.ButtonYesNo();
    }

}
