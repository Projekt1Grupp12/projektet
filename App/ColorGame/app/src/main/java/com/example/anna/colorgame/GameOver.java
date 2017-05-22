package com.example.anna.colorgame;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by George on 2017-04-28.
 */

public class GameOver extends AppCompatActivity {

    private String TAG = "GameOverDebug";

    //The alertDialog
    private String message = null;// message to show in AlertDialog window

    //Player and the new intents
    private Player player;
    private AlertDialogClass alertDialog = null;
    private Context context = null;
    private String dataMessage = null;// "Give Up", "logout", "LOSE!", "WIN!"

    public GameOver(Context context, Player player, String dataMessage){
        this.context = context;
        this.player = new Player(player.getName(), player.getUserID(), player.getChoosenIP());
        this.dataMessage = dataMessage;
        setTextMessageOfAlertDialog(dataMessage);
        displayMessage();
    }

    private void setTextMessageOfAlertDialog(String message){
        switch(message){
            case "WIN!":
                setTextWinner();
                break;
            case "LOSE!":
                setTextLoser();
                break;
            case "logout":
                setTextLogout();
                break;
            case "Give Up":
                setTextGiveUp();
                break;
        }
    }

    private void setTextWinner(){
        this.message = "You are the winner!";
    }
    private void setTextLoser(){ this.message = "Epic fail!"; }
    private void setTextGiveUp(){ this.message = "Game is interrupted." +"\nProgress will be lost";}
    private void setTextLogout(){ this.message = "Game is interrupted." + "\nYou will be logged out";}

    /**
     * Prepares the alertDialog to display if player has won or lost the game
     */
    public void displayMessage(){
        alertDialog = new AlertDialogClass(context, dataMessage);
        alertDialog.setTextToShowInAlertDialog(message);
        alertDialog.setPlayer(player);
        alertDialog.ButtonOK();
    }
}
