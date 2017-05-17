package com.example.anna.colorgame;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by George on 2017-04-28.
 */

public class GameOver extends AppCompatActivity {

    private String TAG = "GameOverDebug";

    //The alertDialog
    private String text = null;// text to show in AlertDialog window

    //Player and the new intents
    private Player player;
    private AlertDialogClass alertDialog = null;
    private Context context = null;
    private String message = null;// "Give Up", "logoutok", "LOSE!", "WIN!"

    public GameOver(Context context, Player player, String message){
        this.context = context;
        this.player = new Player(player.getName(), player.getUserID(), player.getChoosenIP());
        this.message = message;
        setTextMessageOfAlertDialog(message);
        displayMessage();
    }

    private void setTextMessageOfAlertDialog(String message){
        switch(message){
            case "WIN!":
                setTextWinner();
                playAgainMessage();
                break;
            case "LOSE!":
                setTextLoser();
                playAgainMessage();
                break;
            case "logoutok":
                setTextLogout();
                break;
            case "Give Up":
                setTextGiveUp();
                break;
        }

     /*   if (result.contains("WIN")) {
            setTextWinner();
            playAgainMessage();
        } else if (result.contains("LOS")) {
            setTextLoser();
            playAgainMessage();
        }else if(result.contains("Give Up")){
            setTextGiveUp();
        }*/
    }

    private void setTextWinner(){
        this.text = "You are the winner!";
    }
    private void setTextLoser(){ this.text = "Epic fail!"; }
    private void setTextGiveUp(){ this.text = "Game is interrupted." +"\nProgress will be lost";}
    private void setTextLogout(){ this.text = "Game is interrupted." + "\nYou will be logged out";}

    /**
     * Prepares the alertDialog to display if player has won or lost the game
     */
    public void displayMessage(){
        alertDialog = new AlertDialogClass(context, message);
        alertDialog.setTextToShowInAlertDialog(text);
        alertDialog.setPlayer(player);
        alertDialog.ButtonOK();
    }

    /**
     * Prepares the alertDialogClass to ask the player if they want
     * to play again or not.
     */
    private void playAgainMessage() {
        alertDialog = new AlertDialogClass(context, "New game?", " ");
        alertDialog.setTextToShowInAlertDialog("Do you want to play again?");
        alertDialog.setPlayer(player);
        alertDialog.ButtonYesNo();
    }

}
